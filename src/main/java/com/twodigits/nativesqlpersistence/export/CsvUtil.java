package com.twodigits.nativesqlpersistence.export;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Clob;

import com.twodigits.nativesqlpersistence.DBColumn;
import com.twodigits.nativesqlpersistence.ResultObject;
import com.twodigits.nativesqlpersistence.ResultRow;

/**
 * can create CSV from Result
 * @author robert.diers
 *
 */
public class CsvUtil {
	
	/**
     * get CSV
     * @throws IOException 
     * 
     */
    public static String getCSV(ResultObject result, String language) throws IOException {
    	StringWriter sw = new StringWriter();
    	writeCSV(result, sw, language);
    	return sw.toString();
    }    
    
    /**
     * write CSV from Result into BufferedWriter
     * @throws IOException 
     */
    public static void writeCSV(ResultObject result, Writer buf, String language) throws IOException 
    {  
    	buf.append("sep=;");
    	buf.write(ExportConstants.line_separator);
    	
        for (DBColumn col : result.getColumnsFromView())
        {            
            buf.append("=\""+col.getColumnname()+"\";");         
        }        
        buf.write(ExportConstants.line_separator);
        buf.flush();
        
        //data
        for (ResultRow row : result.getResultRows())
        {            
            for (DBColumn col : result.getColumnsFromView())
            {       
            	Object o = row.getObjectValue(col.getColumnname());
            	if (o == null) {
            		buf.append(";");
            	} else if (o instanceof java.util.Date) {
            		try {
            			buf.append("=\""+ResultObject.formatDate(((java.util.Date)o).getTime())+"\";");
            		} catch (Exception e) {
            			e.printStackTrace();
            			buf.append(e.getMessage()+";");
            		}
            	} else if (o instanceof java.sql.Date) {
            		try {
            			buf.append("=\""+ResultObject.formatDate(((java.sql.Date)o).getTime())+"\";");
            		} catch (Exception e) {
            			e.printStackTrace();
            			buf.append(e.getMessage()+";");
            		}
            	} else if (o instanceof Clob) {
            		try {
            			buf.append("=\""+ResultObject.readClob((Clob)o)+"\";");
            		} catch (Exception e) {
            			e.printStackTrace();
            			buf.append(e.getMessage()+";");
            		}
            	} else if (o instanceof String) {
            		try {
            			buf.append("=\""+((String)o)+"\";");
            		} catch (Exception e) {
            			e.printStackTrace();
            			buf.append(e.getMessage()+";");
            		}
            	} else if (o instanceof BigDecimal) {
            		try {
            			BigDecimal bd = (BigDecimal)o;
            			String value = bd.toString();
            			if (language == null || !language.equalsIgnoreCase("de")) buf.append(value+";");
            			else buf.append("=\""+value.replaceAll("\\.", ",")+"\";");
            		} catch (Exception e) {
            			e.printStackTrace();
            			buf.append(e.getMessage()+";");
            		}
            	} else {
            		buf.append("=\""+o.toString()+"\";");
            	}                
            }            
            buf.write(ExportConstants.line_separator);          
            buf.flush();
        }
    }  

}
