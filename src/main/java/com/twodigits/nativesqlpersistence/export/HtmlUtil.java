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
 * can create HTML from Result
 * @author robert.diers
 *
 */
public class HtmlUtil {	
	
	/**
     * get HTML from Result
	 * @throws IOException 
     */
    public static String getHTML(ResultObject result, String language, boolean xls) throws IOException
    {
    	StringWriter sw = new StringWriter();
    	writeHTML(result, sw, language, xls);
    	return sw.toString();
    }
    
    /**
     * get HTML from Result
	 * @throws IOException 
     */
    public static String getHTML(ResultObject result, String language) throws IOException
    {
    	StringWriter sw = new StringWriter();
    	writeHTML(result, sw, language, false);
    	return sw.toString();
    }
    
    /**
     * write HTML from Result into BufferedWriter
     * @throws IOException 
     */
    public static void writeHTML(ResultObject result, Writer buf, String language) throws IOException 
    { 
    	writeHTML(result, buf, language, false);
    }
    
    /**
     * write HTML from Result into BufferedWriter
     * @throws IOException 
     */
    public static void writeHTML(ResultObject result, Writer buf, String language, boolean xls) throws IOException 
    { 
    	if (xls) {
	    	buf.append("<html xmlns:v=\"urn:schemas-microsoft-com:vml\"");
	    	buf.write(ExportConstants.line_separator);
	    	buf.append("xmlns:o=\"urn:schemas-microsoft-com:office:office\"");
	    	buf.write(ExportConstants.line_separator);
	    	buf.append("xmlns:x=\"urn:schemas-microsoft-com:office:excel\"");
	    	buf.write(ExportConstants.line_separator);
	    	buf.append("xmlns=\"http://www.w3.org/TR/REC-html40\">");
	    	buf.write(ExportConstants.line_separator);
	    	buf.append("<head></head>");
	    	buf.write(ExportConstants.line_separator);
	    	buf.append("<body>");
	    	buf.write(ExportConstants.line_separator);
    	}
    	
    	buf.append("<table border='1' frame='void'>");
    	buf.write(ExportConstants.line_separator);
        
        //columns
        buf.append("<tr>");
        buf.write(ExportConstants.line_separator);
        for (DBColumn col : result.getColumnsFromView())
        {
        	if (xls) {
        		buf.append("<th x:autofilter='all' bgcolor='lightgrey' align='left' valign='top'>"+col.getColumnname()+"</th>");
        	} else {
        		buf.append("<th>"+col.getColumnname()+"</th>");
        	}
            buf.write(ExportConstants.line_separator);
        }
        buf.append("</tr>");
        buf.write(ExportConstants.line_separator);
        buf.flush();
        
        //data        
		int colorcount = 0;
        for (ResultRow row : result.getResultRows())
        {
            buf.append("<tr>");
            buf.write(ExportConstants.line_separator);
            for (DBColumn col : result.getColumnsFromView())
            {            
            	Object o = row.getObjectValue(col.getColumnname());
            	if (o == null) {
            		buf.append(getTD(xls, colorcount)+"</td>");
            	} else if (o instanceof java.util.Date) {
            		try {
            			buf.append(getTD(xls, colorcount)+ResultObject.formatDate(((java.util.Date)o).getTime())+"</td>");
            		} catch (Exception e) {
            			e.printStackTrace();
            			buf.append(getTD(xls, colorcount)+e.getMessage()+"</td>");
            		}
            	} else if (o instanceof java.sql.Date) {
            		try {
            			buf.append(getTD(xls, colorcount)+ResultObject.formatDate(((java.sql.Date)o).getTime())+"</td>");
            		} catch (Exception e) {
            			e.printStackTrace();
            			buf.append(getTD(xls, colorcount)+e.getMessage()+"</td>");
            		}
            	} else if (o instanceof Clob) {
            		try {
            			buf.append(getTD(xls, colorcount)+ResultObject.readClob((Clob)o)+"</td>");
            		} catch (Exception e) {
            			e.printStackTrace();
            			buf.append(getTD(xls, colorcount)+e.getMessage()+"</td>");
            		}
            	} else if (o instanceof String) {
            		try {
            			buf.append(getTD(xls, colorcount)+((String)o)+"</td>");
            		} catch (Exception e) {
            			e.printStackTrace();
            			buf.append(getTD(xls, colorcount)+e.getMessage()+"</td>");
            		}
            	} else if (o instanceof BigDecimal) {
            		try {
            			BigDecimal bd = (BigDecimal)o;
            			String value = bd.toString();
            			if (language == null || !language.equalsIgnoreCase("de")) buf.append(getTD(xls, colorcount)+value+"</td>");
            			else buf.append(getTD(xls, colorcount)+value.replaceAll("\\.", ",")+"</td>");
            		} catch (Exception e) {
            			e.printStackTrace();
            			buf.append(getTD(xls, colorcount)+e.getMessage()+"</td>");
            		}
            	} else {
            		buf.append(getTD(xls, colorcount)+o.toString()+"</td>");
            	}
                buf.write(ExportConstants.line_separator);
            }
            buf.append("</tr>");
            buf.write(ExportConstants.line_separator);        
            buf.flush();
            
            //colorcount
			colorcount++;
			if (colorcount == ExportConstants.maxcolorcount) colorcount = 0;
        }
        
        buf.append("</table>");
        buf.write(ExportConstants.line_separator);
        
        if (xls) {
        	buf.append("</body>");
        	buf.write(ExportConstants.line_separator);
        	buf.append("</html>");
    		buf.write(ExportConstants.line_separator);
    	}
        
        buf.flush();
    }
    
    /**
     * set correct bgcolor
     * @param xls
     * @param colors
     * @param colorcount
     * @return
     */
    private static String getTD(boolean xls, int colorcount) {
    	if (xls) {
    		return "<td bgcolor='"+ExportConstants.colors[colorcount]+"' align='left' valign='top'>";
    	} else {
    		return "<td>";
    	}
    }    

}
