package ru.kim.volsu.telegram.bank.telegram.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;

public interface MessageHandler {
    SendMessage handle(Message message);

    BotStateEnum getBotState();
}
