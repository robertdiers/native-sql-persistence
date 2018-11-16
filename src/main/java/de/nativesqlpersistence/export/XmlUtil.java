package de.nativesqlpersistence.export;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.nativesqlpersistence.DBColumn;
import de.nativesqlpersistence.ResultObject;
import de.nativesqlpersistence.ResultRow;

/**
 * can create XML from Result
 * @author robert.diers
 *
 */
public class XmlUtil {
	
	//2009-04-20T00:00:00.000
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	/**
     * get XML from Result
	 * @throws IOException 
     */
    public static String getXML(ResultObject result, boolean xls) throws IOException
    {
    	StringWriter sw = new StringWriter();
    	List<ResultObject> reslist = new ArrayList<ResultObject>();
    	reslist.add(result);
    	List<String> sheetnames = new ArrayList<String>();
    	sheetnames.add("export");
    	writeXML(reslist, sheetnames, sw, xls);
    	return sw.toString();
    }
    
    /**
     * get XML from Result
	 * @throws IOException 
     */
    public static String getXML(List<ResultObject> results, List<String> sheetnames, boolean xls) throws IOException
    {
    	StringWriter sw = new StringWriter();
    	writeXML(results, sheetnames, sw, xls);
    	return sw.toString();
    }
    
    /**
     * get XML from Result
	 * @throws IOException 
     */
    public static String getXML(ResultObject result) throws IOException
    {
    	StringWriter sw = new StringWriter();
    	List<ResultObject> reslist = new ArrayList<ResultObject>();
    	reslist.add(result);
    	writeXML(reslist, null, sw, false);
    	return sw.toString();
    }
    
    /**
     * get XML from Result
	 * @throws IOException 
     */
    public static String getXML(List<ResultObject> results) throws IOException
    {
    	StringWriter sw = new StringWriter();
    	writeXML(results, null, sw, false);
    	return sw.toString();
    }
    
    /**
     * write XML from Result into BufferedWriter
     * @throws IOException 
     */
    public static void writeXML(ResultObject result, Writer buf) throws IOException 
    { 
    	List<ResultObject> reslist = new ArrayList<ResultObject>();
    	reslist.add(result);
    	writeXML(reslist, null, buf, false);
    }
    
    /**
     * write XML from Result into BufferedWriter
     * @throws IOException 
     */
    public static void writeXML(List<ResultObject> results, Writer buf) throws IOException 
    {     	
    	writeXML(results, null, buf, false);
    }
    
    /**
     * write HTML from Result into BufferedWriter
     * @throws IOException 
     */
    public static void writeXML(ResultObject result, Writer buf, boolean xls) throws IOException 
    { 
    	List<ResultObject> reslist = new ArrayList<ResultObject>();
    	reslist.add(result);
    	List<String> sheetnames = new ArrayList<String>();
    	sheetnames.add("export");
    	writeXML(reslist, sheetnames, buf, xls);
    }
    
    /**
     * write HTML from Result into BufferedWriter
     * @throws IOException 
     */
    public static void writeXML(List<ResultObject> results, List<String> sheetnames, Writer buf, boolean xls) throws IOException 
    { 
    	if (xls) {
    		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	    	buf.write(ExportConstants.line_separator);
	    	buf.append("<?mso-application progid=\"Excel.Sheet\"?>");
	    	buf.write(ExportConstants.line_separator);
	    	buf.append("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"");
	    	buf.write(ExportConstants.line_separator);
	    	buf.append(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"");
	    	buf.write(ExportConstants.line_separator);
	    	buf.append(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"");
	    	buf.write(ExportConstants.line_separator);
	    	buf.append(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"");
	    	buf.write(ExportConstants.line_separator);
	    	buf.append(" xmlns:html=\"http://www.w3.org/TR/REC-html40\">");
	    	buf.write(ExportConstants.line_separator);
	    	buf.append(" <ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\">");
		    buf.write(ExportConstants.line_separator);
	    	buf.append("  <ActiveSheet>"+results.size()+"</ActiveSheet>");
		    buf.write(ExportConstants.line_separator);
	    	buf.append(" </ExcelWorkbook>");
		    buf.write(ExportConstants.line_separator);
	    	buf.append(" <Styles>");
		    buf.write(ExportConstants.line_separator);
	    	buf.append("  <Style ss:ID=\"Default\" ss:Name=\"Normal\">");
		    buf.write(ExportConstants.line_separator);
	    	buf.append("   <Alignment ss:Vertical=\"Bottom\"/>");
		    buf.write(ExportConstants.line_separator);
	    	buf.append("   <Borders/>");
		    buf.write(ExportConstants.line_separator);
	    	buf.append("   <Interior/>");
		    buf.write(ExportConstants.line_separator);
	    	buf.append("   <NumberFormat/>");
		    buf.write(ExportConstants.line_separator);
	    	buf.append("   <Protection/>");
		    buf.write(ExportConstants.line_separator);
	    	buf.append("  </Style>");
		    buf.write(ExportConstants.line_separator);
		    buf.append("  <Style ss:ID=\"sHeader\">");
		    buf.write(ExportConstants.line_separator);
		    buf.append("   <Alignment ss:Horizontal=\"Left\" ss:Vertical=\"Top\"/>");
		    buf.write(ExportConstants.line_separator);		         
		    buf.append("   <Interior ss:Color=\"#A6A6A6\" ss:Pattern=\"Solid\"/>");
		    buf.write(ExportConstants.line_separator);			    
		    buf.append("   <Font ss:Bold=\"1\"/>");
		    buf.write(ExportConstants.line_separator);	
		    buf.append("  </Style>");
		    buf.write(ExportConstants.line_separator);		
		    buf.append("  <Style ss:ID=\"sGrey\">");
		    buf.write(ExportConstants.line_separator);	
		    buf.append("   <Interior ss:Color=\"#D9D9D9\" ss:Pattern=\"Solid\"/>");
		    buf.write(ExportConstants.line_separator);	
		    buf.append("  </Style>");
		    buf.write(ExportConstants.line_separator);	
		    buf.append("  <Style ss:ID=\"sDate\">");
		    buf.write(ExportConstants.line_separator);	
		    buf.append("   <NumberFormat ss:Format=\"dd/mm/yyyy\\ hh:mm:ss;@\"/>");
		    buf.write(ExportConstants.line_separator);
		    buf.append("  </Style>");
		    buf.write(ExportConstants.line_separator);
		    buf.append("  <Style ss:ID=\"sDateGrey\">");
		    buf.write(ExportConstants.line_separator);	
		    buf.append("   <NumberFormat ss:Format=\"dd/mm/yyyy\\ hh:mm:ss;@\"/>");
		    buf.write(ExportConstants.line_separator);
		    buf.append("   <Interior ss:Color=\"#D9D9D9\" ss:Pattern=\"Solid\"/>");
		    buf.write(ExportConstants.line_separator);
		    buf.append("  </Style>");
		    buf.write(ExportConstants.line_separator);	
		    buf.append("  <Style ss:ID=\"sNumber\">");
		    buf.write(ExportConstants.line_separator);	
		    buf.append("   <NumberFormat ss:Format=\"Standard\"/>/>");
		    buf.write(ExportConstants.line_separator);
		    buf.append("  </Style>");	
		    buf.write(ExportConstants.line_separator);	
		    buf.append("  <Style ss:ID=\"sNumberGrey\">");
		    buf.write(ExportConstants.line_separator);	
		    buf.append("   <NumberFormat ss:Format=\"Standard\"/>/>");
		    buf.write(ExportConstants.line_separator);
		    buf.append("   <Interior ss:Color=\"#D9D9D9\" ss:Pattern=\"Solid\"/>");
		    buf.write(ExportConstants.line_separator);
		    buf.append("  </Style>");
		    buf.write(ExportConstants.line_separator);
	    	buf.append(" </Styles>");
		    buf.write(ExportConstants.line_separator);
    	}
    	
    	//write sheets
    	int resultcounter = 0;
    	for (ResultObject result : results) {
    		resultcounter++;
    		
    		long rowcount = result.getResultRows().size();
	    	long columncount = result.getColumnsFromView().size();
    		
    		if (xls) {    			
    			String sheetname = "Sheet"+resultcounter;
    			if (sheetnames != null) {
    				sheetname = sheetnames.get(resultcounter-1);
    			}
    			buf.append(" <Worksheet ss:Name=\""+sheetname+"\">");
    	    	buf.write(ExportConstants.line_separator);    	    	
    	    	buf.append("  <Table ss:ExpandedColumnCount=\""+columncount+"\" ss:ExpandedRowCount=\""+(rowcount+1)+"\" x:FullColumns=\"1\" x:FullRows=\"1\" ss:DefaultRowHeight=\"15\">");
    	    	buf.write(ExportConstants.line_separator);
    	    	
    	    	//column headers for Excel    	
    	    	for (DBColumn col : result.getColumnsFromView())
    	        {
    	    		double width = 5.25 * (col.getColumnname().length()+2) + 25; //added filter dropdown
    	        	buf.append("   <Column ss:Width=\""+width+"\"/>");  	
    	            buf.write(ExportConstants.line_separator);
    	        }
    	        buf.append("   <Row ss:AutoFitHeight=\"0\" ss:StyleID=\"sHeader\">");
    	        buf.write(ExportConstants.line_separator);
    	        for (DBColumn col : result.getColumnsFromView())
    	        {
    	        	buf.append("    <Cell><Data ss:Type=\"String\">"+col.getColumnname()+"</Data></Cell>");    	        	
    	            buf.write(ExportConstants.line_separator);
    	        }
    	        buf.append("   </Row>");
    	        buf.write(ExportConstants.line_separator);
    		} else {
    			buf.append("<ResultObject id=\""+resultcounter+"\">");
    	    	buf.write(ExportConstants.line_separator);
    		}     
    		buf.flush();
	        
	        //data        
			int colorcount = 0;
			long rowcounter = 0;
	        for (ResultRow row : result.getResultRows())
	        {
	        	rowcounter++;
	        	
	        	if (xls) {
		            if (colorcount == 0) {
		            	buf.append("   <Row ss:AutoFitHeight=\"0\">");
		            	buf.write(ExportConstants.line_separator);
		            } else {
		            	buf.append("   <Row ss:AutoFitHeight=\"0\" ss:StyleID=\"sGrey\">");
		            	buf.write(ExportConstants.line_separator);
		            }
	        	} else {
	        		buf.append(" <ResultRow id=\""+rowcounter+"\">");
	        		buf.write(ExportConstants.line_separator);
	        	}
	            
	            for (DBColumn col : result.getColumnsFromView())
	            {            
	            	Object o = row.getObjectValue(col.getColumnname());
	            	if (o == null) {
	            		buf.append(getCell(col.getColumnname(), "String", "", xls, colorcount));
	            		buf.write(ExportConstants.line_separator);
	            	} else if (o instanceof java.util.Date) {
	            		try {
	            			buf.append(getCell(col.getColumnname(), "DateTime", formatDate(((java.util.Date)o).getTime()), xls, colorcount));
	            			buf.write(ExportConstants.line_separator);
	            		} catch (Exception e) {
	            			e.printStackTrace();
	            			buf.append(getCell(col.getColumnname(), "String", e.getMessage(), xls, colorcount));
	            			buf.write(ExportConstants.line_separator);
	            		}
	            	} else if (o instanceof java.sql.Date) {
	            		try {
	            			buf.append(getCell(col.getColumnname(), "DateTime", formatDate(((java.sql.Date)o).getTime()), xls, colorcount));
	            			buf.write(ExportConstants.line_separator);
	            		} catch (Exception e) {
	            			e.printStackTrace();
	            			buf.append(getCell(col.getColumnname(), "String", e.getMessage(), xls, colorcount));
	            			buf.write(ExportConstants.line_separator);
	            		}
	            	} else if (o instanceof Clob) {
	            		try {
	            			buf.append(getCell(col.getColumnname(), "String", ResultObject.readClob((Clob)o), xls, colorcount));
	            			buf.write(ExportConstants.line_separator);
	            		} catch (Exception e) {
	            			e.printStackTrace();
	            			buf.append(getCell(col.getColumnname(), "String", e.getMessage(), xls, colorcount));
	            			buf.write(ExportConstants.line_separator);
	            		}
	            	} else if (o instanceof String) {
	            		try {
	            			buf.append(getCell(col.getColumnname(), "String", ((String)o), xls, colorcount));
	            			buf.write(ExportConstants.line_separator);
	            		} catch (Exception e) {
	            			e.printStackTrace();
	            			buf.append(getCell(col.getColumnname(), "String", e.getMessage(), xls, colorcount));
	            			buf.write(ExportConstants.line_separator);
	            		}
	            	} else if (o instanceof BigDecimal) {
	            		try {
	            			BigDecimal bd = (BigDecimal)o;
	            			//force round 10 digits
	            			bd = bd.setScale(10, RoundingMode.HALF_UP);
	            			String value = bd.toString();
	            			buf.append(getCell(col.getColumnname(), "Number", value, xls, colorcount));          			
	            			buf.write(ExportConstants.line_separator);
	            		} catch (Exception e) {
	            			e.printStackTrace();
	            			buf.append(getCell(col.getColumnname(), "String", e.getMessage(), xls, colorcount));
	            			buf.write(ExportConstants.line_separator);
	            		}
	            	} else {
	            		buf.append(getCell(col.getColumnname(), "String", o.toString(), xls, colorcount));
	            		buf.write(ExportConstants.line_separator);
	            	}
	            }
	            if (xls) {
	            	buf.append("   </Row>");
	            	buf.write(ExportConstants.line_separator);
	            } else {
	            	buf.append(" </ResultRow>");
	            	buf.write(ExportConstants.line_separator);
	            }    
	            buf.flush();
	            
	            //colorcount
				colorcount++;
				if (colorcount == 2) colorcount = 0;
	        }	        
	        
	        if (xls) {
	        	buf.append("  </Table>");
		        buf.write(ExportConstants.line_separator);
		        buf.append("  <AutoFilter x:Range=\"R1C1:R1C"+columncount+"\" xmlns=\"urn:schemas-microsoft-com:office:excel\">");
		        buf.write(ExportConstants.line_separator);
		        buf.append("  </AutoFilter>");
		        buf.write(ExportConstants.line_separator);
    			buf.append(" </Worksheet>");
    	    	buf.write(ExportConstants.line_separator);    	    	
    		} else {
    			buf.append("</ResultObject>");
    	        buf.write(ExportConstants.line_separator);
    		}
    	}
        
        if (xls) {
        	buf.append("</Workbook>");
        	buf.write(ExportConstants.line_separator);
    	}
        
        buf.flush();
    }
    
    /**
     * get one sell
     * @param columnname
     * @param data
     * @param xls
     * @return
     */
    private static String getCell(String columnname, String type, String data, boolean xls, int colorcount) {
    	String result = "";
    	if (xls) {
    		if (type.equals("DateTime")) {		
    			String style = "sDate";
    			if (colorcount == 1) {
    				style = "sDateGrey";
    			}
    			result = "    <Cell ss:StyleID=\""+style+"\"><Data ss:Type=\""+type+"\">"+data+"</Data></Cell>";
    		} else if (type.equalsIgnoreCase("Number")) {
    			String style = "sNumber";
    			if (colorcount == 1) {
    				style = "sNumberGrey";
    			}
    			result = "    <Cell ss:StyleID=\""+style+"\"><Data ss:Type=\""+type+"\">"+data+"</Data></Cell>";
    		} else {
    			result = "    <Cell><Data ss:Type=\""+type+"\">"+escapeSpecialCharactersXls(data)+"</Data></Cell>";
    		}
		} else {
			result = "  <"+columnname+">"+escapeSpecialCharacters(data)+"</"+columnname+">";
		}
    	return result;
    }
    
    /**
     * format Date output for XML
     * @param millis
     * @return
     */
    private static String formatDate(long millis) {    	
    	return sdf.format(new java.util.Date(millis)) + ".000";
    }
    
    /**
     * XML has got predefined signs
     * @return
     */
    public static String escapeSpecialCharacters(String input) {
    	/*
	    	"   &quot;
			'   &apos;
			<   &lt;
			>   &gt;
			&   &amp;
    	*/
    	input = input.replaceAll("&", "&amp;");
    	input = input.replaceAll("\"", "&quot;");
    	input = input.replaceAll("'", "&apos;");
    	input = input.replaceAll("<", "&lt;");
    	input = input.replaceAll(">", "&gt;");
    	return input;
    }
    
    /**
     * Excel XML is not accepting all signs
     * @return
     */
    public static String escapeSpecialCharactersXls(String input) {
    	input = escapeSpecialCharacters(input);
    	//XML cannot use German special characters, other languages might require more of them
    	input = input.replaceAll("ü", "&uuml;");
    	input = input.replaceAll("Ü", "&Uuml;");
    	input = input.replaceAll("ö", "&ouml;");
    	input = input.replaceAll("Ö", "&Ouml;");
    	input = input.replaceAll("ä", "&auml;");
    	input = input.replaceAll("Ä", "&Auml;");
    	input = input.replaceAll("ß", "&szlig;");	
    	return input;
    }

}