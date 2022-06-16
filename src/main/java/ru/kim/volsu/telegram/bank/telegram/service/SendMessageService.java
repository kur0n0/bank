package ru.kim.volsu.telegram.bank.telegram.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kim.volsu.telegram.bank.core.model.TransactionHistory;

import java.util.List;

public interface SendMessageService {
    void sendMessage(String chatId, String text) throws TelegramApiException;

    void sendTransactionHistory(String chatId, List<TransactionHistory> transactionList, Integer currentCardId) throws TelegramApiException;

    void sendMessage(SendMessage message) throws TelegramApiException;
}
