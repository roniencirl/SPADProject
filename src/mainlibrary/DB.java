/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlibrary;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* @author bikash
*/
public class DB {

    private static final Logger LOGGER = Logger.getLogger(DB.class.getName());

    static {
        try {
            checkFilePermissions();
        } catch (URISyntaxException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // check configuration file properties to ensure it's not world readable.
    private static void checkFilePermissions() throws URISyntaxException {
        Path path;
        path = Paths.get("config/db.properties");
        Set<PosixFilePermission> permissions;
        try {
            FileAttributeView attributeView;
            attributeView = Files.getFileAttributeView(path, PosixFileAttributeView.class);
            if (attributeView != null) {
                permissions = Files.getPosixFilePermissions(path);
                if (permissions.contains(PosixFilePermission.OTHERS_READ) ||
                        permissions.contains(PosixFilePermission.OTHERS_WRITE) ||
                        permissions.contains(PosixFilePermission.OTHERS_EXECUTE)) {
                    LOGGER.log(Level.SEVERE, String.format(
                            "Database properties file has too permissive permissions: %s", permissions.toString()));
                }
            }
            if (!Files.isReadable(path)) {
                throw new RuntimeException("Database properties file is not readable by the application.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to check database properties file permissions.");
        }
    }

    public static Connection getConnection() {
        Properties props = new Properties();
        Properties connProps = new Properties();
        props = LibraryUtils.loadProperties();
        Connection con = null;
        try {
            connProps.put("useUnicode", "true");
            connProps.put("useServerPrepStmts", "false"); // use client-side prepared statement
            connProps.put("characterEncoding", "UTF-8"); // ensure charset is utf8 here
            connProps.put("user", props.getProperty("db.user"));
            connProps.put("password", props.getProperty("db.password"));
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(props.getProperty("db.connectionUrl"), connProps);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Unable to connect to database %s", props.getProperty("db.connectionUrl")));
        }
        return con;
    }

}
