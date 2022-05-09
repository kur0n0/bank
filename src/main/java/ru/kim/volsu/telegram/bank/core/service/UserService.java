package ru.kim.volsu.telegram.bank.core.service;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface UserService {
    void createNewUser(Message message);

    boolean isExist(String chatId);
}
