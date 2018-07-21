# RSS-Reader
Nimbo Project RSS Reader.
This Project Read rss and insert fully news items to database.
for more help run command ?l in console.

#How to Build
use `mvn install` Command to build the jar file

#How to Run
use `java -jar news-reader-1.0-SNAPSHOT.jar` to run program
Notice: You Should Run `rss-reader.sql` in your database before run in resources folder.

#Settings
place `databaseConfig.properties` next to jar file to customize database Connection.

#Dependencies
You need Maven to build Project. maven dependencies are on pom.xml
Default Driver installed is Mysql but you can implement DAO interfaces and make your own driver.