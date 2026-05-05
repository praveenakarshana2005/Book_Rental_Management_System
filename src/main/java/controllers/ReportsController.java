package controllers;

import com.sun.javafx.font.FontFactory;
import dao.ReportDao;
import impl.ReportDaoImpl;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import model.ReportRental;
import model.ReportReturn;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


public class ReportsController {

    @FXML
    private DatePicker dpFrom;
    @FXML
    private DatePicker dpTo;
    @FXML
    private ComboBox<String> cmbCustomer;


    @FXML
    private TableView<ReportRental> tableRentals;
    @FXML
    private TableColumn<ReportRental, ?> colRentId;
    @FXML
    private TableColumn<ReportRental, ?> colRentCustomer;
    @FXML
    private TableColumn<ReportRental, ?> colRentDate;
    @FXML
    private TableColumn<ReportRental, ?> colRentBooks;
    @FXML
    private TableColumn<ReportRental, ?> colRentTotal;
    @FXML
    private TableView<ReportReturn> tableReturns;
    @FXML
    private TableColumn<ReportReturn, ?> colReturnId;
    @FXML
    private TableColumn<ReportReturn, ?> colReturnCustomer;
    @FXML
    private TableColumn<ReportReturn, ?> colReturnDate;
    @FXML
    private TableColumn<ReportReturn, ?> colReturnBooks;
    @FXML
    private TableColumn<ReportReturn, ?> colReturnFine;

    private final ReportDao reportDao = new ReportDaoImpl();

    @FXML
    public void initialize() {
        loadCustomers();
        loadTables();
    }


    private void loadCustomers() {
        try {
            cmbCustomer.setItems(FXCollections.observableArrayList(reportDao.loadCustomerNames()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadTables() {
        try {
            tableRentals.setItems(FXCollections.observableArrayList(reportDao.getAllRentalHistory()));
            tableReturns.setItems(FXCollections.observableArrayList(reportDao.getAllReturnHistory()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onFilterReports() {

        LocalDate from = dpFrom.getValue();
        LocalDate to = dpTo.getValue();
        String customer = cmbCustomer.getValue();

        List<ReportRental> rentals = reportDao.filterRentalHistory(from, to, customer);
        List<ReportReturn> returns = reportDao.filterReturnHistory(from, to, customer);

        tableRentals.setItems(FXCollections.observableArrayList(rentals));
        tableReturns.setItems(FXCollections.observableArrayList(returns));
    }

    @FXML
    private void onClearFilters() {
        dpFrom.setValue(null);
        dpTo.setValue(null);
        cmbCustomer.setValue(null);

        loadTables();
    }


    // ----------------------------------------------------
    // PDF EXPORT — RETURN HISTORY
    // ----------------------------------------------------
    @FXML
    private void onExportRentalPDF(ActionEvent actionEvent) {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Rental Report as PDF");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = chooser.showSaveDialog(null);

        if (file == null) return;

        try (FileOutputStream fos = new FileOutputStream(file)) {

            Document pdf = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            PdfWriter.getInstance(pdf, fos);
            pdf.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font head = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

            pdf.add(new Paragraph("Rental History Report\n\n", titleFont));

            PdfPTable table = new PdfPTable(5);
            table.setWidths(new int[]{10, 25, 15, 40, 10});
            table.setWidthPercentage(100);

            table.addCell(new PdfPCell(new Phrase("ID", head)));
            table.addCell(new PdfPCell(new Phrase("Customer", head)));
            table.addCell(new PdfPCell(new Phrase("Date", head)));
            table.addCell(new PdfPCell(new Phrase("Books", head)));
            table.addCell(new PdfPCell(new Phrase("Qty", head)));

            for (ReportRental r : tableRentals.getItems()) {
                table.addCell(new PdfPCell(new Phrase(String.valueOf(r.getRentalId()), normal)));
                table.addCell(new PdfPCell(new Phrase(r.getCustomer(), normal)));
                table.addCell(new PdfPCell(new Phrase(r.getDate(), normal)));
                table.addCell(new PdfPCell(new Phrase(r.getBooks(), normal)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(r.getTotalQty()), normal)));
            }

            pdf.add(table);
            pdf.close();


            new Alert(Alert.AlertType.INFORMATION, "Rental Report PDF exported successfully!").show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to export PDF").show();
        }
    }

    public void onExportReturnPDF(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Return Report as PDF");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = chooser.showSaveDialog(null);

        if (file == null) return;

        try (FileOutputStream fos = new FileOutputStream(file)) {

            Document pdf = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            PdfWriter.getInstance(pdf, fos);
            pdf.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font head = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

            pdf.add(new Paragraph("Return History Report\n\n", titleFont));

            PdfPTable t = new PdfPTable(5);
            t.setWidths(new int[]{10, 25, 15, 40, 10});
            t.setWidthPercentage(100);

            t.addCell(new PdfPCell(new Phrase("ID", head)));
            t.addCell(new PdfPCell(new Phrase("Customer", head)));
            t.addCell(new PdfPCell(new Phrase("Date", head)));
            t.addCell(new PdfPCell(new Phrase("Books", head)));
            t.addCell(new PdfPCell(new Phrase("Fine", head)));

            for (ReportReturn r : tableReturns.getItems()) {
                t.addCell(new PdfPCell(new Phrase(String.valueOf(r.getReturnId()), normal)));
                t.addCell(new PdfPCell(new Phrase(r.getCustomer(), normal)));
                t.addCell(new PdfPCell(new Phrase(r.getDate(), normal)));
                t.addCell(new PdfPCell(new Phrase(r.getBooks(), normal)));
                t.addCell(new PdfPCell(new Phrase(String.valueOf(r.getTotalFine()), normal)));
            }

            pdf.add(t);
            pdf.close();

            new Alert(Alert.AlertType.INFORMATION, "Return Report PDF exported successfully!").show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to export PDF").show();
        }
    }
}

