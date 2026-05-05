package controllers;

import dao.BookDao;
import dao.CustomerDao;
import impl.BookDaoImpl;
import impl.CustomerDaoImpl;
import model.*;
import service.RentalService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class RentalController {

    @FXML
    private ComboBox<Customer> cmbCustomer;
    @FXML
    private ComboBox<Book> cmbBook;
    @FXML
    private TextField txtQty;
    @FXML
    private TextField txtFine;
    @FXML
    private TextField txtDays;

    @FXML
    private TableView<RentalItem> tableItems;
    @FXML
    private TableColumn<RentalItem, String> colTitle;
    @FXML
    private TableColumn<RentalItem, Integer> colQty;
    @FXML
    private TableColumn<RentalItem, Double> colFine;

    private final CustomerDao customerDao = new CustomerDaoImpl();
    private final BookDao bookDao = new BookDaoImpl();
    private final RentalService rentalService = new RentalService();

    private final ObservableList<RentalItem> items = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadCombos();

        colTitle.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getBook().getTitle()));
        colQty.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantity()).asObject());
        colFine.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getPerDayFine()).asObject());

        tableItems.setItems(items);
    }

    private void loadCombos() {
        try {
            cmbCustomer.setItems(FXCollections.observableArrayList(customerDao.findAll()));
            cmbBook.setItems(FXCollections.observableArrayList(bookDao.findAll()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onRefreshCustomers() {
        loadCombos();
    }

    @FXML
    public void onAddItem() {
        try {
            Book b = cmbBook.getValue();
            int qty = Integer.parseInt(txtQty.getText());
            double fine = Double.parseDouble(txtFine.getText());

            if (b == null) return;

            RentalItem ri = new RentalItem();
            ri.setBook(b);
            ri.setQuantity(qty);
            ri.setPerDayFine(fine);

            items.add(ri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onIssueRental() {
        try {
            Customer customer = cmbCustomer.getValue();
            int days = Integer.parseInt(txtDays.getText());

            if (customer == null || items.isEmpty()) return;

            Rental rental = new Rental();
            rental.setRentalNo("R" + System.currentTimeMillis());
            rental.setCustomer(customer);

            // TEMP issuedBy = fake user
            User u = new User();
            u.setId(1);
            rental.setIssuedBy(u);

            rental.setIssueDate(LocalDate.now());
            rental.setDueDate(LocalDate.now().plusDays(days));
            rental.setStatus("issued");

            rental.getItems().addAll(items);

            rentalService.createRental(rental);

            items.clear();
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Rental Issued Successfully!");
            a.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
