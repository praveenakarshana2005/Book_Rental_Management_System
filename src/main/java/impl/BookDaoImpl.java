package impl;

import dao.BookDao;
import model.Book;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDaoImpl implements BookDao {

    @Override
    public Book save(Book book) throws Exception {
        String sql = "INSERT INTO books (isbn, title, author, category, total_quantity, available_quantity, price) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, book.getIsbn());
            pst.setString(2, book.getTitle());
            pst.setString(3, book.getAuthor());
            pst.setString(4, book.getCategory());
            pst.setInt(5, book.getTotalQuantity());
            pst.setInt(6, book.getAvailableQuantity());
            pst.setDouble(7, book.getPrice());
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    book.setId(rs.getInt(1));
                }
            }
        }
        return book;
    }

    @Override
    public boolean update(Book book) throws Exception {
        String sql = "UPDATE books SET isbn=?, title=?, author=?, category=?, total_quantity=?, available_quantity=?, price=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, book.getIsbn());
            pst.setString(2, book.getTitle());
            pst.setString(3, book.getAuthor());
            pst.setString(4, book.getCategory());
            pst.setInt(5, book.getTotalQuantity());
            pst.setInt(6, book.getAvailableQuantity());
            pst.setDouble(7, book.getPrice());
            pst.setInt(8, book.getId());
            return pst.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM books WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, id);

            return pst.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Book> findById(int id) throws Exception {
        String sql = "SELECT * " + "FROM books WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() throws Exception {
        String sql = "SELECT *" + " FROM books ORDER BY title";
        List<Book> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public List<Book> search(String q) throws Exception {
        String sql = "SELECT * " + "FROM books WHERE title LIKE ? OR author LIKE ? OR category LIKE ? ORDER BY title";
        List<Book> list = new ArrayList<>();
        String like = "%" + q + "%";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, like);
            pst.setString(2, like);
            pst.setString(3, like);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public boolean adjustAvailableQuantity(int bookId, int delta) throws Exception {
        String sql = "UPDATE books SET available_quantity = available_quantity + ? WHERE id = ? AND (available_quantity + ?) >= 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, delta);
            pst.setInt(2, bookId);
            pst.setInt(3, delta);
            return pst.executeUpdate() > 0;
        }
    }

    private Book map(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setId(rs.getInt("id"));
        b.setIsbn(rs.getString("isbn"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setCategory(rs.getString("category"));
        b.setTotalQuantity(rs.getInt("total_quantity"));
        b.setAvailableQuantity(rs.getInt("available_quantity"));
        b.setPrice(rs.getDouble("price"));
        return b;
    }
}
