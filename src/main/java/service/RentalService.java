package service;

import dao.RentalDao;
import impl.RentalDaoImpl;
import impl.BookDaoImpl;
import model.Rental;
import model.RentalItem;


import java.time.LocalDate;

public class RentalService {
    private final RentalDao rentalDao = new RentalDaoImpl();
    private final BookDaoImpl bookDao = new BookDaoImpl();

    public Rental createRental(Rental rental) throws Exception {
        return rentalDao.create(rental);
    }

    public double processReturn(String rentalNo, LocalDate actualReturnDate) throws Exception {
        var opt = rentalDao.findByRentalNo(rentalNo);
        if (opt.isEmpty()) throw new Exception("Rental not found");
        Rental rental = opt.get();
        double totalFine = 0.0;

        for (RentalItem ri : rental.getItems()) {
            int qtyOutstanding = ri.getQuantity() - ri.getReturnedQuantity();
            if (qtyOutstanding <= 0) continue;
            long overdueDays = Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(rental.getDueDate(), actualReturnDate));
            double fine = overdueDays * ri.getPerDayFine() * qtyOutstanding;
            totalFine += fine;
            try (var conn = util.DBConnection.getConnection()) {
                conn.setAutoCommit(false);
                try (var pst = conn.prepareStatement("UPDATE rental_items SET returned_quantity = returned_quantity + ? WHERE id = ?")) {
                    pst.setInt(1, qtyOutstanding);
                    pst.setInt(2, ri.getId());
                    pst.executeUpdate();
                }
                bookDao.adjustAvailableQuantity(ri.getBook().getId(), qtyOutstanding);
                conn.commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
        rental.setReturnDate(actualReturnDate);
        rental.setTotalFine(rental.getTotalFine() + totalFine);
        boolean allReturned = rental.getItems().stream().allMatch(i -> i.getQuantity() <= (i.getReturnedQuantity() + (i.getQuantity() - i.getReturnedQuantity())));
        rental.setStatus(allReturned ? "returned" : "partially_returned");
        rentalDao.update(rental);
        return totalFine;
    }

    public RentalDao getRentalDao() {
        return rentalDao;
    }
}
