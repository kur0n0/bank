package ru.kim.volsu.telegram.bank.core.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kim.volsu.telegram.bank.core.model.User;

public interface UserService {
    void createNewUser(Message message);

    User getByChatId(String chatId);
}
