package impl;

import dao.UserDao;
import model.Role;
import model.User;
import util.DBConnection;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.Optional;

public class UserDaoImpl implements UserDao {


    @Override
    public User findByUsername(String username) throws Exception {

        String sql = "SELECT u.*, r.id AS rid, r.name AS rname " +
                "FROM users u " +
                "JOIN roles r ON u.role_id = r.id " +
                "WHERE u.username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setFullName(rs.getString("full_name"));

                // CORRECT ROLE MAPPING
                Role role = new Role();
                role.setId(rs.getInt("rid"));
                role.setName(rs.getString("rname"));

                u.setRole(role);

                return u;
            }
        }

        return null;
    }


    @Override
    public Optional<User>findById(int id) throws Exception {
        String sql = "SELECT u.*, r.id as role_id, r.name as role_name FROM users u LEFT JOIN roles r ON u.role_id = r.id WHERE u.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setFullName(rs.getString("full_name"));
                    Role role = new Role();
                    role.setId(rs.getInt("role_id"));
                    role.setName(rs.getString("role_name"));
                    try {
                        Field roleField = User.class.getDeclaredField("role");
                        roleField.setAccessible(true);
                        roleField.set(u, role);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    return Optional.of(u);
                }
            }

        }
        return Optional.empty();
    }

    @Override
    public User save(User user) throws Exception {
        String sql = "INSERT INTO users (username, password_hash, full_name, role_id) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, user.getUsername());
            pst.setString(2, user.getPasswordHash());
            pst.setString(3, user.getFullName());
            pst.setInt(4, Integer.parseInt(user.getRole().getRoleName()));
            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
        return user;
    }

}
