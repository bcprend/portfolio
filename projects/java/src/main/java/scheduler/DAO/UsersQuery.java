package main.java.scheduler.DAO;

import main.java.scheduler.model.Roster;
import main.java.scheduler.model.User;
import main.resources.helper.JDBC;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** This abstract class provides methods to query data related to users from the database. */
public abstract class UsersQuery {

    /** Retrieves all users from the database and populates the User roster. */
    public static void retrieveAllUsers() {

        try {
            String sql = "SELECT * FROM users";

            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("User_ID");
                String userName = rs.getString("User_Name");

                User user = new User(userId, userName);
                Roster.addToRoster(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Validates a given username and password against the database.
     * @param user The username to check
     * @param pass The password to check
     * @return True if the username and password match an entry in the database, otherwise False
     */
    public static boolean validateLogin(String user, String pass) {
        try {
            String sql = "SELECT Password FROM users WHERE User_Name = ?";

            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setString(1, user);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String dbPass = rs.getString("Password");
                if (dbPass.equals(pass)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
