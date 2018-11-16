package com.twodigits.nativesqlpersistence;

/**
 * log interface for DatabaseAccessor
 * @author robert.diers
 *
 */
public interface DatabaseLogger {
	
	public void logInfo(String message);
	public void logWarn(String message);
	public void logError(String message);

}
