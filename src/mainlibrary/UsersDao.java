/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlibrary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bikash
 */
public class UsersDao {

    private static final Logger LOGGER = Logger.getLogger(DB.class.getName());

    public static boolean validate(String name, String password) {
        boolean status = false;

        if (!LibraryUtils.validateUsername(name) || !LibraryUtils.validatePassword(password)) {
            LOGGER.log(Level.INFO, ("Username or password are not compliant with requirements."));
            return status;
        }

        try {
            Connection con = DB.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "select UserName from Users where Username(name) and UserPass(password) values(?,?)");
            ps.setString(1, name);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            status = rs.next();
            con.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to validate Username and Password in the database.");
        }
        return status;
    }

    public static boolean CheckIfAlready(String UserName) {
        boolean status = false;

        if (!LibraryUtils.validateUsername(UserName)) {
            LOGGER.log(Level.INFO, ("Username is not compliant with requirements."));
            return status;
        }
        try {
            Connection con = DB.getConnection();
            PreparedStatement ps = con.prepareStatement("select UserName from Users where UserName(name) values(?)");
            ps.setString(1, UserName);
            ResultSet rs = ps.executeQuery();
            status = rs.next();
            con.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to check if Username is already in the database.");
        }
        return status;

    }

    public static int AddUser(String User, String UserPass, String UserEmail, String Date) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change
        // body of generated methods, choose Tools | Templates.
        int status = 0;

        if (!LibraryUtils.validateUsername(User) || !LibraryUtils.validatePassword(UserPass)) {
            LOGGER.log(Level.INFO, ("Username or password are not compliant with requirements."));
            return status;
        }

        try {

            Connection con = DB.getConnection();
            PreparedStatement ps = con
                    .prepareStatement("insert into Users(UserPass,RegDate,UserName,Email) values(?,?,?,?)");
            ps.setString(1, UserPass);
            ps.setString(2, Date);
            ps.setString(3, User);
            ps.setString(4, UserEmail);
            status = ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Unable to add new user to database."));
        }
        return status;

    }

}
