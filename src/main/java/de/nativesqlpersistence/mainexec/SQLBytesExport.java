package de.nativesqlpersistence.mainexec;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.nativesqlpersistence.DatabaseAccessor;

/**
 * export Bytes to disk
 * @author robert.diers
 *
 */
public class SQLBytesExport {
	

	private final static String outdir = "out";	
	
	public static void main(String[] args) {
		
		DatabaseAccessor db = null;
		String to_execute = "";
		Connection conn = null;
		try{
			if (args.length != 5)
			{				
				System.out.println("usage:");
				System.out.println("java -cp nativesqlpersistence_1.1.jar:<database-driver.jar> de.nativesqlpersistence.mainexec.SQLBytesExport <url> <DriverClass> <User> <Password> \"<SQL>\"");
				System.out.println("please use column alias 'filename' and 'filedata'");
				System.exit(1);
			}
			
			String url = args[0];
			String driver = args[1];
			String user = args[2];
			String password = args[3];			
			String sql = args[4];
			
			System.out.println("url: "+url);
			System.out.println("driver: "+driver);
			System.out.println("user: "+user);
			System.out.println("password: "+password);
			
			//we will use DatabaseAccessor only for connection creation to save heap
			db = new DatabaseAccessor(url, user, password, driver);				
			
			File outputdir = new File(outdir);
			outputdir.mkdirs();
			
			// Prepare a Statement
			conn = db.getConnection();
	        PreparedStatement stmnt = conn.prepareStatement(sql);

	        // Execute
	        ResultSet rs = stmnt.executeQuery();	        

	        long counter = 0;	        
	        while(rs.next()) {
	        	String filename = rs.getString("filename");	        	
	        	try {	        					
					System.out.println(outdir+File.separator+filename);				
				    
				    //we will use bytes!
				    FileOutputStream fos = new FileOutputStream(outdir+File.separator+filename);
				    byte[] bytes = rs.getBytes("filedata");
				    try {
	            	    fos.write(bytes);
	            	}
	            	finally {
	            	    fos.close();
	            	}
				    
				    counter++;
				    System.out.println("exported: "+counter);
	            } catch (Exception ex){
	            	ex.printStackTrace();
	            }
	        }       			
			    
			System.out.println("Finished!");
			
			System.exit(1);
			
		} catch (Exception e)
		{
			System.out.println("ERROR: "+to_execute);
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if (conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}
		}
	}
}
