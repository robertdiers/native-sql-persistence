package example;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import de.nativesqlpersistence.DBColumn;
import de.nativesqlpersistence.ResultObject;
import de.nativesqlpersistence.ResultRow;

/**
 * test class
 * @author robert.diers
 *
 */
public class Tester {

	public static void main(String[] args) {
	
		try {		
			
			//please set database information in Database1 and Database2 classes
			//it is possible to define multiple data sources with different providers
			
			System.out.println("execute SQL injection vulnerable one with Database1:");
			
			String sql = "select userid from testuser";
			ResultObject res = Database1.getInstance().getDatabaseAccessor().executeQuery(sql);
			for (ResultRow r : res.getResultRows()) {
				System.out.println(r.getStringValue("userid"));
			}
			
			System.out.println();
			
			System.out.println("execute prepared one with Database2:");
			
			sql = "select userid from testuser where userid = ?";
			Collection<Object> fields_values = new ArrayList<Object>();
			fields_values.add("robert.diers");
			res = Database1.getInstance().getDatabaseAccessor().executeQueryPrepared(sql, fields_values);
			for (ResultRow r : res.getResultRows()) {
				System.out.println(r.getStringValue("userid"));
			}
			
			System.out.println();
			
			System.out.println("reading columns from second one:");
			for (DBColumn col : res.getColumnsFromView()) {
				System.out.println(col.getColumnname() + " - " + col.getColumntype());
			}
			
			System.out.println();
			
			System.out.println("generating the XML for Excel:");
			System.out.println(res.getXML(true));			
		
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (IOException ioe) {			
			ioe.printStackTrace();
		}
		
	}

}
