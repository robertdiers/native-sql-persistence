package com.twodigits.nativesqlpersistence;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.twodigits.nativesqlpersistence.connectionpool.ConnectionPoolManager;
import com.twodigits.nativesqlpersistence.connectionpool.DBConnection;

/***
 * Simple Database access with separate connection handling
 * SHOULD BE USED AS SINGLETON OR DataSource IN WEB APPLICATIONS
 * 
 * @author Robert Diers
 */
public class DatabaseAccessor implements DataSource {	
	
	public static String SINGLE_QUOTE="'";
	
	//Flags
	private boolean useServerPool = true;
	private boolean isXA = false;
	
	//Logger
	private DatabaseLogger printer = null;
	
	//DataSource for JNDI access
	private String jndi = ""; //for log only
	private DataSource dataSource = null;
	
	//ConnectionPoolManager for internal connection pool
	private ConnectionPoolManager manager = null;
	private boolean working_flag = false;
	
	//Data for native Connection
	private String url = "";
	private String userid = "";
	private String password = "";
	private String driver = "";
	private boolean ignoreCommentMinusMinus = false;
	private boolean ignoreBlockComment = false;
	
	//connection object
	private Connection connectionForStatementsWithoutCommit = null;	
	
	/***
	 * Constructor with JNDI name
	 * ATTENTION: please use prepared methods to avoid SQL injection vulnerability
	 * @throws NamingException
	 */
	public DatabaseAccessor(String jndiname) throws NamingException
	{
		this(jndiname, false);
	}
	
	/***
	 * Constructor with JNDI name
	 * ATTENTION: please use prepared methods to avoid SQL injection vulnerability
	 * @throws NamingException
	 */
	public DatabaseAccessor(String jndiname, boolean isXADataSource) throws NamingException
	{		
		//Flags
		this.isXA = isXADataSource;
		this.useServerPool = true;		
		this.jndi = jndiname;
		//Datasource initialize
		InitialContext initCtx = null;
	    try {
	        initCtx = new InitialContext();
	        this.dataSource = (DataSource)initCtx.lookup(jndiname);	            
	    } catch (NamingException e) {			
			logError(e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
	         if ( initCtx != null ) {
	              initCtx.close();
	         }
	    }
	}	
	
	/***
	 * Constructor with driver name
	 * ATTENTION: please use prepared methods to avoid SQL injection vulnerability
	 * @throws NamingException
	 */
	public DatabaseAccessor(String url, String userid, String password, String driver)
	{		
		this(url, userid, password, driver, false);
	}	
	
	/***
	 * Constructor with driver name
	 * ATTENTION: please use prepared methods to avoid SQL injection vulnerability
	 * @throws NamingException
	 */
	public DatabaseAccessor(String url, String userid, String password, String driver, boolean isXADataSource)
	{		
		//Flags	
		this.isXA = isXADataSource;
		this.useServerPool = false;
		//Data
		this.url = url;
		this.userid = userid;
		this.password = password;
		this.driver = driver;
	}	
	
	/**
	 * set IsXA
	 */
	public void setXA(boolean isXADriver) {
		this.isXA = isXADriver;
	}
	
	/***
	 * set DatabaseLogger for statement logging
	 * @param printstream
	 */
	public void setDatabaseLoggerForStatementLogging(DatabaseLogger printer_in)
	{
		this.printer = printer_in;
	}
	
	/**
	 * throw SQL exception
	 * @throws SQLException 
	 */
	public void throwSqlException(String message) throws SQLException {
		if (this.useServerPool) throw new SQLException(message + " (" + this.jndi + ")");
		else throw new SQLException(message + " (" + this.url + ")");
	}
	
	/**
	 * initialize ConnectionPoolManager
	 * @throws SQLException
	 */
	private synchronized void initializeCPM() throws SQLException {
		//initialize ConnectionPoolManager
		if (!this.working_flag) {
			this.working_flag = true;
			try {
				this.manager = new ConnectionPoolManager(url, userid, password, driver, printer);
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			} finally {
				this.working_flag = false;
			}
		} else {
			//wait for initialization of first process
			while (this.working_flag) {
				try {
					printer.logInfo("waiting for ConnectionPoolManager initialization");
					Thread.sleep(250);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * DO NOT USE FOR SERVER POOLS
	 * @return the timeout_last_used for internal pool
	 * @throws SQLException 
	 */
	public int getTimeoutLastUsedForInternalPool() throws SQLException {
		//initialized?
		if (this.manager == null) {
			this.initializeCPM();
		}
		return this.manager.getTimeout_last_used();
	}

	/**
	 * DO NOT USE FOR SERVER POOLS
	 * @param timeout_last_used for internal pool
	 * @throws SQLException 
	 */
	public void setTimeoutLastUsedForInternalPool(int timeout_last_used) throws SQLException {
		//initialized?
		if (this.manager == null) {
			this.initializeCPM();
		}
		this.manager.setTimeout_last_used(timeout_last_used);
	}

	/**
	 * DO NOT USE FOR SERVER POOLS
	 * @return the timeout_created for internal pool
	 * @throws SQLException 
	 */
	public int getTimeoutCreatedForInternalPool() throws SQLException {
		//initialized?
		if (this.manager == null) {
			this.initializeCPM();
		}
		return this.manager.getTimeout_created();
	}

	/**
	 * DO NOT USE FOR SERVER POOLS
	 * @param the timeout_created for internal pool
	 * @throws SQLException 
	 */
	public void setTimeoutCreatedForInternalPool(int timeout_created) throws SQLException {
		//initialized?
		if (this.manager == null) {
			this.initializeCPM();
		}
		this.manager.setTimeout_created(timeout_created);
	}	
	
	/***
	 * write to DatabaseLogger info
	 * @param message
	 */
	private void logInfo(String message)
	{
		if (this.printer != null)
		{			
			if (this.useServerPool) printer.logInfo("INFO: "+getClass().getSimpleName()+ ": " + message + " (" + this.jndi + ")");
			else printer.logInfo("INFO: "+getClass().getSimpleName()+": " + message + " (" + this.url + ")");
		}
	}	
	
	/***
	 * write to DatabaseLogger warn
	 * @param message
	 */
	private void logWarn(String message)
	{
		if (this.printer != null)
		{			
			if (this.useServerPool) printer.logWarn("WARN: "+getClass().getSimpleName()+": " + message + " (" + this.jndi + ")");
			else printer.logWarn("WARN: "+getClass().getSimpleName()+": " + message + " (" + this.url + ")");
		}
	}	
	
	/***
	 * write to DatabaseLogger error
	 * @param message
	 */
	private void logError(String message)
	{
		if (this.printer != null)
		{			
			if (this.useServerPool) printer.logError("ERROR: "+getClass().getSimpleName()+": " + message + " (" + this.jndi + ")");
			else printer.logError("ERROR: "+getClass().getSimpleName()+": " + message + " (" + this.url + ")");
		}
	}	
	
	/***
	 * get an connection
	 * please call close after working with it
	 * @return one connection, please close it by yourself !!!
	 * @throws SQLException
	 */
	@SuppressWarnings("resource")
	public Connection getConnection() throws SQLException
	{
		try {
			if (this.useServerPool) {
				//JNDI
				Connection con = this.dataSource.getConnection();
				while (con.isClosed())
				{
					logWarn(getClass().getName()+": connection closed, trying to get new one");				
					con = this.dataSource.getConnection();
				}
				return con;
			} else {
				//from internal pool
				return getInternalPoolConnection().getCon();
			}
		 } catch (SQLException e) {			
			logError(e.getMessage());
			e.printStackTrace();
			throw e;
		 }
	}	
	
	/**
	 * get DBConnection from internal pool
	 * @throws SQLException 
	 */
	private synchronized DBConnection getInternalPoolConnection() throws SQLException {
		//initialized?
		if (this.manager == null) {
			this.initializeCPM();
		}
		//connection
		return this.manager.getConnectionFromPool();
	}
	
	/**
	 * create statement
	 * @return
	 * @throws SQLException 
	 */
	private Statement createStatement(Connection connection, Integer statementType, Integer concurrencyType, Integer holdabilityType) throws SQLException {
		if (statementType != null && concurrencyType != null && holdabilityType != null) {
			return connection.createStatement(statementType.intValue(), concurrencyType.intValue(), holdabilityType.intValue());
		} else {
			return connection.createStatement();
		}
	}
	
	/**
	 * prepare statement
	 * @return
	 * @throws SQLException 
	 */
	private PreparedStatement prepareStatement(Connection connection, String sql, Integer statementType, Integer concurrencyType, Integer holdabilityType) throws SQLException {
		if (statementType != null && concurrencyType != null && holdabilityType != null) {
			return connection.prepareStatement(sql, statementType.intValue(), concurrencyType.intValue(), holdabilityType.intValue());
		} else {
			return connection.prepareStatement(sql);
		}
	}
	
	/***
	 * execute an SELECT statement
	 * ATTENTION: SQL-Injection vulnerable when using dynamic SQL
	 * @param sql
	 * @return ResultObject with columns and ResultRows
	 * @throws SQLException
	 */
	public ResultObject executeQuery(String sql) throws SQLException
	{		
		return executeQuery(sql, null, null, null);
	}	
	
	/***
	 * execute an SELECT statement
	 * ATTENTION: SQL-Injection vulnerable when using dynamic SQL
	 * @param sql
	 * @return ResultObject with columns and ResultRows
	 * @throws SQLException
	 */
	public ResultObject executeQuery(String sql, Integer statementType, Integer concurrencyType, Integer holdabilityType) throws SQLException
	{	
		//log and validation
		logInfo(sql);
		blockComments(sql);
		
		//do it
		ResultObject res = null;
		Statement stmt = null;
		ResultSet result = null;		
		
		Connection connection = null;
		DBConnection dbconnection = null;
		try {
			if (this.useServerPool) {
				connection = getConnection();
				stmt = createStatement(connection, statementType, concurrencyType, holdabilityType);
			} else {
				dbconnection = getInternalPoolConnection();				
				stmt = createStatement(dbconnection.getCon(), statementType, concurrencyType, holdabilityType);				
			}			
			result = stmt.executeQuery(sql);
			res = ObjectBuilder.buildResult(result);
			//auto-commit on
		} catch (SQLException e) {			
			logError(e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			this.cleanup(result, stmt, connection, dbconnection);
		}		
		return res;
	}
	
	/***
	 * execute an SELECT statement prepared
	 * SQL injection safe
	 * @param sql
	 * @return ResultObject with columns and ResultRows
	 * @throws SQLException
	 */
	public ResultObject executeQueryPrepared(String sql, Collection<Object> fields_values) throws SQLException
	{
		return executeQueryPrepared(sql, fields_values, null, null, null);
	}
	
	/***
	 * execute an SELECT statement prepared
	 * SQL injection safe
	 * @param sql
	 * @return ResultObject with columns and ResultRows
	 * @throws SQLException
	 */
	public ResultObject executeQueryPrepared(String sql, Collection<Object> fields_values, Integer statementType, Integer concurrencyType, Integer holdabilityType) throws SQLException
	{	
		//log and validation
		logInfo(sql);
		blockComments(sql);
		
		//do it
		ResultObject res = null;
		PreparedStatement stmt = null;
		ResultSet result = null;		
		
		Connection connection = null;
		DBConnection dbconnection = null;
		try {
			if (this.useServerPool) {
				connection = getConnection();
				stmt = prepareStatement(connection, sql, statementType, concurrencyType, holdabilityType);
			} else {
				dbconnection = getInternalPoolConnection();				
				stmt = prepareStatement(dbconnection.getCon(), sql, statementType, concurrencyType, holdabilityType);				
			}			
			//data
			int i = 1;
			StringBuffer values = new StringBuffer();
			for(Object value : fields_values)
			{		
				stmt = this.addParameter(stmt, i, value);							
				if (value != null) values.append(value.toString()+",");
				else values.append("null,");
				i++;
			}
			logInfo(values.toString());
			//execute
			result = stmt.executeQuery();
			res = ObjectBuilder.buildResult(result);
			//auto-commit on
		} catch (SQLException e) {
			logError(e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			this.cleanup(result, stmt, connection, dbconnection);
		}		
		return res;
	}
	
	/***
	 * execute an SELECT statement with max results
	 * ATTENTION: SQL-Injection vulnerable when using dynamic SQL
	 * @param sql
	 * @return ResultObject with columns and ResultRows
	 * @throws SQLException
	 */
	public ResultObject executeQuery(String sql, int max_resultrow_count) throws SQLException
	{
		return executeQuery(sql, max_resultrow_count, null, null, null);
	}
	
	/***
	 * execute an SELECT statement with max results
	 * ATTENTION: SQL-Injection vulnerable when using dynamic SQL
	 * @param sql
	 * @return ResultObject with columns and ResultRows
	 * @throws SQLException
	 */
	public ResultObject executeQuery(String sql, int max_resultrow_count, Integer statementType, Integer concurrencyType, Integer holdabilityType) throws SQLException
	{	
		//log and validation
		logInfo(sql);
		blockComments(sql);		

		//do the max count check
		Statement stmt = null;
		ResultSet result = null;
		
		Connection connection = null;
		DBConnection dbconnection = null;
		try {			
			int from_pos = sql.toLowerCase().indexOf(" from ");
			String newSQL = "select count(*) as GROESSE " + sql.substring(from_pos);
			//check size
			if (this.useServerPool) {
				connection = getConnection();				
				stmt = createStatement(connection, statementType, concurrencyType, holdabilityType);
			} else {
				dbconnection = getInternalPoolConnection();
				stmt = createStatement(dbconnection.getCon(), statementType, concurrencyType, holdabilityType);
			}			
			result = stmt.executeQuery(newSQL);
			result.next();
			if (result.getInt("GROESSE") > max_resultrow_count)
			{
				throwSqlException("Resultset too large. (>"+max_resultrow_count+")");				
			}
			//auto-commit on			
		} catch (SQLException e) {			
			logError(e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			this.cleanup(result, stmt, connection, dbconnection);
		}		
		
		return executeQuery(sql);
	}	
	
	/***
	 * Execute an INSERT, UPDATE, DELETE, ... statement
	 * ATTENTION: SQL-Injection vulnerable when using dynamic SQL
	 * @param sql
	 * @throws SQLException
	 */
	public int execute(String sql) throws SQLException
	{
		return execute(sql, null, null, null);
	}
	
	/***
	 * Execute an INSERT, UPDATE, DELETE, ... statement
	 * ATTENTION: SQL-Injection vulnerable when using dynamic SQL
	 * @param sql
	 * @throws SQLException
	 */
	public int execute(String sql, Integer statementType, Integer concurrencyType, Integer holdabilityType) throws SQLException
	{		
		//log and validation
		logInfo(sql);
		blockComments(sql);
		
		//do it
		Statement stmt = null;	
		
		Connection connection = null;
		DBConnection dbconnection = null;
		try{			
			if (this.useServerPool) {
				connection = getConnection();
				stmt = createStatement(connection, statementType, concurrencyType, holdabilityType);
			} else {
				dbconnection = getInternalPoolConnection();
				stmt = createStatement(dbconnection.getCon(), statementType, concurrencyType, holdabilityType);
			}   
			//auto-commit on
			return stmt.executeUpdate(sql);		
		} catch (SQLException e) {			
			logError(e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			this.cleanup(null, stmt, connection, dbconnection);
		}
	}	
	
	/***
	 * Execute an INSERT, UPDATE, DELETE, ... statement WITHOUT commit
	 * PLEASE EXECUTE commitForStatementsWithoutCommit AFTER YOUR WORK IS DONE!
	 * ATTENTION: SQL-Injection vulnerable when using dynamic SQL
	 * @param sql
	 * @throws SQLException
	 */
	public int executeWithoutCommit(String sql) throws SQLException
	{
		return executeWithoutCommit(sql, null, null, null);
	}
	
	/***
	 * Execute an INSERT, UPDATE, DELETE, ... statement WITHOUT commit
	 * PLEASE EXECUTE commitForStatementsWithoutCommit AFTER YOUR WORK IS DONE!
	 * ATTENTION: SQL-Injection vulnerable when using dynamic SQL
	 * @param sql
	 * @throws SQLException
	 */
	public int executeWithoutCommit(String sql, Integer statementType, Integer concurrencyType, Integer holdabilityType) throws SQLException
	{		
		//log and validation
		logInfo(sql);
		blockComments(sql);
		
		//do it
		Statement stmt = null;
		if (connectionForStatementsWithoutCommit == null) {
			if (this.useServerPool) {
				connectionForStatementsWithoutCommit = getConnection();
			} else {
				connectionForStatementsWithoutCommit = getInternalPoolConnection().getCon();
			}			
		}
		try{			
			stmt = createStatement(connectionForStatementsWithoutCommit, statementType, concurrencyType, holdabilityType);
			int res = stmt.executeUpdate(sql);			
			return res;
		} catch (SQLException e) {			
			logError(e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			this.cleanup(null, stmt, null, null);
		}
	}	
	
	/**
	 * commit after using executeInsUpdDelWithoutCommit
	 */
	public void commitForStatementsWithoutCommit() {
		if (connectionForStatementsWithoutCommit != null) {
			try {
				connectionForStatementsWithoutCommit.commit();
			} catch (SQLException e) {
				logError(e.getMessage());
				e.printStackTrace();
			}
			try {
				connectionForStatementsWithoutCommit.close();
			} catch (SQLException e) {
				logError(e.getMessage());
				e.printStackTrace();
			}
		}
		connectionForStatementsWithoutCommit = null;
	}
	
	/***
	 * Execute an statement as Prepared Statement
	 * supported: 
	 * BigDecimal, Blob, Boolean, Byte, byte[], Clob, Date, Double,
	 * Float, Integer, Long, Object, Ref, Short, Time, Timestamp, URL
	 * SQL-Injection safe
	 * @throws Exception 
	 */
	public void executePrepared(String sql, Collection<Object> fields_values) throws SQLException
	{	
		ArrayList<Collection<Object>> col = new ArrayList<Collection<Object>>();
		col.add(fields_values);
		executePreparedList(sql, col);
	}
	
	/***
	 * Execute multiple statements as Prepared Statement
	 * supported: 
	 * BigDecimal, Blob, Boolean, Byte, byte[], Clob, Date, Double,
	 * Float, Integer, Long, Object, Ref, Short, Time, Timestamp, URL
	 * SQL-Injection safe
	 * @throws Exception 
	 */
	public void executePreparedList(String sql, Collection<Collection<Object>> fields_values_col) throws SQLException
	{
		executePreparedList(sql, fields_values_col, null, null, null);
	}
	
	/***
	 * Execute multiple statements as Prepared Statement
	 * supported: 
	 * BigDecimal, Blob, Boolean, Byte, byte[], Clob, Date, Double,
	 * Float, Integer, Long, Object, Ref, Short, Time, Timestamp, URL
	 * SQL-Injection safe
	 * @throws Exception 
	 */
	public void executePreparedList(String sql, Collection<Collection<Object>> fields_values_col, Integer statementType, Integer concurrencyType, Integer holdabilityType) throws SQLException
	{		
		//log and validation
		logInfo(sql);
		blockComments(sql);
				
		//create PreparedStatement
		PreparedStatement stmt = null;
		
		Connection connection = null;
		DBConnection dbconnection = null;
		try{
			if (this.useServerPool) {
				connection = getConnection();
				connection.setAutoCommit(false);				
				stmt = prepareStatement(connection, sql, statementType, concurrencyType, holdabilityType);				
			} else {
				dbconnection = getInternalPoolConnection();				
				dbconnection.setAutoCommit(false);
				stmt = prepareStatement(dbconnection.getCon(), sql, statementType, concurrencyType, holdabilityType);				
			}	
			
			//iterate over data
			long counter = 1;
			for(Collection<Object> fields_values : fields_values_col)
			{
				//iterator over columns
				stmt.clearParameters();
				int i = 1;
				StringBuffer values = new StringBuffer();
				for(Object value : fields_values)
				{					
					this.addParameter(stmt, i, value);
					if (value != null) values.append(value.toString()+",");
					else values.append("null,");
					i++;
				}
				logInfo(counter+": "+values.toString());
				stmt.executeUpdate();
				counter++;
			}	
			
			//auto-commit off
			if (!this.isXA) {
				if (this.useServerPool) connection.commit();
				else dbconnection.getCon().commit();		
			}			
			
		} catch (SQLException e) {
			//auto-commit off
			if (!this.isXA) {
				if (this.useServerPool) connection.rollback();
				else dbconnection.getCon().rollback();		
			}
			logError(e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {			
			try {
				if (dbconnection != null)
					dbconnection.setAutoCommit(true);
			} catch (Exception e) {
				logWarn(e.getMessage());
			}
			this.cleanup(null, stmt, connection, null);
		}		
	}		
	
	/**
	 * adding parameter to prepared statement
	 * @return
	 * @throws SQLException 
	 */
	private PreparedStatement addParameter(PreparedStatement stmt, int i, Object value) throws SQLException {
		//check type
		if (value == null)							{ stmt.setObject(i, null); }
		else if (value instanceof BigDecimal) 		{ stmt.setBigDecimal(i, (BigDecimal)value); }
		else if (value instanceof Blob) 			{ stmt.setBlob(i, (Blob)value); }
		else if (value instanceof Boolean) 			{ stmt.setBoolean(i, (Boolean)value); }
		else if (value instanceof Byte) 			{ stmt.setByte(i, (Byte)value); }
		else if (value instanceof byte[]) 			{ stmt.setBytes(i, (byte[])value); }
		else if (value instanceof Clob) 			{ stmt.setClob(i, (Clob)value); }
		else if (value instanceof Time) 			{ stmt.setTime(i, (Time)value); }
		else if (value instanceof Timestamp) 		{ stmt.setTimestamp(i, (Timestamp)value); }
		else if (value instanceof java.sql.Date) 	{ stmt.setDate(i, (java.sql.Date)value); }
		else if (value instanceof java.util.Date) 	{ stmt.setDate(i, new java.sql.Date(((java.util.Date)value).getTime())); }
		else if (value instanceof Double) 			{ stmt.setDouble(i, (Double)value); }
		else if (value instanceof Float) 			{ stmt.setFloat(i, (Float)value); }
		else if (value instanceof Integer) 			{ stmt.setInt(i, (Integer)value); }
		else if (value instanceof Long) 			{ stmt.setLong(i, (Long)value); }
		else if (value instanceof Ref) 				{ stmt.setRef(i, (Ref)value); }
		else if (value instanceof Short) 			{ stmt.setShort(i, (Short)value); }
		//else if (value instanceof String) 			{ stmt.setString(i, maskSingleQuote((String)value)); }	
		else if (value instanceof String) 			{ stmt.setString(i, (String)value); }
		else if (value instanceof URL) 				{ stmt.setURL(i, (URL)value); }
		else 										{ stmt.setObject(i, value); }
		return stmt;
	}

	/***
	 * explicit closeConnection for internal connection pool
	 */
	public void closeConnection()
	{		
		try {			
			if (!this.useServerPool)	{
				this.manager.closeAllConnections();
			}			
		} catch (Exception e) {		
			logWarn(e.getMessage());
		}			
	}	
	
	/***
	 * internal cleanup
	 */
	private void cleanup(ResultSet result, Statement stmt, Connection connection, DBConnection dbconnection)
	{
		try {
			if (result != null)				
				result.close();			
		} catch (Exception e) {	
			logWarn(e.getMessage());
		}
		try {
			if (stmt != null)
				stmt.close();				
		} catch (Exception e) {		
			logWarn(e.getMessage());
		}
		/*try {
			if (connection != null)
				connection.close();						
		} catch (Exception e) {		
			logWarn(e.getMessage());
		}*/
		try {
			if (dbconnection != null)
				this.manager.returnConnectionToPool(dbconnection);
		} catch (Exception e) {		
			logWarn(e.getMessage());
		}
	}	
	
	/***
	 * converts Blob to byte[]
	 * @param blob
	 * @return byte Array from Blob
	 * @throws SQLException
	 * @throws IOException
	 */
	public static byte[] getBytesFromBlob(Blob blob) throws SQLException, IOException
	{			
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		byte[] buf = new byte[1024];
		 
		InputStream in = blob.getBinaryStream();		
		int n = 0;
		while ((n=in.read(buf))>=0)
		{
			baos.write(buf, 0, n);		 
		}
		
		in.close();
		return baos.toByteArray(); 
	}
	
	/***
	 * comments are not allowed --> '-- in field will ignore following where clause
	 * @throws SQLException 
	 */
	private void blockComments(String sql) throws SQLException
	{
		// --,/*,*/
		if (!isIgnoreCommentMinusMinus() && sql.contains("--")) throwSqlException("comment -- is not allowed.");
		
		if (sql.contains("/*+")){
			//existing hint
			if (!isIgnoreBlockComment() && sql.contains("/* ")) throwSqlException("comment /* is not allowed.");
		} else {
			if (!isIgnoreBlockComment() && sql.contains("/*")) throwSqlException("comment /* is not allowed.");
			if (!isIgnoreBlockComment() && sql.contains("*/")) throwSqlException("comment */ is not allowed.");
		}
	}
	
	/***
	 * mask Single Quote
	 * @param String
	 * @return String
	 */
	public static String maskSingleQuote(String in)
	{
		return in.replaceAll(SINGLE_QUOTE, "''");		
	}	
	
	/**
	 * remove leading whitespace 
	 */
	public static String ltrim(String source) {
		return source.replaceAll("^\\s+", "");
	}

	/**
	 * remove trailing whitespace 
	 */
	public static String rtrim(String source) {
		return source.replaceAll("\\s+$", "");
	}

	/**
	 * ignore comment --?
	 * @return
	 */
	private boolean isIgnoreCommentMinusMinus() {
		return ignoreCommentMinusMinus;
	}
	
	/**
	 * ignore comment /*?
	 * @return
	 */
	private boolean isIgnoreBlockComment() {
		return ignoreBlockComment;
	}

	/**
	 * set true to allow comment '--'
	 * @param ignoreComment
	 */
	public void setAllowCommentMinusMinus(boolean ignoreComment) {
		this.ignoreCommentMinusMinus = ignoreComment;
	}	
	
	/**
	 * set true to allow comment '/*'
	 * @param ignoreComment
	 */
	public void setAllowBlockComment(boolean ignoreComment) {
		this.ignoreBlockComment = ignoreComment;
	}

	/**
	 * using implementation as DataSource...
	 */
	public Connection getConnection(String username, String password)
			throws SQLException {		
		return getConnection();
	}
	
	public PrintWriter getLogWriter() throws SQLException {		
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setLogWriter(PrintWriter out) throws SQLException {
		// TODO Auto-generated method stub
	}
	
	public void setLoginTimeout(int seconds) throws SQLException {	
		// TODO Auto-generated method stub
	}
	
	public int getLoginTimeout() throws SQLException {	
		// TODO Auto-generated method stub
		return 0;
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}		
	
}
