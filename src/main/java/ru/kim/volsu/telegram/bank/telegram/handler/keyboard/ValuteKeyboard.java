package ru.kim.volsu.telegram.bank.telegram.handler.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.kim.volsu.telegram.bank.core.model.Valute;
import ru.kim.volsu.telegram.bank.core.service.ValuteService;
import ru.kim.volsu.telegram.bank.telegram.cache.Cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ValuteKeyboard {

    private final Cache cache;
    private final ValuteService valuteService;

    public ValuteKeyboard(Cache cache, ValuteService valuteService) {
        this.cache = cache;
        this.valuteService = valuteService;
    }

    public InlineKeyboardMarkup getKeyBoard() {
        Map<String, Valute> valuteMap = valuteService.parseValute();
        ;
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        valuteMap.entrySet().forEach(entry -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(entry.getKey());
            button.setCallbackData(entry.getKey());
            keyboard.add(List.of(button));
        });

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }
}
