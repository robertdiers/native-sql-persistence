package com.twodigits.nativesqlpersistence;

public class DB2SqlFormater {
	
	/**
	 * Escapes an input string for to be used in queries on a db2 DB.
	 * @param arg the input string
	 * @return the escaped output string
	 */
	public static String escapeForDB2SQL(String arg) {
		
		if(arg==null) return new String("");
		int len = arg.length();
		StringBuffer dest = new StringBuffer(len*2);

		for (int i = 0; i < len; i++) {
		switch (arg.charAt(i)) {
			case '*':
				dest.append("%");		
				break;
			case '?':
				dest.append("_");		
				break;
			//case '"':
			//	dest.append("\\\"");		
			//	break;
			case '\'':
				dest.append("\'\'");
				break;
			//case '\\':
			//	dest.append("\\\\");		
			//	break;
			default:
				if (arg.charAt(i)<32) {
						dest.append(' ');		
						} else {
							dest.append(arg.charAt(i));		
						}
					}
		}

		return(dest.toString());
	}

}
