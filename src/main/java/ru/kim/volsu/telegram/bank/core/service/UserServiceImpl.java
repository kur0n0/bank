package ru.kim.volsu.telegram.bank.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kim.volsu.telegram.bank.core.dao.UserDao;
import ru.kim.volsu.telegram.bank.core.model.TransactionHistory;
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

    @Override
    public User getByCardId(Integer to) {
        return userDao.getByCardId(to);
    }

    @Override
    public String buildTextMessageForTransaction(TransactionHistory transaction, Integer currentCardId) {
        User fromUser = getByCardId(transaction.getFrom().getCardId());
        String amount = transaction.getAmount().toPlainString();
        String textMessage = String.format("Входящий перевод от пользоваетля %s +%s рублей",
                fromUser.getUserName(), amount);

        if (currentCardId.equals(transaction.getFrom().getCardId())) {
            User toUser = getByCardId(transaction.getTo().getCardId());
            textMessage = String.format("Исходящий перевод пользователю %s %s рублей",
                    toUser.getUserName(), amount);
        }

        return textMessage;
    }

}
