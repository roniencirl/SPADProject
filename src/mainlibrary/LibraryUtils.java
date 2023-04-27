package mainlibrary;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class LibraryUtils {

    public static boolean validatePassword(String password) {
        boolean isValid = true;

        /*
         * Validate password
         * Based on our testing partitions.
         * Invalid – less than 8 characters, greater than 100* characters.
         * Valid – any alphabetic and punctuations characters combined.
         */
        if (password == null || password.length() < 8 || password.length() > 100 ||
                !password.matches("^[a-zA-Z!@#$%^&*()_+\\-={}|\\[\\]\\\\:\";'<>?,./]+$")) {
            isValid = false;
        }

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

        return isValid;
    }

    public static boolean validateEmail(String email) {

        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);

    }

    public static boolean validateDate(String date) {
        boolean isValid = true;
        if (date == null) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date pdate = sdf.parse(date);
            return isValid;
        } catch (ParseException e) {
            return false;
        }
    }

}
