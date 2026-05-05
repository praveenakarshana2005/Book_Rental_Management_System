package service;

import dao.UserDao;
import impl.UserDaoImpl;
import model.User;
import util.HashUtil;

import java.util.Optional;

public class AuthService {
    private final UserDao userDao = new UserDaoImpl();

    public Optional<User> authenticate(String username, String password) {
        try {
            Optional<User> userOpt = Optional.ofNullable(userDao.findByUsername(username));
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (HashUtil.verify(password, user.getPasswordHash())) {
                    return Optional.of(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
