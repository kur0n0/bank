package ru.kim.volsu.telegram.bank.telegram.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kim.volsu.telegram.bank.core.service.UserService;
import ru.kim.volsu.telegram.bank.telegram.cache.Cache;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;


@Component
public class TransferMoneyHandler implements MessageHandler{

    @Autowired
    private Cache cache;

    @Autowired
    private UserService userService;

    private static Logger log = LogManager.getLogger(TransferMoneyHandler.class);

    @Override
    public SendMessage handle(Message message) {
        Long userId = message.getFrom().getId();
        BotStateEnum state = cache.getBotStateByUserId(userId);
        switch (state) {
            case TRANSFER_MONEY_MENU:
                SendMessage sendMessage = new SendMessage();
                sendMessage.enableMarkdown(true);
                sendMessage.setText("Введите свой номер телефона");
                sendMessage.setChatId(message.getChatId().toString());

                if(!userService.isExist(message.getChatId().toString())) {
                    userService.createNewUser(message);
                    log.info("Регистрация пользователя с chatId: {} прошла успешно", message.getChatId());
                }

                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_ASK_PHONE_NUMBER);
                return sendMessage;
            default:
                return SendMessage.builder()
                        .text("Что-то пошло не так")
                        .chatId(message.getChatId().toString())
                        .build();
        }
    }

    @Override
    public BotStateEnum getBotState() {
        return BotStateEnum.TRANSFER_MONEY_MENU;
    }
}
