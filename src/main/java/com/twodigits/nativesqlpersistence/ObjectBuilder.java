package com.twodigits.nativesqlpersistence;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/***
 * Object Builder for Simple Database Access
 * 
 * @author Robert Diers
 */
public class ObjectBuilder {
	
	/***
	 * generates ResultObject
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	public static ResultObject buildResult(ResultSet resultSet) throws SQLException
	{
		ResultObject result = new ResultObject();
		
		//get columns				
		ResultSetMetaData rsmd = resultSet.getMetaData();
        int numColumns = rsmd.getColumnCount();		
        for (int i=1; i<numColumns+1; i++) {
            String columnName = rsmd.getColumnName(i);
            String columnType = rsmd.getColumnTypeName(i);         
            
            //add column
            DBColumn col = new DBColumn();
            col.setColumnname(columnName);
            col.setColumntype(columnType);
            result.addColumnFromView(col);
        }
		
        //get data
		while (resultSet.next())
		{			
			ResultRow obj = new ResultRow();
			
			//iterator over all columns
			int counter = 0;			
	        for (DBColumn col : result.getColumnsFromView()) 
	        {		
	        	counter++;        	
	        	//store result object
	        	obj.setColumnValue(col.getColumnname(), counter, createObj(resultSet, col.getColumnname(), col.getColumntype()));
	        }	        
	        
	        //add ResultRow
	        result.addResultRow(obj);
		}		
		
		return result;
	}	
	
	/***
	 * creates an Object from an type definition
	 * @param resultSet
	 * @param columnName
	 * @param columnType
	 * @return
	 * @throws SQLException
	 */
	private static Object createObj(ResultSet resultSet, String columnName, String columnType) throws SQLException
	{
		Object res = null;
		
    	//type?
    	if (columnType.equals("BIGINT"))
    	{
    		res = new Integer(resultSet.getInt(columnName));
    	}
    	else if (columnType.equals("BINARY"))
    	{
    		res = resultSet.getBinaryStream(columnName);
    	}
    	else if (columnType.equals("BLOB"))
    	{
    		res = resultSet.getBlob(columnName);
    	}
    	else if (columnType.equals("BOOLEAN"))
    	{
    		res = new Boolean(resultSet.getBoolean(columnName));
    	}
    	else if (columnType.equals("CHAR"))
    	{
    		res = resultSet.getString(columnName);
    	}
    	else if (columnType.equals("CLOB"))
    	{
    		res = resultSet.getClob(columnName);
    	}
    	else if (columnType.equals("DATE"))
    	{
    		//Oracle Bugfix Time for all Dates
    		res = resultSet.getTimestamp(columnName);
    		//res = resultSet.getDate(columnName);
    	}
    	else if (columnType.equals("DECIMAL"))
    	{
    		res = resultSet.getBigDecimal(columnName);
    	}
    	else if (columnType.equals("DOUBLE"))
    	{
    		res = resultSet.getBigDecimal(columnName);
    	}
    	else if (columnType.equals("FLOAT"))
    	{
    		res = new Float(resultSet.getFloat(columnName));
    	}
    	else if (columnType.equals("INTEGER"))
    	{
    		res = new Integer(resultSet.getInt(columnName));
    	}
    	else if (columnType.equals("LONG"))
    	{
    		res = new Long(resultSet.getLong(columnName));
    	}
    	else if (columnType.equals("LONGVARCHAR"))
    	{
    		res = resultSet.getString(columnName);
    	}
    	else if (columnType.equals("NUMERIC"))
    	{
    		res = resultSet.getBigDecimal(columnName);
    	}
    	else if (columnType.equals("NUMBER"))
    	{
    		res = resultSet.getBigDecimal(columnName);
    	}
    	else if (columnType.equals("REAL"))
    	{
    		res = resultSet.getBigDecimal(columnName);
    	}
    	else if (columnType.equals("REF"))
    	{
    		res = resultSet.getRef(columnName);
    	}
    	else if (columnType.equals("SMALLINT"))
    	{
    		res = new Integer(resultSet.getInt(columnName));
    	}
    	else if (columnType.equals("SHORT"))
    	{
    		res = new Short(resultSet.getShort(columnName));
    	}
    	else if (columnType.equals("TIME"))
    	{
    		res = resultSet.getTime(columnName);
    	}
    	else if (columnType.equals("TIMESTAMP"))
    	{
    		res = resultSet.getTimestamp(columnName);
    	}
    	else if (columnType.equals("VARCHAR"))
    	{
    		res = resultSet.getString(columnName);
    	}
    	else if (columnType.equals("NVARCHAR"))
    	{
    		res = resultSet.getString(columnName);
    	}
    	else if (columnType.equals("URL"))
    	{
    		res = resultSet.getURL(columnName);
    	}
    	else
    	{
    		res = resultSet.getObject(columnName);
    	}
		
		return res;
	}	
}
