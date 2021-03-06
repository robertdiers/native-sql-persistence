package example;

import com.twodigits.nativesqlpersistence.DatabaseAccessor;

/**
 * singleton for database access to use connection pool feature
 * @author robert.diers
 *
 */
public class Database2 {

	private static Database2 instance = null;
	private DatabaseAccessor db = null;
	
	private Database2() {}
	
	/**
	 * get database access class
	 * @return
	 */
	public DatabaseAccessor getDatabaseAccessor() {
		if (db == null) {
			String url = "<database url>";
			String userid = "<database user>";
			String password = "<database password>";
			String driver = "<database driver class>";
			db = new DatabaseAccessor(url, userid, password, driver);
		}
		return db;
	}
	
	/**
	 * singleton access
	 * @return
	 */
	public static Database2 getInstance() {
		if (instance == null) {
			instance = new Database2();
		}
		return instance;
	}
	
}
