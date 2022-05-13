package ru.kim.volsu.telegram.bank.telegram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kim.volsu.telegram.bank.telegram.Bot;

@Component
public class SendMessageServiceImpl implements SendMessageService {
    @Autowired
    @Lazy
    private Bot bot;

    @Override
    public void sendMessage(String chatId, String text) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .build();

        bot.execute(sendMessage);
    }
}
