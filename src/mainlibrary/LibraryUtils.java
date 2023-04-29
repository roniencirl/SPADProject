package mainlibrary;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class LibraryUtils {

    private static final Logger LOGGER = Logger.getLogger(LibraryUtils.class.getName());

    static Properties loadProperties() {
        Properties props = new Properties();
        Map<String, String> dbProps = new HashMap<String, String>();

        try (FileInputStream in = new FileInputStream("config/db.properties")) {
            props.load(in);
            if (props.getProperty("db.connectionUrl").contains("useSSL=false")) {
                LOGGER.log(Level.WARNING, "WARNING: SSL is not enabled for production.");

            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load database properties from file.");
        }
        return props;
    }

    public static boolean validatePassword(String password) {
        boolean isValid = true;

        /*
         * Validate password
         * Based on our testing partitions.
         * Invalid – less than 8 characters, greater than 100* characters.
         * Valid – any alphabetic and punctuations characters combined.
         */
        if (password == null || password.length() < 8 || password.length() > 100 ) {
            isValid = false;
        }
        System.out.println("password:"+  isValid);

        return isValid;
    }

    public static boolean validateUsername(String username) {
        boolean isValid = true;

        /*
         * Validate username
         * Based on our testing partitions.
         * Invalid – no value, greater than 100* characters, spaces, non-alphanumeric
         * characters.
         * Valid – any alphanumeric characters.
         */
        if (username == null || username.trim().isEmpty() || username.length() > 100 ||
                !username.matches("^[a-zA-Z0-9]+$")) {
            isValid = false;
        }
        System.out.println("username:" + isValid);
        return isValid;
    }

    public static boolean validateEmail(String email) {
        boolean isValid = true;
        if (email == null || email.trim().isEmpty()) {
        
            isValid = false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        isValid = email.matches(emailRegex);
        System.out.println("email:" + isValid);
        return isValid;

    }

    /*  Date is not input from user, genereted by system
    public static boolean validateDate(String date) {
        boolean isValid = true;
        if (date == null) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date pdate = sdf.parse(date);
   
            System.out.println(isValid);

            return isValid;
        } catch (ParseException e) {
            return false;
        }
    }
*/
    // Use the password and a random salt to generates a salted hash
    public static String createHashedPassword(String password) {
        String generatedHash = null;
        int iterations = 600000; // OWASP recommendations
        int keyLength = 512;
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
            SecretKey secretKey = factory.generateSecret(spec);
            byte[] encoded = secretKey.getEncoded();
            generatedHash = Base64.getEncoder().encodeToString(encoded);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.log(Level.SEVERE, "Password hash failed due to NoSuchAlgorithm or InvalidKeySpec.");
        }
        // return the base 64 encoded salt for this password and the base64 encoded
        // hashed password, separated by a colon
        return Base64.getEncoder().encodeToString(salt) + ":" + generatedHash;
    }

    // Given a password and a salt, return the salted hash
    public static String hashPassword(String password, String saltString) {
        String generatedHash = null;
        int iterations = 600000; // OWASP recommendations
        int keyLength = 512;
        byte[] salt = Base64.getDecoder().decode(saltString);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
            SecretKey secretKey = factory.generateSecret(spec);
            byte[] encoded = secretKey.getEncoded();
            generatedHash = Base64.getEncoder().encodeToString(encoded);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.log(Level.SEVERE, "Password hash failed due to NoSuchAlgorithm or InvalidKeySpec.");
        }
        // return the hashed password
        return generatedHash;

    }
}
