# Native SQL Persistence

Framework is designed to provide simple database access using native SQL queries. 
It will care about connection handling and object closing statements to avoid connection leaks.
Containing a pure Java connection pool, it can be used in fat clients, web applications, batch jobs - everything based on Java.

Export features are available: 
CSV, HTML, XML, XML-Spreadsheets (Excel)

Can be used as XA and None-XA datasource, statement parameters are available.

### ATTENTION:
* You should use one DatabaseAccessor instance per database, so please use Singleton pattern in multi-thread environments like web applications. DatabaseAccessor is implementing DataSource interface, so you might use it directly in your existing code as an extension.
* Please use Prepared method calls for dynamic SQL, as they are less vulnerable to SQL injection!

### Features
* multiple native Java connection pools
* can be used with server connection pools
* CSV, HTML, XML, XML-Spreadsheet export functionality
* prepared and unprepared statements (please be careful unprepared ones are SQL injection vulnerable)
* code including example
* main class execution functionality (SQLExecuter)
* main class Bytes export functionality (SQLBytesExport)

[Download](target/native-sql-persistence.jar)

### Direct Usage
```
java -cp native-sql-persistence.jar:database-driver.jar com.twodigits.nativesqlpersistence.mainexec.SQLExecuter url DriverClass User Password "SQL"
```
```
java -cp native-sql-persistence.jar:database-driver.jar com.twodigits.nativesqlpersistence.mainexec.SQLExecuter url DriverClass User Password Encoding x.sql
```
```
java -cp native-sql-persistence.jar:database-driver.jar com.twodigits.nativesqlpersistence.mainexec.SQLBytesExport url DriverClass User Password "SQL"
```

