package main.java.scheduler.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.java.scheduler.model.Customer;
import main.java.scheduler.model.Division;
import main.java.scheduler.model.Roster;

import java.net.URL;
import java.util.ResourceBundle;

/** Controller class for the customer form. */
public class CustomerFormController implements Initializable {
    public Label labelCustomerHeader;
    public Label labelCustomerId;
    public Label labelError;
    public TextField fieldName;
    public TextField fieldPhone;
    public TextField fieldAddress;
    public TextField fieldPost;
    public ComboBox comboCountry;
    public ComboBox comboDivision;
    public Button buttonCancel;
    public Button buttonSave;

    private Customer selectedCustomer = null;
    private boolean editMode = false;
    private String css = "-fx-border-color: red;";

    /** Initializes the customer form view. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelCustomerHeader.setText("Add New Customer");
        comboCountry.setItems(Division.getCountries());  // Populates the country combo box
    }

    /**
     * Sets the selected customer.
     * Populates the form fields and controls with the data of the customer selected for editing.
     * @param selectedCustomer the customer to be edited
     */
    public void editCustomer (Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
        this.editMode = true;
        labelCustomerHeader.setText("Edit Existing Customer");

        if(selectedCustomer != null) {
            String selectedCountry = Division.getCountry(selectedCustomer.getDivision());
            comboCountry.setValue(selectedCountry);
            comboDivision.setItems(Division.getDivisions(selectedCountry));
            comboDivision.setValue(selectedCustomer.getDivision());

            labelCustomerId.setText(String.valueOf(selectedCustomer.getId()));
            fieldName.setText(selectedCustomer.getName());
            fieldPhone.setText(selectedCustomer.getPhone());
            fieldAddress.setText(selectedCustomer.getAddress());
            fieldPost.setText(selectedCustomer.getPost());
        }
    }

    /**
     * Collects and validates form data.
     * Creates/updates customers with collected form data.
     * Closes customer form.
     * @param actionEvent The action event triggered by the save button
     */
    public void onSaveClick(ActionEvent actionEvent) {
        boolean formIsComplete = validateForm();

        if(formIsComplete) {
            try {
                String name = fieldName.getText().trim().substring(0, Math.min(50, fieldName.getText().trim().length()));
                String phone = fieldPhone.getText().trim().substring(0, Math.min(50, fieldPhone.getText().trim().length()));
                String division = comboDivision.getValue().toString();
                String address = fieldAddress.getText().trim().substring(0, Math.min(100, fieldAddress.getText().trim().length()));
                String post = fieldPost.getText().trim().substring(0, Math.min(50, fieldPost.getText().trim().length()));

                if (editMode) {
                    int id = selectedCustomer.getId();
                    Roster.updateCustomer(id, name, address, post, phone, division);
                } else {
                    Roster.createCustomer(name, address, post, phone, division);
                }

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error Saving Data");
                alert.setContentText("An error occurred while saving the data.");

                alert.showAndWait();
                return;
            }
        } else {
            return;
        }

        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    /** Closes the form without saving. */
    public void onCancelClick() {
        Stage stage = (Stage) buttonCancel.getScene().getWindow();
        stage.close();
    }

    /** Populates the division combo box with the associated states/provinces when a country is selected. */
    public void onCountrySelected() {
        String selectedCountry = comboCountry.getValue().toString();
        comboDivision.setItems(Division.getDivisions(selectedCountry));
        comboDivision.setPromptText("Select state/province");
    }

    /**
     * Verifies that all customer details have been filled.
     * Highlights fields and displays an error message if validation fails.
     * @return True if all form is valid, otherwise False
     */
    private boolean validateForm () {
        boolean isValid = true;

        if (fieldName.getText().trim().isEmpty()) {
            fieldName.setStyle(css);
            isValid = false;
        } else fieldName.setStyle("");

        if (fieldAddress.getText().trim().isEmpty()) {
            fieldAddress.setStyle(css);
            isValid = false;
        } else fieldAddress.setStyle("");

        if (fieldPost.getText().trim().isEmpty()) {
            fieldPost.setStyle(css);
            isValid = false;
        } else fieldPost.setStyle("");

        if (fieldPhone.getText().trim().isEmpty()) {
            fieldPhone.setStyle(css);
            isValid = false;
        } else fieldPhone.setStyle("");

        if (comboDivision.getValue() == null) {
            comboDivision.setStyle(css);
            isValid = false;
        } else comboDivision.setStyle("");

        if (!isValid) {
            labelError.setText("Error: Please fill in all required fields");
        } else {
            labelError.setText("");
        }

        return isValid;
    }

}
