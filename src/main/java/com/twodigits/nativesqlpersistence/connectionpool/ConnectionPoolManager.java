package com.twodigits.nativesqlpersistence.connectionpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.twodigits.nativesqlpersistence.DatabaseLogger;

/**
 * create an connection pool internally
 * DO NOT USE FOR EXTERNAL APPLICATIONS!
 * DO NOT USE SINGLETON, BECAUSE IT COULD BE USED MULTIPLE TIMES!
 * @author robert.diers
 *
 */
public class ConnectionPoolManager {
	
	//Constants
	public static long MINUTE_AS_MS = 1000*60;
	
	//parameters
	private int timeout_last_used = 5; //minutes
	private int timeout_created = 30; //minutes

	//Data for native Connection
	private String url = "";
	private String userid = "";
	private String password = "";
	private String driver = "";
	
	//Logger
	private DatabaseLogger printer = null;
	
	//use synchronized Collection as Pool
	//private List<DBConnection> connectionPool = new Vector<DBConnection>(); //out dated...
	private List<DBConnection> connectionPool = Collections.synchronizedList(new LinkedList<DBConnection>());

	/**
	 * for singleton
	 * @throws SQLException
	 */
	public ConnectionPoolManager(String url, String userid, String password, String driver, DatabaseLogger printer) throws SQLException {
		//connect data
		this.url = url;
		this.userid = userid;
		this.password = password;
		this.driver = driver;
		this.printer = printer;
		//initialize the pool with one connection
		initializeConnectionPool();
		logWarn("Connection pool initilized");
		//create the task for 30 minute connection check!
		Timer timer = new Timer();
		TimerTask cleanuptask  = new ConnectionPoolCleanupTask(this);
		timer.scheduleAtFixedRate(cleanuptask, getNextMinute(getTimeout_created()), (getTimeout_created() * MINUTE_AS_MS));
		logWarn("ConnectionPoolCleanupTask scheduled every " + getTimeout_created() + " minutes");
	}
	
	/**
	 * used for TimerTask
	 * @param minute
	 * @return
	 */
	private Date getNextMinute(int minute){
	    Calendar nextMin = new GregorianCalendar();
	    nextMin.add(Calendar.MINUTE, minute);	    
	    return nextMin.getTime();
	}	

	/**
	 * add one connection
	 * @throws SQLException
	 */
	private void initializeConnectionPool() throws SQLException {		
		connectionPool.add(createNewConnectionForPool());
	}	

	/**
	 * create standard jdbc connection
	 * @return
	 * @throws SQLException
	 */
	private DBConnection createNewConnectionForPool() throws SQLException {
		Connection connection = null;
		try {
			Class.forName(this.driver);
		} catch (ClassNotFoundException e) {		
			logError(e.getMessage());
			e.printStackTrace();
			throw new SQLException("ClassNotFoundException: "+e.getMessage()+ ": " + this.url);
		}
		connection = DriverManager.getConnection(this.url, this.userid, this.password);		
		
		//wrapper
		DBConnection dbcon = new DBConnection();
		dbcon.setCon(connection);
		logWarn("Connection "+dbcon.getID()+" created");
		return dbcon;
	}

	/**
	 * get DBConnection from pool
	 * @return
	 * @throws SQLException 
	 */
	public synchronized DBConnection getConnectionFromPool() throws SQLException {	

		//check if there is a connection available
		DBConnection connection = getDBConnectionFromVector();
		while (connection != null) {
			//check and close to old ones
			if (!isToOld(connection)) {
				return connection;
			}
			//next one
			connection = getDBConnectionFromVector();	
		}
		
		//create new one
		return createNewConnectionForPool();				
	}
	
	/**
	 * getNextDBConnection -> must be synchronized
	 */
	private synchronized DBConnection getDBConnectionFromVector() {
		DBConnection connection = null;
		if (connectionPool.size() > 0) {			
			connection = (DBConnection) connectionPool.get(0);
			connectionPool.remove(0);
		}
		return connection;
	}
	
	/**
	 * check if connection is to old
	 */
	private boolean isToOld(DBConnection con) {
		boolean toOld = false;
		if (getMinutesBetweenTwoDates(con.getLast_used(), new Date()) > getTimeout_last_used()) {
			toOld = true;
			//last used to old --> close
			try {
				con.getCon().close();
				logWarn("connection "+con.getID()+" closed because last used to old (>"+getTimeout_last_used()+"min)");
			} catch (SQLException e) {
				logError(e.getMessage());
				e.printStackTrace();				
			}
		}
		if (getMinutesBetweenTwoDates(con.getCreated(), new Date()) > getTimeout_created()) {
			toOld = true;
			//created to old --> close
			try {
				con.getCon().close();
				logWarn("connection "+con.getID()+" closed because created to old (>"+getTimeout_created()+"min)");
			} catch (SQLException e) {
				logError(e.getMessage());
				e.printStackTrace();				
			}
		}
		return toOld;
	}
	
	/**
	 * get time between two dates in minutes
	 */
	private long getMinutesBetweenTwoDates(Date startDate, Date endDate) {
		long start = startDate.getTime();
		long end = endDate.getTime();
		long duration = (end - start);
		//convert ms to minutes
		return (duration / MINUTE_AS_MS);
	}

	/**
	 * return DBConnection to pool
	 * @param connection
	 */
	public synchronized void returnConnectionToPool(DBConnection connection) {
		//adding the connection from the client back to the connection pool
		connection.setLast_used(new Date());
		connectionPool.add(connection);
	}
	
	/**
	 * close all connections in pool
	 */
	public synchronized void closeAllConnections() {
		logWarn("force closing " + connectionPool.size() + " connections");
		DBConnection connection = getDBConnectionFromVector();
		while (connection != null) {
			//close
			try {				
				connection.getCon().close();
				logInfo("Connection "+connection.getID()+" forced closed.");
			} catch (SQLException e) {
				logError(e.getMessage());
				e.printStackTrace();				
			}
			//next one
			connection = getDBConnectionFromVector();	
		}	
	}	

	/***
	 * write to DatabaseLogger
	 * @param message
	 */
	private void logInfo(String message)
	{
		if (this.printer != null)
		{			
			printer.logInfo("INFO: "+getClass().getSimpleName()+ ": " + message + " (" + this.url + ")");
		}
	}	
	private void logWarn(String message)
	{
		if (this.printer != null)
		{			
			printer.logWarn("WARN: "+getClass().getSimpleName()+ ": " + message + " (" + this.url + ")");
		}
	}	
	private void logError(String message)
	{
		if (this.printer != null)
		{			
			printer.logError("ERROR: "+getClass().getSimpleName()+ ": " + message + " (" + this.url + ")");
		}
	}

	/**
	 * @return the timeout_last_used
	 */
	public int getTimeout_last_used() {
		return timeout_last_used;
	}

	/**
	 * @param timeout_last_used the timeout_last_used to set
	 */
	public void setTimeout_last_used(int timeout_last_used) {
		this.timeout_last_used = timeout_last_used;
	}

	/**
	 * @return the timeout_created
	 */
	public int getTimeout_created() {
		return timeout_created;
	}

	/**
	 * @param timeout_created the timeout_created to set
	 */
	public void setTimeout_created(int timeout_created) {
		this.timeout_created = timeout_created;
	}		
	
}
