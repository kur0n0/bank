package ru.kim.volsu.telegram.bank.telegram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kim.volsu.telegram.bank.core.model.TransactionHistory;
import ru.kim.volsu.telegram.bank.core.service.UserService;
import ru.kim.volsu.telegram.bank.telegram.Bot;

import java.util.List;

@Component
public class SendMessageServiceImpl implements SendMessageService {
    @Autowired
    @Lazy
    private Bot bot;

    @Autowired
    private UserService userService;

    @Override
    public void sendMessage(String chatId, String text) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .build();

        bot.execute(sendMessage);
    }

    @Override
    public void sendTransactionHistory(String chatId, List<TransactionHistory> transactionList, Integer currentCardId) throws TelegramApiException {
        for (TransactionHistory transaction : transactionList) {
            String textMessage = userService.buildTextMessageForTransaction(transaction, currentCardId);
            sendMessage(chatId, textMessage);
        }
    }
}
