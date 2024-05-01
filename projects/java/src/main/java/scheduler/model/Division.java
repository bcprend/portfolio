package main.java.scheduler.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.scheduler.DAO.DivisionsQuery;

import java.util.*;

/** Contains a mapping between divisions and their associated countries. */
public class Division {
    private static Map<String, String> divisionCountryMap = new HashMap<>();

    /**
     * Adds a division and its associated country to the mapping.
     * @param division the division to be added
     * @param country the associated country
     */
    public static void addEntry(String division, String country) {
        divisionCountryMap.put(division, country);
    }

    /**
     * Returns the associated country for a given division.
     * @param division the division to find
     */
    public static String getCountry(String division) {
        return divisionCountryMap.get(division);
    }

    /**
     * Returns a list of all divisions associated with a given country.
     * @param country the country to find divisions for
     */
    public static ObservableList<String> getDivisions(String country) {
        List<String> divisions = new ArrayList<>();

        for (Map.Entry<String, String> entry: divisionCountryMap.entrySet()) {
            if (entry.getValue().equals(country)) {
                divisions.add(entry.getKey());
            }
        }

        Collections.sort(divisions);

        return FXCollections.observableArrayList(divisions);
    }

    /** Returns a list of all unique countries in the hashmap. */
    public static ObservableList<String> getCountries() {
        Set<String> countries = new TreeSet<>(divisionCountryMap.values());

        return FXCollections.observableArrayList(countries);
    }

    /**
     * Returns the Division ID for a given division.
     * @param division the Name of the division.
     */
    public static int getDivisionId (String division) {
        return DivisionsQuery.retrieveDivisionId(division);
    }
}
