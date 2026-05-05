package dao;

import model.User;
import java.util.Optional;

public interface UserDao {
    User findByUsername(String username) throws Exception;
    Optional<User> findById(int id) throws Exception;
    User save(User user) throws Exception;
}
