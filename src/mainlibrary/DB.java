/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlibrary;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* @author bikash
*/
public class DB {

    private static final Logger LOGGER = Logger.getLogger(DB.class.getName());

    private static String user;
    private static String password;
    private static String connectionUrl;

    static {
        checkFilePermissions();
        loadProperties();
    }

    // load properties from a configuration file
    private static void loadProperties() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")) {
            props.load(in);
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
            connectionUrl = props.getProperty("db.connectionUrl");
            if (connectionUrl.contains("useSSL=false")) {
                LOGGER.log(Level.WARNING, "WARNING: SSL is not enabled for production.");

            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load database properties");
        }
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
        Connection con;
        try {
            props.put("useUnicode", "true");
            props.put("useServerPrepStmts", "false"); // use client-side prepared statement
            props.put("characterEncoding", "UTF-8"); // ensure charset is utf8 here
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(connectionUrl, props);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Unable to connect to database %s", connectionUrl));
        }
        return con;
    }

}
