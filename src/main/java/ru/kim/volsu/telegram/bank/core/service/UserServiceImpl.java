package ru.kim.volsu.telegram.bank.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kim.volsu.telegram.bank.core.dao.UserDao;
import ru.kim.volsu.telegram.bank.core.model.User;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public void createNewUser(Message message) {
        org.telegram.telegrambots.meta.api.objects.User from = message.getFrom();
        User user = new User();
        user.setUserName(from.getUserName());
        user.setFirstName(from.getFirstName());
        user.setLastName(from.getLastName());
        user.setChatId(message.getChatId().toString());

        userDao.saveUser(user);
    }

    @Override
    public User getByChatId(String chatId) {
        User user = userDao.getByChatId(chatId);
        return Objects.isNull(user) ? null : user;
    }
}
