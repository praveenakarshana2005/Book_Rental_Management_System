package controllers;

import model.Rental;
import model.RentalItem;
import service.RentalService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class ReturnsController {

    @FXML
    private TextField txtRentalNo;
    @FXML
    private TableView<RentalItem> tableItems;

    @FXML
    private TableColumn<RentalItem, String> colBook;
    @FXML
    private TableColumn<RentalItem, Integer> colQty;
    @FXML
    private TableColumn<RentalItem, Integer> colReturned;
    @FXML
    private TableColumn<RentalItem, Integer> colPending;

    @FXML
    private DatePicker dpReturnDate;
    @FXML
    private Label lblFine;
    @FXML
    private Label lblStatus;

    private final RentalService rentalService = new RentalService();
    private Rental loadedRental;

    @FXML
    public void initialize() {
        colBook.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getBook().getTitle()));
        colQty.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantity()).asObject());
        colReturned.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getReturnedQuantity()).asObject());
        colPending.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(
                        c.getValue().getQuantity() - c.getValue().getReturnedQuantity()
                ).asObject());
    }

    @FXML
    public void onLoadRental() {
        try {
            String rentalNo = txtRentalNo.getText().trim();
            var opt = rentalService.getRentalDao().findByRentalNo(rentalNo);

            if (opt.isEmpty()) {
                lblStatus.setText("Rental not found");
                return;
            }

            loadedRental = opt.get();
            tableItems.setItems(FXCollections.observableArrayList(loadedRental.getItems()));
            lblStatus.setText("Rental Loaded");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onProcessReturn() {
        if (loadedRental == null) return;

        try {
            LocalDate returnDate = dpReturnDate.getValue();
            double fine = rentalService.processReturn(loadedRental.getRentalNo(), returnDate);

            lblFine.setText("Total Fine: Rs. " + fine);
            lblStatus.setText("Return processed");

            onLoadRental();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
