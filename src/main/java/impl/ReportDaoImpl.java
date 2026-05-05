package impl;

import dao.ReportDao;
import model.ReportRental;
import model.ReportReturn;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportDaoImpl implements ReportDao {

    // =======================
    //   GET ALL RENTAL HISTORY
    // =======================
    @Override
    public List<ReportRental> getAllRentalHistory() {

        List<ReportRental> list = new ArrayList<>();

        String sql = """
            SELECT r.id AS rentalId,
                   c.name AS customer,
                   r.issue_date AS date,
                   GROUP_CONCAT(b.title SEPARATOR ', ') AS books,
                   SUM(ri.qty) AS totalQty
            FROM rental r
            JOIN customer c ON r.customer_id = c.id
            JOIN rental_item ri ON ri.rental_id = r.id
            JOIN book b ON b.id = ri.book_id
            GROUP BY r.id
            ORDER BY r.id DESC;
        """;



        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                ReportRental r = new ReportRental();
                r.setRentalId(rs.getInt("rentalId"));
                r.setCustomer(rs.getString("customer"));
                r.setDate(rs.getString("date"));
                r.setBooks(rs.getString("books"));
                r.setTotalQty(rs.getInt("totalQty"));
                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =======================
    //   GET ALL RETURN HISTORY
    // =======================
    @Override
    public List<ReportReturn> getAllReturnHistory() {

        List<ReportReturn> list = new ArrayList<>();

        String sql = """
            SELECT rr.id AS returnId,
                   c.name AS customer,
                   rr.return_date AS date,
                   GROUP_CONCAT(b.title SEPARATOR ', ') AS books,
                   SUM(ri.fine) AS fine
            FROM return_record rr
            JOIN rental r ON rr.rental_id = r.id
            JOIN customer c ON r.customer_id = c.id
            JOIN return_item ri ON ri.return_id = rr.id
            JOIN book b ON b.id = ri.book_id
            GROUP BY rr.id
            ORDER BY rr.id DESC;
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                ReportReturn r = new ReportReturn();
                r.setReturnId(rs.getInt("returnId"));
                r.setCustomer(rs.getString("customer"));
                r.setDate(rs.getString("date"));
                r.setBooks(rs.getString("books"));
                r.setFine(rs.getDouble("fine"));
                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =======================
    //   FILTER RENTAL HISTORY
    // =======================
    @Override
    public List<ReportRental> filterRentalHistory(LocalDate from, LocalDate to, String customer) {

        List<ReportRental> list = new ArrayList<>();

        String sql = """
            SELECT r.id AS rentalId,
                   c.name AS customer,
                   r.issue_date AS date,
                   GROUP_CONCAT(b.title SEPARATOR ', ') AS books,
                   SUM(ri.qty) AS totalQty
            FROM rental r
            JOIN customer c ON r.customer_id = c.id
            JOIN rental_item ri ON ri.rental_id = r.id
            JOIN book b ON b.id = ri.book_id
            WHERE (:from IS NULL OR r.issue_date >= :from)
              AND (:to IS NULL OR r.issue_date <= :to)
              AND (:customer IS NULL OR c.name LIKE :customer)
            GROUP BY r.id
            ORDER BY r.id DESC;
        """;

        sql = sql.replace(":from", from == null ? "NULL" : "'" + from + "'");
        sql = sql.replace(":to", to == null ? "NULL" : "'" + to + "'");
        sql = sql.replace(":customer", customer == null ? "NULL" : "'%" + customer + "%'");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                ReportRental r = new ReportRental();
                r.setRentalId(rs.getInt("rentalId"));
                r.setCustomer(rs.getString("customer"));
                r.setDate(rs.getString("date"));
                r.setBooks(rs.getString("books"));
                r.setTotalQty(rs.getInt("totalQty"));
                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =======================
    //   FILTER RETURN HISTORY
    // =======================
    @Override
    public List<ReportReturn> filterReturnHistory(LocalDate from, LocalDate to, String customer) {

        List<ReportReturn> list = new ArrayList<>();

        String sql = """
            SELECT rr.id AS returnId,
                   c.name AS customer,
                   rr.return_date AS date,
                   GROUP_CONCAT(b.title SEPARATOR ', ') AS books,
                   SUM(ri.fine) AS fine
            FROM return_record rr
            JOIN rental r ON rr.rental_id = r.id
            JOIN customer c ON r.customer_id = c.id
            JOIN return_item ri ON ri.return_id = rr.id
            JOIN book b ON b.id = ri.book_id
            WHERE (:from IS NULL OR rr.return_date >= :from)
              AND (:to IS NULL OR rr.return_date <= :to)
              AND (:customer IS NULL OR c.name LIKE :customer)
            GROUP BY rr.id
            ORDER BY rr.id DESC;
        """;

        sql = sql.replace(":from", from == null ? "NULL" : "'" + from + "'");
        sql = sql.replace(":to", to == null ? "NULL" : "'" + to + "'");
        sql = sql.replace(":customer", customer == null ? "NULL" : "'%" + customer + "%'");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                ReportReturn r = new ReportReturn();
                r.setReturnId(rs.getInt("returnId"));
                r.setCustomer(rs.getString("customer"));
                r.setDate(rs.getString("date"));
                r.setBooks(rs.getString("books"));
                r.setFine(rs.getDouble("fine"));
                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =======================
    //   LOAD CUSTOMER NAMES
    // =======================
    @Override
    public List<String> loadCustomerNames() {

        List<String> list = new ArrayList<>();

        String sql = "SELECT name FROM customer ORDER BY name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
