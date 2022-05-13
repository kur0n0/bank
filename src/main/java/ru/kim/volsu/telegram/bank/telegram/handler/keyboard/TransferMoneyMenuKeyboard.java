package ru.kim.volsu.telegram.bank.telegram.handler.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransferMoneyMenuKeyboard {
    public ReplyKeyboardMarkup getKeyBoard() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Главное меню"));
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Меню перевода денег"));
        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("История переводов"));
        KeyboardRow row4 = new KeyboardRow();
        row4.add(new KeyboardButton("Перевод денег"));
        KeyboardRow row5 = new KeyboardRow();
        row5.add(new KeyboardButton("Узнать баланс"));
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;
    }
}
