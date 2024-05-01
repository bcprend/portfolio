package main.resources.helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class countriesQuery {

    public static ObservableList<String> select() throws SQLException {
        ObservableList<String> countriesList = FXCollections.observableArrayList();

        String sql = "SELECT * FROM countries";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            countriesList.add(rs.getString("Country"));
        }

        return countriesList;
    }

    public static int getCountryId(String country) throws SQLException {
        int countryId = 0;
        String sql = "SELECT * FROM countries WHERE Country = ?";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, country);

        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            countryId = rs.getInt("Country_ID");
        }

        return countryId;
    }


}
