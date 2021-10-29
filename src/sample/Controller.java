package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Optional;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sample.datamodel.Contact;
import sample.datamodel.ContactData;

public class Controller {
    @FXML
    private TableView<Contact> tableView;

    @FXML
    private BorderPane mainBorderPane;

    private ContactData data;

    public void initialize(){
        data = new ContactData();
        data.loadContacts();

        tableView.getSelectionModel().selectFirst();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Contact, String> coLFirstName = new TableColumn<>("First Name");
        TableColumn<Contact, String> colLastName = new TableColumn<>("Last Name");
        TableColumn<Contact, String> colPhoneNumber = new TableColumn<>("Phone Number");
        TableColumn<Contact, String> colNotes = new TableColumn<>("Notes");

        coLFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));

        coLFirstName.setCellValueFactory(Contact -> Contact.getValue().nameProperty());
        colLastName.setCellValueFactory(Contact -> Contact.getValue().lastNameProperty());
        colPhoneNumber.setCellValueFactory(Contact -> Contact.getValue().phoneNumberProperty());
        colNotes.setCellValueFactory(Contact -> Contact.getValue().notesProperty());

        tableView.getColumns().addAll(coLFirstName, colLastName, colPhoneNumber, colNotes);

        tableView.setItems(data.getContacts());
    }

    @FXML
    public void showNewContactDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New Contact");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("contactDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e){
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            ContactController contactController = fxmlLoader.getController();
            Contact newContact = contactController.processResults();
            data.addContact(newContact);
            data.saveContacts();

            tableView.getSelectionModel().select(newContact);
        }
    }

    @FXML
    public void showEditContactDialog(){

        Contact selectedContact = tableView.getSelectionModel().getSelectedItem();

        if(selectedContact == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Contact Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the contact you want to edit.");
            alert.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit Contact");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("contactDialog.fxml"));

        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        ContactController contactController = fxmlLoader.getController();
        contactController.editContact(selectedContact);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            contactController.updateContact(selectedContact);

            data.saveContacts();

            tableView.getSelectionModel().select(selectedContact);

        }
    }

    @FXML
    public void deleteContact(){
        Contact selectedContact = tableView.getSelectionModel().getSelectedItem();

        if(selectedContact == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Contact Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the contact you want to delete");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Contact");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the selected contact: \n" +
                selectedContact.getFirstName() + " " + selectedContact.getLastName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            data.deleteContact(selectedContact);
            data.saveContacts();
        }
    }
}
