package ru.kim.volsu.telegram.bank.core.service;

import ru.kim.volsu.telegram.bank.core.model.User;

public interface UserService {
    void saveUser(User user);

    User getByChatId(String chatId);

    void update(User user);
}
