package ru.kim.volsu.telegram.bank.telegram.service;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface SendMessageService {
    void sendMessage(String chatId, String text) throws TelegramApiException;
}
