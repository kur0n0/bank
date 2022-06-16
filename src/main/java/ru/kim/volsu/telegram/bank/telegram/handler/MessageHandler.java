package ru.kim.volsu.telegram.bank.telegram.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;

public interface MessageHandler {
    SendMessage handle(Update update);

    BotStateEnum getBotState();
}
