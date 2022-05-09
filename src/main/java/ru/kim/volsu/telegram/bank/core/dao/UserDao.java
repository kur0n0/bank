package ru.kim.volsu.telegram.bank.core.dao;

import ru.kim.volsu.telegram.bank.core.model.User;

public interface UserDao {
    User getByChatId(String chatId);

    void saveUser(User user);
}
