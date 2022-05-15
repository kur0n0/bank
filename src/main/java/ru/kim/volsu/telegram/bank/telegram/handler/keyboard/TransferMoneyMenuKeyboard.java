package ru.kim.volsu.telegram.bank.telegram.handler.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
public class TransferMoneyMenuKeyboard {
    public ReplyKeyboardMarkup getKeyBoard() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Главное меню"));
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Перевод денег"));
        List<KeyboardRow> keyboard = List.of(row1, row2);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;
    }
}
