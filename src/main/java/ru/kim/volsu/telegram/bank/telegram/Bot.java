package ru.kim.volsu.telegram.bank.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;
import ru.kim.volsu.telegram.bank.telegram.handler.MessageHandler;
import ru.kim.volsu.telegram.bank.utils.StringHelper;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Bot extends SpringWebhookBot {

    private static Logger log = LogManager.getLogger(Bot.class);
    private final String webHookPath;
    private final String botName;
    private final String botToken;

    @Autowired
    private MessageHandler messageHandler;

    public Bot(SetWebhook setWebhook, String webHookPath, String botName, String botToken) {
        super(setWebhook);
        this.webHookPath = webHookPath;
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        Message message = update.getMessage();
        if(StringHelper.isNullOrEmpty(message.getText())) {
            return null;
        }

        log.info("Сообщение от {}, текст: {}", message.getChat().getUserName(), message.getText());
        BotStateEnum botStateEnum;
        switch (message.getText()) {
            case "Главное меню":
                botStateEnum = BotStateEnum.MAIN_MENU;
                break;
            case "Перевод денег":
                botStateEnum = BotStateEnum.TRANSFER_MONEY;
                break;
            case "Данные банковского счета":
                botStateEnum = BotStateEnum.ACCOUNT_DETAILS;
                break;
            default:
                return SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Попробуйте ввести \"Главное меню\"")
                        .build();
        }

        messageHandler.processState(botStateEnum);
        return null;
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
