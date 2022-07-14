package com.twodigits.nativesqlpersistence.mainexec;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.twodigits.nativesqlpersistence.DatabaseAccessor;
import com.twodigits.nativesqlpersistence.ResultObject;
import com.twodigits.nativesqlpersistence.ResultRow;

/**
 * execute SQL statements as main class
 * @author robert.diers
 *
 */
public class SQLExecuter {
	
	private final static int STP_QUERY_TIMEOUT = 5 * 60;
	private final static int script_log_step = 100;
	
	public static void main(String[] args) {
		
		DatabaseAccessor db = null;
		String to_execute = "";
		try{
			if (args.length != 5 && args.length != 6)
			{				
				System.out.println("usage:");
				System.out.println("java -cp native-sql-persistence.jar:<database-driver.jar> com.twodigits.nativesqlpersistence.mainexec.SQLExecuter <url> <DriverClass> <User> <Password> \"<SQL>\"");
				System.out.println("or");
				System.out.println("java -cp native-sql-persistence.jar:<database-driver.jar> com.twodigits.nativesqlpersistence.mainexec.SQLExecuter <url> <DriverClass> <User> <Password> <Encoding> <*.sql>");
				System.exit(1);
			}
			
			String url = args[0];
			String driver = args[1];
			String user = args[2];
			String password = args[3];			
			
			boolean isScript = false;
			if (args.length == 6) isScript = true;
			
			System.out.println("url: "+url);
			System.out.println("driver: "+driver);
			System.out.println("user: "+user);
			System.out.println("password: "+password);
			
			//xa true, because commit in statement list...
			db = new DatabaseAccessor(url, user, password, driver);	
			db.setAllowBlockComment(true);
			db.setAllowCommentMinusMinus(true);			
			
			if (!isScript)
			{
				//simple SQL or STP			
				String sql = args[4];	
				if (sql.toUpperCase().startsWith("CALL ")) {
					//STP
					System.out.println("Executing STP: "+sql);
					Connection connection = null;
					try {
						connection = db.getConnection();
				        //disable auto commit 
				        connection.setAutoCommit(false);
				        to_execute = sql;
				        CallableStatement statement = connection.prepareCall(to_execute);
				        statement.setQueryTimeout(STP_QUERY_TIMEOUT);
				        long start = new Date().getTime();
				        //execute
				        statement.execute();
				        long stop = new Date().getTime();
				        System.out.println("Execution Time in ms: "+(stop-start));
				        int counter = 0;
				        while (statement.getMoreResults()) {
				        	counter++;
				        	writeResultset(statement.getResultSet(), counter);				        	
				        }					        
					} finally {
						if (connection!=null) connection.close();
					}
				} else {
					//SQL
					System.out.println("Executing SQL: "+sql);
					to_execute = sql;
					if (sql.toUpperCase().startsWith("SELECT")) {
						ResultObject res = db.executeQuery(to_execute);
						int columncount = res.getColumnsFromView().size();
						System.out.println("Columns: "+columncount);
						
						System.out.println("Writing output.csv");
						FileWriter fstream = new FileWriter("output.csv");
						BufferedWriter out = new BufferedWriter(fstream);	
						ArrayList<ResultRow> rows = res.getResultRows();
						System.out.println("Rows: "+rows.size());
						for (ResultRow row : rows)
						{
							StringBuffer buf = new StringBuffer();
							for (int i = 1; i <= columncount; i++)
							{
								Object o = row.getObjectValue(i);
								if (o != null) buf.append(o.toString() + ";");
								else buf.append("null;");
							}
							out.write(buf.toString());
							out.newLine();
						}
						out.close();
					} else {
						//execute none query
						db.execute(sql);
					}					
				}				
			} else {
				//Script		
				String encoding = args[4];	
				String sql = args[5];	
				File sqlScript = new File(sql);
				System.out.println(sql+" - Executing script: "+sqlScript.getPath());
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sqlScript), encoding));				
		        ArrayList<String> sqls = new ArrayList<String>();
			    try {			        
			        String line = br.readLine();			        
			        while (line != null) {			            
			            sqls.add(line);
			            line = br.readLine();			            
			        }			        
			    } finally {
			        br.close();
			    }
			    
			    int size = sqls.size();
			    System.out.println(sql+" - Executing "+size + " SQLs...");
			    
			    int counter = 0;
		        int counter2 = 0;
		        FileWriter fstream = new FileWriter(sql+"_ERROR.txt");
		        FileWriter fstream2 = new FileWriter(sql+"_DONE.txt");
		        FileWriter fstream3 = new FileWriter(sql+"_FAILED.txt");
		        
    			BufferedWriter out = new BufferedWriter(fstream);
    			BufferedWriter out_done = new BufferedWriter(fstream2);
    			BufferedWriter out_failed = new BufferedWriter(fstream3);
    		   			
			    for (String tsql : sqls) {
			    	counter++;
		        	counter2++;		        	
		        	try {		
		        		//execute
		    			db.execute(tsql);    			
		    			
		    			out_done.write(tsql);
		    			out_done.newLine();				        		
		    		} catch (Exception e2) {
		    			System.out.println("ERROR: "+e2.getMessage());
		        		System.out.println("SQL: " + tsql);			
		        		out_failed.write(tsql);
		        		out_failed.newLine();	
		        		out_failed.flush();
		    			out.write("ERROR: "+e2.getMessage());
		    			out.newLine();
		    			out.write("SQL: " + tsql);
		    			out.newLine();
		    			out.flush();
		    		}
		        	if (counter2 >= script_log_step) {
		            	counter2 = 0;	
		            	out_done.flush();		            	
		            	System.out.println(sql+" - Executed: "+counter + " / " + size);
		            }
			    }
			    
			    out.flush();
			    out.close();
			    out_done.flush();
			    out_done.close();
			    out_failed.flush();
			    out_failed.close();
			    System.out.println(sql+" - Finally executed: "+counter);
			}
			System.out.println("Finished!");
			
			System.exit(1);
			
		} catch (Exception e)
		{
			System.out.println("ERROR: "+to_execute);
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if (db!=null) db.closeConnection();
		}
	}
	
	/**
	 * write result set to file
	 * @param rs
	 * @param counter
	 * @throws SQLException
	 * @throws IOException
	 */
	private static void writeResultset(ResultSet rs, int counter) throws SQLException, IOException {
		System.out.println("Writing output_resultset"+counter+".txt");
		FileWriter fstream = new FileWriter("output_resultset"+counter+".txt");
		BufferedWriter out = new BufferedWriter(fstream);	
		int columnCount = rs.getMetaData().getColumnCount();
		while (rs.next()) {
			for (int i = 0; i < columnCount;) {
				out.write(rs.getString(i + 1));
		        if (++i < columnCount) out.write(";");
		    }			
			out.newLine();
        }		
		out.close();		
	}
}
