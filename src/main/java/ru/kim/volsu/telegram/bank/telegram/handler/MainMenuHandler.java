package ru.kim.volsu.telegram.bank.telegram.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;

import java.util.ArrayList;
import java.util.List;

@Component
public class MainMenuHandler implements MessageHandler {
    @Override
    public SendMessage handle(Message message) {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Главное меню"));
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Перевод денег"));
        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("Данные банковского счета"));
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setText("Выберете действие на клавиатуре");
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    @Override
    public BotStateEnum getBotState() {
        return BotStateEnum.MAIN_MENU;
    }
}
