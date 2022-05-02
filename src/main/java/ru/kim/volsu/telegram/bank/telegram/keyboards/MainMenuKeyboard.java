package ru.kim.volsu.telegram.bank.telegram.keyboards;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainMenuKeyboard {
    public static SendMessage getMainKeyboard(String chatId) {
        List<KeyboardRow> keyboard = Arrays.stream(BotStateEnum.values())
                .map(state -> new KeyboardRow(List.of(new KeyboardButton(state.getText()))))
                .collect(Collectors.toList());

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText("Клавиатура: ");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }
}
