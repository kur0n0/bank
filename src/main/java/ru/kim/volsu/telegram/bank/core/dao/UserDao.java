package ru.kim.volsu.telegram.bank.core.dao;

import ru.kim.volsu.telegram.bank.core.model.User;

public interface UserDao {
    User getByChatId(String chatId);

    void saveUser(User user);

    void update(User user);

    User getByUsername(String username);
}
