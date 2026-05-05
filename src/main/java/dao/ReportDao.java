package dao;

import model.ReportRental;
import model.ReportReturn;

import java.time.LocalDate;
import java.util.List;

public interface ReportDao {

    List<ReportRental> getAllRentalHistory();
    List<ReportReturn> getAllReturnHistory();

    List<ReportRental> filterRentalHistory(LocalDate from, LocalDate to, String customer);
    List<ReportReturn> filterReturnHistory(LocalDate from, LocalDate to, String customer);

    List<String> loadCustomerNames();
}
