package de.nativesqlpersistence;

/**
 * OracleSqlFormatter formats Oracle specific types. These include
 * Calendar, Date, Time, and TimeStamps. Generic types are handled
 * by SqlFormatter.
 */
public class OracleSqlFormater {

  /**
   * Format of Oracle date: 'YYYY-MM-DD HH24:MI:SS.#'
   */
  private static final String ymd24="'YYYY-MM-DD HH24:MI:SS.###'";

  /**
   * Formats Calendar object into Oracle TO_DATE String.
   * @param cal Calendar to be formatted
   * @return formatted TO_DATE function
   */
  public static String format(java.util.Calendar cal){
    return "TO_DATE('" + new java.sql.Timestamp(cal.getTime().getTime()) + "',"+ymd24+")";
  }
  
  /**
   * Formats Date object into Oracle TO_DATE String.
   * @param cal Calendar to be formatted
   * @return formatted TO_DATE function
   */
  public static String format(java.util.Date date){
    return format(new java.sql.Date(date.getTime()));
  }

  /**
   * Formats Date object into Oracle TO_DATE String.
   * @param date Date to be formatted
   * @return formatted TO_DATE function
   */
  public static String format(java.sql.Date date){
    return "TO_DATE('" + new java.sql.Timestamp(date.getTime()) + "',"+ymd24+")";
  }

  /**
   * Formats Time object into Oracle TO_DATE String.
   * @param time Time to be formatted
   * @return formatted TO_DATE function
   */
  public static String format(java.sql.Time time){
	java.util.Calendar cal = java.util.Calendar.getInstance();
    cal.setTime(new java.util.Date(time.getTime()));
    return "TO_DATE('" + cal.get(java.util.Calendar.HOUR_OF_DAY) + ":" +
      cal.get(java.util.Calendar.MINUTE) + ":" + cal.get(java.util.Calendar.SECOND) + "." +
      cal.get(java.util.Calendar.MILLISECOND) + "','HH24:MI:SS')";
  }

  /**
   * Formats Timestamp object into Oracle TO_DATE String.
   * @param timestamp Timestamp to be formatted
   * @return formatted TO_DATE function
   */
  public static String format(java.sql.Timestamp timestamp){
    return "TO_DATE('" + timestamp.toString() + "',"+ymd24+")";
  }

}

