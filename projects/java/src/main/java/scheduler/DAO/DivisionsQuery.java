package main.java.scheduler.DAO;

import main.java.scheduler.model.Division;
import main.resources.helper.JDBC;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** This abstract class provides methods to query data related to divisions from the database. */
public abstract class DivisionsQuery {

    /**
     * Returns the division ID based on the division name.
     * @param division the name of the division
     */
    public static int retrieveDivisionId(String division) {
        int divisionId = 0;

        try {
            String sql = "SELECT Division_ID FROM first_level_divisions WHERE Division = ?";

            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setString(1, division);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                divisionId = rs.getInt("Division_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return divisionId;
    }

    /** Retrieves all divisions from the database and populates the Division model. */
    public static void retrieveAllDivisions() {
        try {
            String sql =
                    "SELECT d.Division, c.Country " +
                    "FROM first_level_divisions d " +
                    "JOIN countries c ON d.Country_ID = c.Country_ID";

            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String division = rs.getString("Division");
                String country = rs.getString("Country");
                Division.addEntry(division, country);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
