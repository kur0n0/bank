package ru.kim.volsu.telegram.bank.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kim.volsu.telegram.bank.core.dao.UserDao;
import ru.kim.volsu.telegram.bank.core.model.User;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void saveUser(User user) {
        userDao.saveUser(user);
    }

    @Override
    public User getByChatId(String chatId) {
        User user = userDao.getByChatId(chatId);
        return Objects.isNull(user) ? null : user;
    }

    @Override
    public User getByUsername(String username) {
        User user = userDao.getByUsername(username);
        return Objects.isNull(user) ? null : user;
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

}
