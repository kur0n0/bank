package ru.kim.volsu.telegram.bank.telegram.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kim.volsu.telegram.bank.core.model.Valute;
import ru.kim.volsu.telegram.bank.telegram.cache.Cache;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;
import ru.kim.volsu.telegram.bank.telegram.handler.keyboard.ValuteKeyboard;
import ru.kim.volsu.telegram.bank.utils.StringHelper;

import java.util.Map;

@Component
public class ValuteHandler implements MessageHandler {

    @Autowired
    private ValuteKeyboard valuteKeyboard;

    @Autowired
    private Cache cache;

    @Override
    public SendMessage handle(Update update) {
        Message message = update.getMessage();
        if (update.hasCallbackQuery()) {
            message = update.getCallbackQuery().getMessage();
        }
        String chatId = message.getChatId().toString();

        if (!update.hasCallbackQuery()) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("Выберете валюту из списка: ")
                    .replyMarkup(valuteKeyboard.getKeyBoard())
                    .build();
        } else if (!StringHelper.isNullOrEmpty(update.getCallbackQuery().getData())) {
            String data = update.getCallbackQuery().getData();
            Map<String, Valute> valuteMap = cache.getValuteMap();
            Valute valute = valuteMap.get(data);
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(String.format("Текущий курс валюты \"%s\" %s - %s рублей", data, valute.getName(), valute.getValue().toPlainString()))
                    .build();
        }

        return SendMessage.builder().build();
    }

    @Override
    public BotStateEnum getBotState() {
        return BotStateEnum.VALUTE_MENU;
    }
}
