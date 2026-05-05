package impl;

import dao.RentalDao;
import model.*;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RentalDaoImpl implements RentalDao {

    private final BookDaoImpl bookDao = new BookDaoImpl();

    @Override
    public Rental create(Rental rental) throws Exception {
        String insertRental = "INSERT INTO rentals (rental_no, customer_id, user_id, issue_date, due_date, status) VALUES (?,?,?,?,?,?)";
        String insertItem = "INSERT INTO rental_items (rental_id, book_id, quantity, per_day_fine, returned_quantity) VALUES (?,?,?,?,?)";
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(insertRental, Statement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, rental.getRentalNo());
                pst.setInt(2, rental.getCustomer().getId());
                pst.setInt(3, rental.getIssuedBy().getId());
                pst.setDate(4, Date.valueOf(rental.getIssueDate()));
                pst.setDate(5, Date.valueOf(rental.getDueDate()));
                pst.setString(6, rental.getStatus());
                pst.executeUpdate();
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) rental.setId(rs.getInt(1));
                }
            }

            try (PreparedStatement pstItem = conn.prepareStatement(insertItem)) {
                for (RentalItem ri : rental.getItems()) {
                    boolean ok = bookDao.adjustAvailableQuantity(ri.getBook().getId(), -ri.getQuantity());
                    if (!ok) throw new SQLException("Insufficient stock for book id " + ri.getBook().getId());

                    pstItem.setInt(1, rental.getId());
                    pstItem.setInt(2, ri.getBook().getId());
                    pstItem.setInt(3, ri.getQuantity());
                    pstItem.setDouble(4, ri.getPerDayFine());
                    pstItem.setInt(5, 0);
                    pstItem.addBatch();
                }
                pstItem.executeBatch();
            }
            conn.commit();
            return rental;
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    @Override
    public Optional<Rental> findById(int id) throws Exception {
        String sql = "SELECT r.*, c.*, u.*, ro.* FROM rentals r " +
                "JOIN customers c ON r.customer_id = c.id " +
                "JOIN users u ON r.user_id = u.id " +
                "LEFT JOIN roles ro ON u.role_id = ro.id WHERE r.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Rental r = mapRental(rs);
                    r.setItems(getItems(conn, r.getId()));
                    return Optional.of(r);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Rental> findByRentalNo(String rentalNo) throws Exception {
        String sql = "SELECT r.*, c.*, u.*, ro.* FROM rentals r " +
                "JOIN customers c ON r.customer_id = c.id " +
                "JOIN users u ON r.user_id = u.id " +
                "LEFT JOIN roles ro ON u.role_id = ro.id WHERE r.rental_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, rentalNo);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Rental r = mapRental(rs);
                    r.setItems(getItems(conn, r.getId()));
                    return Optional.of(r);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Rental> findByDateRange(LocalDate from, LocalDate to) throws Exception {
        String sql = "SELECT r.*, c.* FROM rentals r JOIN customers c ON r.customer_id = c.id WHERE r.issue_date BETWEEN ? AND ? ORDER BY r.issue_date DESC";
        List<Rental> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setDate(1, Date.valueOf(from));
            pst.setDate(2, Date.valueOf(to));
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Rental r = new Rental();
                    r.setId(rs.getInt("id"));
                    r.setRentalNo(rs.getString("rental_no"));
                    r.setIssueDate(rs.getDate("issue_date").toLocalDate());
                    r.setDueDate(rs.getDate("due_date").toLocalDate());
                    Customer c = new Customer();
                    c.setId(rs.getInt("customer_id"));
                    c.setName(rs.getString("name"));
                    r.setCustomer(c);
                    list.add(r);
                }
            }
        }
        return list;
    }

    @Override
    public List<Rental> findOverdue() throws Exception {
        String sql = "SELECT r.*, c.*, DATEDIFF(CURDATE(), r.due_date) as overdue_days FROM rentals r JOIN customers c ON r.customer_id = c.id WHERE r.status = 'issued' AND CURDATE() > r.due_date";
        List<Rental> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Rental r = new Rental();
                r.setId(rs.getInt("id"));
                r.setRentalNo(rs.getString("rental_no"));
                r.setIssueDate(rs.getDate("issue_date").toLocalDate());
                r.setDueDate(rs.getDate("due_date").toLocalDate());
                Customer c = new Customer();
                c.setId(rs.getInt("customer_id"));
                c.setName(rs.getString("name"));
                r.setCustomer(c);
                list.add(r);
            }
        }
        return list;
    }

    @Override
    public boolean update(Rental rental) throws Exception {
        String sql = "UPDATE rentals SET status=?, return_date=?, total_fine=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, rental.getStatus());
            if (rental.getReturnDate() != null) pst.setDate(2, Date.valueOf(rental.getReturnDate()));
            else pst.setNull(2, Types.DATE);
            pst.setDouble(3, rental.getTotalFine());
            pst.setInt(4, rental.getId());
            return pst.executeUpdate() > 0;
        }
    }

    // helper: load items
    private List<RentalItem> getItems(Connection conn, int rentalId) throws SQLException {
        String sql = "SELECT ri.*, b.* FROM rental_items ri JOIN books b ON ri.book_id = b.id WHERE ri.rental_id = ?";
        List<RentalItem> items = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, rentalId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    RentalItem ri = new RentalItem();
                    ri.setId(rs.getInt("id"));
                    ri.setRentalId(rs.getInt("rental_id"));
                    Book b = new Book();
                    b.setId(rs.getInt("book_id"));
                    b.setTitle(rs.getString("title"));
                    b.setAuthor(rs.getString("author"));
                    b.setAvailableQuantity(rs.getInt("available_quantity"));
                    b.setTotalQuantity(rs.getInt("total_quantity"));
                    ri.setBook(b);
                    ri.setQuantity(rs.getInt("quantity"));
                    ri.setPerDayFine(rs.getDouble("per_day_fine"));
                    ri.setReturnedQuantity(rs.getInt("returned_quantity"));
                    items.add(ri);
                }
            }
        }
        return items;
    }

    private Rental mapRental(ResultSet rs) throws SQLException {
        Rental r = new Rental();
        r.setId(rs.getInt("id"));
        r.setRentalNo(rs.getString("rental_no"));
        r.setIssueDate(rs.getDate("issue_date").toLocalDate());
        r.setDueDate(rs.getDate("due_date").toLocalDate());
        r.setStatus(rs.getString("status"));
        Customer c = new Customer();
        c.setId(rs.getInt("customer_id"));
        c.setName(rs.getString("name"));
        r.setCustomer(c);
        User u = new User();
        u.setId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        r.setIssuedBy(u);
        return r;
    }
}
