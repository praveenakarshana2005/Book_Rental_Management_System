package dao;

import model.Rental;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentalDao {
    Rental create(Rental rental) throws Exception;

    Optional<Rental> findById(int id) throws Exception;

    Optional<Rental> findByRentalNo(String rentalNo) throws Exception;

    List<Rental> findByDateRange(LocalDate from, LocalDate to) throws Exception;

    List<Rental> findOverdue() throws Exception;

    boolean update(Rental rental) throws Exception;
}
