package ru.kim.volsu.telegram.bank.core.service;

import ru.kim.volsu.telegram.bank.core.model.TransactionHistory;
import ru.kim.volsu.telegram.bank.core.model.User;

public interface UserService {
    void saveUser(User user);

    User getByChatId(String chatId);

    User getByUsername(String username);

    void update(User user);

    User getByCardId(Integer to);

    String buildTextMessageForTransaction(TransactionHistory transaction, Integer currentCardId);
}
