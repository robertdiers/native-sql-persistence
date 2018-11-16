package com.twodigits.nativesqlpersistence;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.twodigits.nativesqlpersistence.export.CsvUtil;
import com.twodigits.nativesqlpersistence.export.HtmlUtil;
import com.twodigits.nativesqlpersistence.export.XmlUtil;

/***
 * Result Object for Simple Database Access
 * - contains columns and ResultRows
 * 
 * @author Robert Diers
 */
public class ResultObject {
	
	ArrayList<ResultRow> resultRows = new ArrayList<ResultRow>();
	ArrayList<DBColumn> columnsFromView = new ArrayList<DBColumn>();	
	
	/***
	 * returns the list with ResultRows
	 * @return the list with ResultRows
	 */
	public ArrayList<ResultRow> getResultRows() {
		return resultRows;
	}	
	
	/***
	 * add one ResultRow
	 */
	public void addResultRow(ResultRow resultRow) {
		this.resultRows.add(resultRow);
	}
	
	/***
	 * add multiple ResultRows
	 */
	public void addAllResultRows(Collection<ResultRow> resultRows) {
		this.resultRows.addAll(resultRows);
	}
	
	/***
	 * returns the list with Columns as String
	 * @return the list with Columns as String
	 */
	public ArrayList<DBColumn> getColumnsFromView() {
		return columnsFromView;
	}
	
	/***
	 * add one column
	 */
	public void addColumnFromView(DBColumn column) {
		this.columnsFromView.add(column);
	}
	
	/***
	 * add multiple columns
	 */
	public void addAllColumnsFromView(Collection<DBColumn> columns) {
		this.columnsFromView.addAll(columns);
	}	    

    /**
     * format Date output
     * @param millis
     * @return
     */
    public static String formatDate(long millis) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return sdf.format(new java.util.Date(millis));
    }
    
    /**
	 * just for Conversation...
	 * @param clob
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static String readClob(Clob clob) throws SQLException, IOException {
	    StringBuilder sb = new StringBuilder((int) clob.length());
	    Reader r = clob.getCharacterStream();
	    char[] cbuf = new char[2048];
	    int n;
	    while ((n = r.read(cbuf, 0, cbuf.length)) != -1) {
	        sb.append(cbuf, 0, n);
	    }
	    return sb.toString();
	}
	
	/**
     * get CSV
     * @throws IOException 
     * 
     */
    public String getCSV(String language) throws IOException {    	
    	return CsvUtil.getCSV(this, language);
    }    
    
    /**
     * write CSV from Result into BufferedWriter
     * @throws IOException 
     */
    public void writeCSV(Writer buf, String language) throws IOException 
    {  
    	CsvUtil.writeCSV(this, buf, language);
    }
	
	/**
     * get HTML from Result
	 * @throws IOException 
     */
    public String getHTML(String language, boolean xls) throws IOException
    {
    	return HtmlUtil.getHTML(this, language, xls);
    }
    
    /**
     * get HTML from Result
	 * @throws IOException 
     */
    public String getHTML(String language) throws IOException
    {
    	return HtmlUtil.getHTML(this, language);
    }
    
    /**
     * write HTML from Result into BufferedWriter
     * @throws IOException 
     */
    public void writeHTML(Writer buf, String language) throws IOException 
    { 
    	HtmlUtil.writeHTML(this, buf, language);
    }
    
    /**
     * write HTML from Result into BufferedWriter
     * @throws IOException 
     */
    public void writeHTML(Writer buf, String language, boolean xls) throws IOException 
    { 
    	HtmlUtil.writeHTML(this, buf, language, xls);
    }
    
    /**
     * get XML from Result
	 * @throws IOException 
     */
    public String getXML(boolean xls) throws IOException
    {
    	return XmlUtil.getXML(this, xls);
    }   
    
    /**
     * get XML from Result
	 * @throws IOException 
     */
    public String getXML() throws IOException
    {
    	return XmlUtil.getXML(this);
    }    
    
    /**
     * write XML from Result into BufferedWriter
     * @throws IOException 
     */
    public void writeXML(Writer buf) throws IOException 
    { 
    	XmlUtil.writeXML(this, buf);
    }
    
    /**
     * write HTML from Result into BufferedWriter
     * @throws IOException 
     */
    public void writeXML(Writer buf, boolean xls) throws IOException 
    { 
    	XmlUtil.writeXML(this, buf, xls);
    }
    
    /**
     * get XML from Result
	 * @throws IOException 
     */
    public static String getXML(List<ResultObject> results, List<String> sheetnames, boolean xls) throws IOException
    {
    	return XmlUtil.getXML(results, sheetnames, xls);
    }
    
    /**
     * get XML from Result
	 * @throws IOException 
     */
    public static String getXML(List<ResultObject> results) throws IOException
    {
    	return XmlUtil.getXML(results);
    }
    
    /**
     * write XML from Result into BufferedWriter
     * @throws IOException 
     */
    public static void writeXML(List<ResultObject> results, Writer buf) throws IOException 
    {     	
    	XmlUtil.writeXML(results, buf);
    }    
    
    /**
     * write XML from Result into BufferedWriter
     * @throws IOException 
     */   
    public static void writeXML(List<ResultObject> results, List<String> sheetnames, Writer buf, boolean xls) throws IOException 
    { 
    	XmlUtil.writeXML(results, sheetnames, buf, xls);
    }
    
}
