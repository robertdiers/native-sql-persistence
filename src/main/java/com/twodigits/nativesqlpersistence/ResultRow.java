package com.twodigits.nativesqlpersistence;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;

/***
 * Result Row from Simple Database Access
 * contains column values of one row
 * 
 * @author Robert Diers
 */
public class ResultRow {
	
	//map with data per colum
	HashMap<String,Object> objects = new HashMap<String,Object>();
	HashMap<Integer,String> positions = new HashMap<Integer,String>();
	
	/***
	 * set data
	 * @param column
	 * @param value
	 */
	public void setColumnValue(String column, int position, Object value)
	{
		objects.put(column.toUpperCase(), value);
		positions.put(position, column.toUpperCase());
	}
	
	/***
	 * get columnname for position
	 */
	public String getColumnNameForPosition(int position)
	{
		return (String)positions.get(position);
	}
	
	/***
	 * get data as Object
	 * @param column
	 * @return Object
	 */
	public Object getObjectValue(String column)
	{		
		return objects.get(column.toUpperCase());
	}	
	
	/***
	 * get data as Object
	 * @param column
	 * @return Object
	 */
	public Object getObjectValue(int position)
	{		
		return getObjectValue(getColumnNameForPosition(position));
	}	
	
	/***
	 * get data as String
	 * @param column
	 * @return String
	 */
	public String getStringValue(String column)
	{
		Object o = objects.get(column.toUpperCase());
		if (o == null) return null;
		return (String)o;
	}
	
	/***
	 * get data as String
	 * @param column
	 * @return String
	 */
	public String getStringValue(int position)
	{
		return getStringValue(getColumnNameForPosition(position));
	}
	
	/***
	 * get data as BigDecimal
	 * @param column
	 * @return BigDecimal
	 */
	public BigDecimal getBigDecimalValue(String column)
	{
		Object o = objects.get(column.toUpperCase());
		if (o == null) return null;
		if (o instanceof BigDecimal) return (BigDecimal)o;
		if (o instanceof Long) return new BigDecimal((Long)o);
		if (o instanceof Short) return new BigDecimal((Short)o);
		if (o instanceof Integer) return new BigDecimal((Integer)o);
		if (o instanceof Float) return new BigDecimal((Float)o);
		if (o instanceof Double) return new BigDecimal((Double)o);
		return (BigDecimal)o;
	}
	
	/***
	 * get data as BigDecimal
	 * @param column
	 * @return BigDecimal
	 */
	public BigDecimal getBigDecimalValue(int position)
	{		
		return getBigDecimalValue(getColumnNameForPosition(position));
	}	
	
	/***
	 * get data as Boolean
	 * @param column
	 * @return Boolean
	 */
	public Boolean getBooleanValue(String column)
	{
		Object o = objects.get(column.toUpperCase());
		if (o == null) return null;
		return (Boolean)o;
	}
	
	/***
	 * get data as Boolean
	 * @param column
	 * @return Boolean
	 */
	public Boolean getBooleanValue(int position)
	{		
		return getBooleanValue(getColumnNameForPosition(position));
	}		
	
	/***
	 * get data as InputStream
	 * @param column
	 * @return InputStream
	 */
	public InputStream getInputStreamValue(String column)
	{
		Object o = objects.get(column.toUpperCase());
		if (o == null) return null;
		return (InputStream)o;
	}	
	
	/***
	 * get data as InputStream
	 * @param column
	 * @return InputStream
	 */
	public InputStream getInputStreamValue(int position)
	{
		return getInputStreamValue(getColumnNameForPosition(position));
	}	

	/***
	 * get data as Blob
	 * @param column
	 * @return Blob
	 */
	public Blob getBlobValue(String column)
	{
		Object o = objects.get(column.toUpperCase());
		if (o == null) return null;
		return (Blob)o;
	}
	
	/***
	 * get data as Blob
	 * @param column
	 * @return Blob
	 */
	public Blob getBlobValue(int position)
	{		
		return getBlobValue(getColumnNameForPosition(position));
	}
	
	/***
	 * get data as Clob
	 * @param column
	 * @return Clob
	 */
	public Clob getClobValue(String column)
	{
		Object o = objects.get(column.toUpperCase());
		if (o == null) return null;
		return (Clob)o;
	}
	
	/***
	 * get data as Clob
	 * @param column
	 * @return Clob
	 */
	public Clob getClobValue(int position)
	{		
		return getClobValue(getColumnNameForPosition(position));
	}
	
	/***
	 * get data as java.sql.Date
	 * @param column
	 * @return java.sql.Date
	 */
	public java.sql.Date getDateSqlValue(String column)
	{
		Object o = objects.get(column.toUpperCase());
		if (o == null) return null;
		if (o instanceof Timestamp) return new java.sql.Date(((Timestamp)o).getTime());
		if (o instanceof java.sql.Date) return (java.sql.Date)o;		
		return (java.sql.Date)o;
	}
	
	/***
	 * get data as java.sql.Date
	 * @param column
	 * @return java.sql.Date
	 */
	public java.sql.Date getDateSqlValue(int position)
	{		
		return getDateSqlValue(getColumnNameForPosition(position));
	}
	
	/***
	 * get data as java.util.Date
	 * @param column
	 * @return java.util.Date
	 */
	public java.util.Date getDateUtilValue(String column)
	{
		Object o = objects.get(column.toUpperCase());
		if (o == null) return null;
		if (o instanceof Timestamp) return new java.util.Date(((Timestamp)o).getTime());
		if (o instanceof java.sql.Date) return new java.util.Date(((java.sql.Date)o).getTime());
		if (o instanceof java.util.Date) return (java.util.Date)o;		
		return (java.util.Date)o;
	}
	
	/***
	 * get data as java.util.Date
	 * @param column
	 * @return java.util.Date
	 */
	public java.util.Date getDateUtilValue(int position)
	{		
		return getDateUtilValue(getColumnNameForPosition(position));
	}
	
	/***
	 * get data as Float
	 * @param column
	 * @return Float
	 */
	public Float getFloatValue(String column)
	{
		Object o = objects.get(column.toUpperCase());
		if (o == null) return null;
		if (o instanceof BigDecimal) return ((BigDecimal)o).floatValue();
		return (Float)o;
	}
	
	/***
	 * get data as Float
	 * @param column
	 * @return Float
	 */
	public Float getFloatValue(int position)
	{		
		return getFloatValue(getColumnNameForPosition(position));
	}
	
	/***
	 * get data as Integer
	 * @param column
	 * @return Integer
	 */
	public Integer getIntegerValue(String column)
	{
		Object o = objects.get(column.toUpperCase());
		if (o == null) return null;
		if (o instanceof BigDecimal) return ((BigDecimal)o).intValue();
		return (Integer)o;
	}	
	
	/***
	 * get data as Integer
	 * @param column
	 * @return Integer
	 */
	public Integer getIntegerValue(int position)
	{		
		return getIntegerValue(getColumnNameForPosition(position));
	}	
	
	/***
	 * get data as Ref
	 * @param column
	 * @return Ref
	 */
	public Ref getRefValue(String column)
	{
		Object o = objects.get(column.toUpperCase());
		if (o == null) return null;
		return (Ref)o;
	}
	
	/***
	 * get data as Ref
	 * @param column
	 * @return Ref
	 */
	public Ref getRefValue(int position)
	{		
		return getRefValue(getColumnNameForPosition(position));
	}
	
	/***
	 * get data as Time
	 * @param column
	 * @return Time
	 */
	public Time getTimeValue(String column)
	{
		Object o = objects.get(column.toUpperCase());
		if (o == null) return null;
		return (Time)o;
	}
	
	/***
	 * get data as Time
	 * @param column
	 * @return Time
	 */
	public Time getTimeValue(int position)
	{		
		return getTimeValue(getColumnNameForPosition(position));
	}

	/***
	 * get data as Timestamp
	 * @param column
	 * @return Timestamp
	 */
	public Timestamp getTimestampValue(String column)
	{
		Object o = objects.get(column.toUpperCase());
		if (o == null) return null;
		return (Timestamp)o;
	}
	
	/***
	 * get data as Timestamp
	 * @param column
	 * @return Timestamp
	 */
	public Timestamp getTimestampValue(int position)
	{		
		return getTimestampValue(getColumnNameForPosition(position));
	}
	
	/***
	 * get data as Long
	 * @param column
	 * @return Long
	 */
	public Long getLongValue(String column)
	{
		Object o = objects.get(column.toUpperCase());
		if (o == null) return null;
		if (o instanceof BigDecimal) return ((BigDecimal)o).longValue();
		return (Long)o;
	}
	
	/***
	 * get data as Long
	 * @param column
	 * @return Long
	 */
	public Long getLongValue(int position)
	{		
		return getLongValue(getColumnNameForPosition(position));
	}
	
	/***
	 * get data as Short
	 * @param column
	 * @return Short
	 */
	public Short getShortValue(String column)
	{
		Object o = objects.get(column.toUpperCase());
		if (o == null) return null;
		if (o instanceof BigDecimal) return ((BigDecimal)o).shortValue();
		return (Short)o;
	}
	
	/***
	 * get data as Short
	 * @param column
	 * @return Short
	 */
	public Short getShortValue(int position)
	{		
		return getShortValue(getColumnNameForPosition(position));
	}
	
	/***
	 * get data as URL
	 * @param column
	 * @return URL
	 */
	public URL getURLValue(String column)
	{
		Object o = objects.get(column.toUpperCase());
		if (o == null) return null;
		return (URL)o;
	}
	
	/***
	 * get data as URL
	 * @param column
	 * @return URL
	 */
	public URL getURLValue(int position)
	{		
		return getURLValue(getColumnNameForPosition(position));
	}
	
	/***
	 * get all Objects as HashMap
	 * @return HashMap
	 */
	public HashMap<String,Object> getAllObjects()
	{
		return objects;
	}		
	
}
