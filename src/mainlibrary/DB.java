/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlibrary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* @author bikash
*/
public class DB {

    private static final Logger LOGGER = Logger.getLogger(DB.class.getName());

    static {
        checkFilePermissions();
    }

    // check configuration file properties.
    private static void checkFilePermissions() {
        try {
            if (!Files.isReadable(Paths.get("db.properties"))) {
                throw new RuntimeException("Database properties file is not readable by the application.");
            }
            if (Files.getPosixFilePermissions(Paths.get("db.properties")).contains("rwx")) {
                throw new RuntimeException("Database properties file has too permissive permissions.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to check database properties file permissions.");
        }
    }

    public static Connection getConnection() {
        Properties props = new Properties();
        props = LibraryUtils.loadProperties();
        Connection con;
        try {
            props.put("useUnicode", "true");
            props.put("useServerPrepStmts", "false"); // use client-side prepared statement
            props.put("characterEncoding", "UTF-8"); // ensure charset is utf8 here
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(props.getProperty("db.connectionUrl"), props);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Unable to connect to database %s", connectionUrl));
        }
        return con;
    }

}
