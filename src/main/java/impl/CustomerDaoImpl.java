package impl;

import dao.CustomerDao;
import model.Customer;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDaoImpl implements CustomerDao {
    @Override
    public Customer save(Customer c) throws Exception {
        String sql = "INSERT INTO customers (name, email, phone, address, membership_no) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, c.getName());
            pst.setString(2, c.getEmail());
            pst.setString(3, c.getPhone());
            pst.setString(4, c.getAddress());
            pst.setString(5, c.getMembershipNo());
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getInt(1));
            }
        }
        return c;
    }

    @Override
    public boolean update(Customer c) throws Exception {
        String sql = "UPDATE customers SET name=?, email=?, phone=?, address=?, membership_no=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, c.getName());
            pst.setString(2, c.getEmail());
            pst.setString(3, c.getPhone());
            pst.setString(4, c.getAddress());
            pst.setString(5, c.getMembershipNo());
            pst.setInt(6, c.getId());
            return pst.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int customerId) throws Exception {
        String sql = "DELETE FROM customers WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, customerId);

            return pst.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Customer> findById(int id) throws Exception {
        String sql = "SELECT * " + "FROM customers WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setEmail(rs.getString("email"));
                    c.setPhone(rs.getString("phone"));
                    c.setAddress(rs.getString("address"));
                    c.setMembershipNo(rs.getString("membership_no"));
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Customer> findAll() throws Exception {
        String sql = "SELECT * " + "FROM customers ORDER BY name";
        List<Customer> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Customer c = new Customer();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setAddress(rs.getString("address"));
                c.setMembershipNo(rs.getString("membership_no"));
                list.add(c);
            }
        }
        return list;
    }

    @Override
    public List<Customer> search(String q) throws Exception {
        String sql = "SELECT * " + "FROM customers WHERE name LIKE ? OR membership_no LIKE ? ORDER BY name";
        List<Customer> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            String like = "%" + q + "%";
            pst.setString(1, like);
            pst.setString(2, like);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Customer c = new Customer();
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setEmail(rs.getString("email"));
                    c.setPhone(rs.getString("phone"));
                    c.setAddress(rs.getString("address"));
                    c.setMembershipNo(rs.getString("membership_no"));
                    list.add(c);
                }
            }
        }
        return list;
    }
}
