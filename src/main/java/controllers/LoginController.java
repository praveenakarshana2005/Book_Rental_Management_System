package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    @FXML
    public void onLogin(ActionEvent event) {

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please enter both username and password").show();
            return;
        }

        final String adminUser = "admin";
        final String adminPass = "admin123";

        final String normalUser = "user";
        final String normalPass = "user";

        try {

            if (username.equals(adminUser) && password.equals(adminPass)) {
                loadDashboard("/view/dashboard.fxml"); // Admin Dashboard
                new Alert(Alert.AlertType.INFORMATION, "Admin login successful!").show();
            }
            else if (username.equals(normalUser) && password.equals(normalPass)) {
                loadDashboard("/view/dashboard2.fxml");
                new Alert(Alert.AlertType.INFORMATION, "User login successful!").show();
            }
            else {
                new Alert(Alert.AlertType.ERROR, "Invalid username or password").show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading dashboard").show();
        }
    }

    private void loadDashboard(String path) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(path));

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Dashboard");
        stage.show();


        Stage loginStage = (Stage) txtUsername.getScene().getWindow();
        loginStage.close();
    }
}
