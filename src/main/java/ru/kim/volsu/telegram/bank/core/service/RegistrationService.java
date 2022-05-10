package ru.kim.volsu.telegram.bank.core.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kim.volsu.telegram.bank.core.model.Card;
import ru.kim.volsu.telegram.bank.core.model.User;

public interface RegistrationService {
    void registrateUser(Message message);

    void registrateCard(User coreUser, Card card);
}
