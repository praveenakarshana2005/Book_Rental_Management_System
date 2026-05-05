package util;

import dao.UserDao;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class HashUtil {
    public static boolean verify(String password, String passwordHash) {
        return BCrypt.checkpw(password, passwordHash);

    }

    public Optional<User> authenticate(String username, String password) throws Exception {

        User user = new UserDao() {
            @Override
            public User findByUsername(String username) throws Exception {
                return null;
            }

            @Override
            public Optional<User> findById(int id) throws Exception {
                return Optional.empty();
            }

            @Override
            public User save(User user) throws Exception {
                return null;
            }
        }.findByUsername(username);
        if (user != null && verify(password, user.getPasswordHash())) {
            return Optional.of(user);
        }


        return Optional.empty();
    }
}