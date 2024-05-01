package main.java.scheduler.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class DivisionDictionary {
    private static Map<String, String> divisionCountryMap = new HashMap<>();

    public static void addDivision(String division, String country) {
        divisionCountryMap.put(division, country);
    }

    public static String getCountry(String division) {
        return divisionCountryMap.get(division);
    }

    public ObservableList<String> getDivisions(String country) {
        List<String> divisions = new ArrayList<>();

        for (Map.Entry<String, String> entry: divisionCountryMap.entrySet()) {
            if (entry.getValue().equals(country)) {
                divisions.add(entry.getKey());
            }
        }

        return FXCollections.observableArrayList(divisions);
    }

    public static ObservableList<String> getCountries() {
        Set<String> countries = new HashSet<>(divisionCountryMap.values());
        return FXCollections.observableArrayList(countries);
    }
}
