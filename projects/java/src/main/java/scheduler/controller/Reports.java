package main.java.scheduler.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.scheduler.model.Appointment;
import main.java.scheduler.model.Schedule;

import java.time.Duration;
import java.time.LocalTime;

/** Provides classes and methods for generating reports. */
public class Reports {

    /** Data structure for the tally by month and type report. */
    public static class MonthTypeReportData {
        private String month;
        private String type;
        private int count;

        /** Constructor */
        MonthTypeReportData(String month, String type) {
            this.month = month;
            this.type = type;
            this.count = 1;
        }

        /** Getter for month */
        public String getMonth() {return month.substring(0, 1) + month.substring(1).toLowerCase();}

        /** Getter for type */
        public String getType() {return type;}

        /** Getter for count */
        public int getCount() {return count;}

        /** Increments count */
        public void updateCount(){this.count++;}
    }

    /** Data structure for the average length by type report. */
    public static class TypeAvgLengthReportData {
        private String type;
        private int count;
        private double avgLength;

        /** Constructor */
        TypeAvgLengthReportData(String type, LocalTime start, LocalTime end) {
            this.type = type;
            this.count = 1;
            double durationSeconds = Duration.between(start, end).getSeconds();
            this.avgLength = (durationSeconds/3600);
        }

        /** Getter for type */
        public String getType() {return type;}

        /** Getter for count */
        public int getCount() {return count;}

        /** Getter for average appointment length */
        public double getAvgLength() {return (Math.round(avgLength * 100.0) / 100.0);}

        /** Increments count and calculates average length */
        public void updateEntry(LocalTime start, LocalTime end) {
            double currDuration = this.count * this.avgLength;
            double newDurationSeconds = Duration.between(start, end).getSeconds();
            double newTotalDuration = (newDurationSeconds/3600) + currDuration;
            this.count++;
            this.avgLength = newTotalDuration/this.count;
        }
    }

    /**
     * Generates data for the tally by month and type report based on the selected month and selected type.
     * Uses lambda expression predicates in the forEach methods for concise code.
     * @param selectedType The selected type for filtering data
     * @param selectedMonth The selected month for filtering data
     * @return An observable list containing the report data
     */
    public static ObservableList<MonthTypeReportData> getMonthTypeReportData(String selectedType, String selectedMonth) {
        ObservableList<MonthTypeReportData> MonthTypeReportAll = FXCollections.observableArrayList();
        ObservableList<MonthTypeReportData> MonthTypeReportFiltered = FXCollections.observableArrayList();

        // Generates typeReportAll list, which tallies all appointments by month and type
        for (Appointment a : Schedule.getSchedule()) {
            boolean match = false;

            for (MonthTypeReportData r : MonthTypeReportAll) {
                if (a.getDate().getMonth().toString().equalsIgnoreCase(r.getMonth()) && a.getType().equals(r.getType())) {
                    r.updateCount();
                    match = true;
                    break;
                }
            }

            if(!match) {
                MonthTypeReportData d = new MonthTypeReportData(a.getDate().getMonth().toString(), a.getType());
                MonthTypeReportAll.add(d);
            }
        }

        // Filter only by selected month
        if (selectedType.equals("All Types") && !(selectedMonth.equals("All Months"))) {
            MonthTypeReportAll.forEach((r -> {
                if (r.getMonth().equals(selectedMonth)) {
                    MonthTypeReportFiltered.add(r);
                }
            }));
        }
        // Filter only by selected type
        if(!(selectedType.equals("All Types")) && selectedMonth.equals("All Months")) {
            MonthTypeReportAll.forEach((r -> {
                if (r.getType().equals(selectedType)) {
                    MonthTypeReportFiltered.add(r);
                }
            }));
        }
        // Filter by selected type and selected month
        if(!(selectedType.equals("All Types") || selectedMonth.equals("All Months"))) {
            MonthTypeReportAll.forEach((r -> {
                if (r.getType().equals(selectedType) && r.getMonth().equals(selectedMonth)) {
                    MonthTypeReportFiltered.add(r);
                }
            }));
        }


        if (selectedType.equals("All Types") && selectedMonth.equals("All Months")) {
            return MonthTypeReportAll;
        } else {
            return MonthTypeReportFiltered;
        }
    }

    /**
     * Generates data for the average length by type report on the selected month and selected type.
     * Uses a lambda expression predicate in the forEach method for concise code.
     * @param selectedType The selected type for filtering data
     * @return An observable list containing the report data
     */
    public static ObservableList<TypeAvgLengthReportData> getTypeAvgLengthReportData (String selectedType) {
        ObservableList<TypeAvgLengthReportData> typeAvgLengthReportAll = FXCollections.observableArrayList();
        ObservableList<TypeAvgLengthReportData> typeAvgLengthReportFiltered = FXCollections.observableArrayList();

        for (Appointment a : Schedule.getSchedule()) {
            boolean match = false;

            for (TypeAvgLengthReportData r : typeAvgLengthReportAll) {
                if (a.getType().equals(r.getType())) {
                    r.updateEntry(a.getStart(), a.getEnd());
                    match = true;
                    break;
                }
            }

            if(!match) {
                TypeAvgLengthReportData d = new TypeAvgLengthReportData(a.getType(), a.getStart(), a.getEnd());
                typeAvgLengthReportAll.add(d);
            }
        }

        typeAvgLengthReportAll.forEach(( r -> {
            if (r.getType().equals(selectedType)) {
                typeAvgLengthReportFiltered.add(r);
            }
        }));

        if(selectedType.equals("All Types")) {
            return typeAvgLengthReportAll;
        } else {
            return typeAvgLengthReportFiltered;
        }
    }

}
