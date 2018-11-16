package de.nativesqlpersistence;

/**
 * column name and type
 * @author robert.diers
 *
 */
public class DBColumn {
	
	private String columnname = "";
	private String columntype = "";
	
	/**
	 * @return the columnname
	 */
	public String getColumnname() {
		return columnname;
	}
	
	/**
	 * @param columnname the columnname to set
	 */
	public void setColumnname(String columnname) {
		this.columnname = columnname;
	}
	
	/**
	 * @return the columntype
	 */
	public String getColumntype() {
		return columntype;
	}
	
	/**
	 * @param columntype the columntype to set
	 */
	public void setColumntype(String columntype) {
		this.columntype = columntype;
	}	

}
