package ru.kim.volsu.telegram.bank.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kim.volsu.telegram.bank.core.model.Card;
import ru.kim.volsu.telegram.bank.core.model.User;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserService userService;
    private final CardService cardService;

    @Autowired
    public RegistrationServiceImpl(UserService userService, CardService cardService) {
        this.userService = userService;
        this.cardService = cardService;
    }

    @Override
    public void registrateUser(Message message) {
        org.telegram.telegrambots.meta.api.objects.User from = message.getFrom();
        User user = new User();
        user.setUserName(from.getUserName());
        user.setFirstName(from.getFirstName());
        user.setLastName(from.getLastName());
        user.setChatId(message.getChatId().toString());

        userService.saveUser(user);
    }

    @Override
    public void registrateCard(User coreUser, Card card) {
        cardService.saveCard(card);

        coreUser.setCard(card);
        userService.update(coreUser);
    }
}
