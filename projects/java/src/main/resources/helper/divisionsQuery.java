package main.resources.helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.scheduler.model.Customer;
import main.java.scheduler.model.Roster;
import main.java.scheduler.util.DivisionDictionary;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class divisionsQuery {

    public static ObservableList<String> byCountry(int countryId) throws SQLException {
        ObservableList<String> divisionsList = FXCollections.observableArrayList();

        String sql = "SELECT Division FROM first_level_divisions WHERE Country_ID = ?";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, countryId);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            divisionsList.add(rs.getString("Division"));
        }

        return divisionsList;
    }

    public static int getDivisionId(String division) throws SQLException {
        int divisionId = 0;
        String sql = "SELECT Division_ID FROM first_level_divisions WHERE Division = ?";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, division);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            divisionId = rs.getInt("Division_ID");
        }

        return divisionId;
    }


    public static void getAllDivisions() throws SQLException {
        String sql =
                "SELECT d.Division, c.Country " +
                "FROM first_level_divisions d " +
                "JOIN countries c ON d.Country_ID = c.Country_ID";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            String division = rs.getString("Division");
            String country = rs.getString("Country");
            DivisionDictionary.addDivision(division, country);
        }
    }
}
