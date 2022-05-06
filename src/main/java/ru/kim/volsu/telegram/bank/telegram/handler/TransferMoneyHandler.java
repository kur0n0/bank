package ru.kim.volsu.telegram.bank.telegram.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.kim.volsu.telegram.bank.telegram.cache.Cache;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransferMoneyHandler implements MessageHandler{

    @Autowired
    private Cache cache;
    @Override
    public SendMessage handle(Message message) {
        // TODO здесь будем смотреть есть ли юзер с таким номером телефона, если нет, то запускаем процесс создания счета
        Long userId = message.getFrom().getId();
        BotStateEnum state = cache.getBotStateByUserId(userId);
        switch (state) {
            case TRANSFER_MONEY_MENU:
                SendMessage sendMessage = new SendMessage();
                sendMessage.enableMarkdown(true);
                sendMessage.setText("Введите свой номер телефона");
                sendMessage.setChatId(message.getChatId().toString());
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
