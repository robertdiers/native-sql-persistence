package com.twodigits.nativesqlpersistence.connectionpool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class DBConnection {
	
	private Connection con;
	private Date created = new Date();
	private Date last_used = new Date();
	
	public DBConnection() {
		this.created = new Date();
		this.last_used = new Date();
	}

	/**
	 * @return the con
	 */
	public Connection getCon() {
		return con;
	}
	/**
	 * @param con the con to set
	 */
	public void setCon(Connection con) {
		this.con = con;
	}
	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}	
	/**
	 * @return the last_used
	 */
	public Date getLast_used() {
		return last_used;
	}
	/**
	 * @param last_used the last_used to set
	 */
	public void setLast_used(Date last_used) {
		this.last_used = last_used;
	}	

	public void setAutoCommit(boolean ac) throws SQLException {
		if (this.con != null) this.con.setAutoCommit(ac);
	}
	public long getID() {
		return created.getTime();
	}
}
