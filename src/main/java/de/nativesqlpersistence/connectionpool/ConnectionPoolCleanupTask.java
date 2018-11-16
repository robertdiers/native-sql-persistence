package de.nativesqlpersistence.connectionpool;

import java.util.TimerTask;

/**
 * cleanup ConnectionPool
 * @author robert.diers
 */
class ConnectionPoolCleanupTask extends TimerTask {	
	
	ConnectionPoolManager manager;
	
	public ConnectionPoolCleanupTask(ConnectionPoolManager manager) {
		this.manager = manager;
	}
		 
	/**
	 * force close all connections
	 */
    public void run() {
    	try {
        	this.manager.closeAllConnections();            
		} catch (Exception e) {			
			e.printStackTrace();
		}
    }    
   
}
