package controllers;

import dao.CustomerDao;
import impl.CustomerDaoImpl;
import javafx.event.ActionEvent;
import model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class CustomersController {

    @FXML
    private TableView<Customer> tableCustomers;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableColumn<Customer, Integer> colId;
    @FXML
    private TableColumn<Customer, String> colName;
    @FXML
    private TableColumn<Customer, String> colEmail;
    @FXML
    private TableColumn<Customer, String> colPhone;
    @FXML
    private TableColumn<Customer, String> colAddress;
    @FXML
    private TableColumn<Customer, String> colMembership;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextArea txtAddress;
    @FXML
    private TextField txtMembership;

    @FXML
    private Button btnAdd;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnClear;

    private final CustomerDao customerDao = new CustomerDaoImpl();
    private final ObservableList<Customer> data = FXCollections.observableArrayList();

    private Customer selectedCustomer = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        colEmail.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));
        colPhone.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPhone()));
        colAddress.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getAddress()));
        colMembership.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMembershipNo()));

        loadAll();


        tableCustomers.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> loadSelectedCustomer(newVal));
    }

    private void loadAll() {
        try {
            List<Customer> list = customerDao.findAll();
            data.setAll(list);
            tableCustomers.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSelectedCustomer(Customer c) {
        if (c == null) return;

        selectedCustomer = c;

        txtName.setText(c.getName());
        txtEmail.setText(c.getEmail());
        txtPhone.setText(c.getPhone());
        txtAddress.setText(c.getAddress());
        txtMembership.setText(c.getMembershipNo());
    }

    @FXML
    public void onSearch() {
        try {
            String txt = txtSearch.getText().trim();
            if (txt.isEmpty()) loadAll();
            else data.setAll(customerDao.search(txt));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────
    // ADD CUSTOMER
    // ─────────────────────────────────────────────
    @FXML
    public void onAdd() {
        try {
            if (txtName.getText().isEmpty()) {
                showAlert("Name is required!");
                return;
            }

            Customer c = new Customer();
            c.setName(txtName.getText());
            c.setEmail(txtEmail.getText());
            c.setPhone(txtPhone.getText());
            c.setAddress(txtAddress.getText());

            if (txtMembership.getText().isEmpty()) {
                c.setMembershipNo("M" + System.currentTimeMillis());
            } else {
                c.setMembershipNo(txtMembership.getText());
            }

            customerDao.save(c);
            loadAll();
            clearForm();

            showAlert("Customer added successfully!");

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error adding customer!");
        }
    }

    // ─────────────────────────────────────────────
    // UPDATE CUSTOMER
    // ─────────────────────────────────────────────
    @FXML
    public void onUpdate(ActionEvent actionEvent) {
        if (selectedCustomer == null) {
            showAlert("Select a customer to update!");
            return;
        }

        try {
            selectedCustomer.setName(txtName.getText());
            selectedCustomer.setEmail(txtEmail.getText());
            selectedCustomer.setPhone(txtPhone.getText());
            selectedCustomer.setAddress(txtAddress.getText());
            selectedCustomer.setMembershipNo(txtMembership.getText());

            customerDao.update(selectedCustomer);
            loadAll();
            showAlert("Customer updated successfully!");

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Could not update customer!");
        }
    }

    // ─────────────────────────────────────────────
    // DELETE CUSTOMER
    // ─────────────────────────────────────────────
    @FXML
    public void onDelete(ActionEvent actionEvent) {
        if (selectedCustomer == null) {
            showAlert("Select a customer to delete!");
            return;
        }

        try {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this customer?",
                    ButtonType.YES, ButtonType.NO);

            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                customerDao.delete(selectedCustomer.getId());
                loadAll();
                clearForm();
                showAlert("Customer deleted!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Could not delete customer!");
        }
    }

    // ─────────────────────────────────────────────
    // CLEAR FORM
    // ─────────────────────────────────────────────
    @FXML
    public void onClear(ActionEvent actionEvent) {
        clearForm();
    }

    private void clearForm() {
        txtName.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtAddress.clear();
        txtMembership.clear();
        tableCustomers.getSelectionModel().clearSelection();
        selectedCustomer = null;
    }

    // ─────────────────────────────────────────────
    // ALERT MESSAGE
    // ─────────────────────────────────────────────
    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.show();
    }
}
