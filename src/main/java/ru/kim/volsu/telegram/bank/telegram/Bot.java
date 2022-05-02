package ru.kim.volsu.telegram.bank.telegram;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;
import ru.kim.volsu.telegram.bank.telegram.keyboards.MainMenuKeyboard;
import ru.kim.volsu.telegram.bank.utils.StringHelper;

public class Bot extends SpringWebhookBot {
    private final String webHookPath;
    private final String botName;
    private final String botToken;

    public Bot(SetWebhook setWebhook, String webHookPath, String botName, String botToken) {
        super(setWebhook);
        this.webHookPath = webHookPath;
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        Message message = update.getMessage();
        if(!StringHelper.isNullOrEmpty(message.getText())) {
            return MainMenuKeyboard.getMainKeyboard(message.getChatId().toString());
        } else {
            return null;
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


    @Override
    public String getBotPath() {
        return webHookPath;
    }
}
