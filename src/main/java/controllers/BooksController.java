package controllers;

import dao.BookDao;
import impl.BookDaoImpl;
import javafx.event.ActionEvent;
import model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class BooksController {

    @FXML
    private TableView<Book> tableBooks;
    @FXML
    private TextField txtSearch;

    @FXML
    private TableColumn<Book, Integer> colId;
    @FXML
    private TableColumn<Book, String> colISBN;
    @FXML
    private TableColumn<Book, String> colTitle;
    @FXML
    private TableColumn<Book, String> colAuthor;
    @FXML
    private TableColumn<Book, String> colCategory;
    @FXML
    private TableColumn<Book, Integer> colTotalQty;
    @FXML
    private TableColumn<Book, Integer> colAvailable;


    @FXML
    private TextField txtISBN;
    @FXML
    private TextField txtTitle;
    @FXML
    private TextField txtAuthor;
    @FXML
    private TextField txtCategory;
    @FXML
    private TextField txtTotalQty;
    @FXML
    private TextField txtAvailableQty;

    private final BookDao bookDao = new BookDaoImpl();
    private final ObservableList<Book> data = FXCollections.observableArrayList();

    private Book selectedBook = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colISBN.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getIsbn()));
        colTitle.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));
        colAuthor.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getAuthor()));
        colCategory.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCategory()));
        colTotalQty.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getTotalQuantity()).asObject());
        colAvailable.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getAvailableQuantity()).asObject());

        loadAllBooks();
        tableBooks.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> loadSelectedBook(newVal));
    }


    private void loadAllBooks() {
        try {
            List<Book> list = bookDao.findAll();
            data.setAll(list);
            tableBooks.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadSelectedBook(Book b) {
        if (b == null) return;

        selectedBook = b;

        txtISBN.setText(b.getIsbn());
        txtTitle.setText(b.getTitle());
        txtAuthor.setText(b.getAuthor());
        txtCategory.setText(b.getCategory());
        txtTotalQty.setText(String.valueOf(b.getTotalQuantity()));
        txtAvailableQty.setText(String.valueOf(b.getAvailableQuantity()));
    }
    // ─────────────────────────────────────────────
    // SEARCH BOOKS
    // ─────────────────────────────────────────────
    @FXML
    public void onSearch() {
        try {
            String text = txtSearch.getText().trim();
            if (text.isEmpty()) loadAllBooks();
            else data.setAll(bookDao.search(text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ─────────────────────────────────────────────
    // VALIDATE FORM
    // ─────────────────────────────────────────────
    private boolean validate() {
        if (txtTitle.getText().isEmpty()) {
            showAlert("Title is required!");
            return false;
        }
        if (!txtTotalQty.getText().matches("\\d+")) {
            showAlert("Total Quantity must be a number");
            return false;
        }
        if (!txtAvailableQty.getText().matches("\\d+")) {
            showAlert("Available Quantity must be a number");
            return false;
        }
        return true;
    }
    // ─────────────────────────────────────────────
    // ADD BOOK
    // ─────────────────────────────────────────────
    @FXML
    public void onAdd() {
        if (!validate()) return;

        try {
            Book b = new Book();
            b.setIsbn(txtISBN.getText());
            b.setTitle(txtTitle.getText());
            b.setAuthor(txtAuthor.getText());
            b.setCategory(txtCategory.getText());
            b.setTotalQuantity(Integer.parseInt(txtTotalQty.getText()));
            b.setAvailableQuantity(Integer.parseInt(txtAvailableQty.getText()));

            bookDao.save(b);
            loadAllBooks();
            clearForm();

            showAlert("Book added successfully!");

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error adding book!");
        }
    }
    // ─────────────────────────────────────────────
    // UPDATE BOOK
    // ─────────────────────────────────────────────
    @FXML
    public void onUpdate(ActionEvent actionEvent) {
        if (selectedBook == null) {
            showAlert("Select a book to update!");
            return;
        }

        if (!validate()) return;

        try {
            selectedBook.setIsbn(txtISBN.getText());
            selectedBook.setTitle(txtTitle.getText());
            selectedBook.setAuthor(txtAuthor.getText());
            selectedBook.setCategory(txtCategory.getText());
            selectedBook.setTotalQuantity(Integer.parseInt(txtTotalQty.getText()));
            selectedBook.setAvailableQuantity(Integer.parseInt(txtAvailableQty.getText()));

            bookDao.update(selectedBook);
            loadAllBooks();

            showAlert("Book updated successfully!");

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Could not update book!");
        }
    }
    // ─────────────────────────────────────────────
    // DELETE BOOK
    // ─────────────────────────────────────────────
    @FXML
    public void onDelete(ActionEvent actionEvent) {
        if (selectedBook == null) {
            showAlert("Select a book to delete!");
            return;
        }

        try {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this book?",
                    ButtonType.YES, ButtonType.NO);

            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                bookDao.delete(selectedBook.getId());
                loadAllBooks();
                clearForm();
                showAlert("Book deleted!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Could not delete book!");
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
        txtISBN.clear();
        txtTitle.clear();
        txtAuthor.clear();
        txtCategory.clear();
        txtTotalQty.clear();
        txtAvailableQty.clear();

        tableBooks.getSelectionModel().clearSelection();
        selectedBook = null;
    }


    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.show();
    }
}
