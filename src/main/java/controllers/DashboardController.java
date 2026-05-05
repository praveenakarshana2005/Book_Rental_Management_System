package controllers;

import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private Label lblWelcome;

    private User loggedUser;

    public void initData(User user) {
        this.loggedUser = user;
        lblWelcome.setText("Welcome, " + user.getFullName() + "  (" + user.getRole().getRoleName() + ")");
    }

    private void openWindow(String fxml, String title) {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/view/" + fxml));
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Navigation buttons
    @FXML
    public void onOpenBooks() {
        openWindow("books.fxml", "Books Management");
    }

    @FXML
    public void onOpenCustomers() {
        openWindow("customers.fxml", "Customers");
    }

    @FXML
    public void onOpenRental() {
        openWindow("rental.fxml", "New Rental");
    }

    @FXML
    public void onOpenReturns() {
        openWindow("returns.fxml", "Return Books");
    }

    @FXML
    public void onOpenReports() {
        openWindow("reports.fxml", "Reports & History");
    }

    public User getLoggedUser() {
        return loggedUser;
    }
}
