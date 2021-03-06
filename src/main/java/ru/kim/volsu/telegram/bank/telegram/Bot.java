package ru.kim.volsu.telegram.bank.telegram;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;
import ru.kim.volsu.telegram.bank.telegram.cache.Cache;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;
import ru.kim.volsu.telegram.bank.telegram.handler.BotStateProcessor;
import ru.kim.volsu.telegram.bank.utils.StringHelper;

public class Bot extends SpringWebhookBot {

    private static final Logger log = LogManager.getLogger(Bot.class);
    private final String webHookPath;
    private final String botName;
    private final String botToken;

    @Autowired
    private Cache cache;

    @Autowired
    private BotStateProcessor botStateProcessor;

    public Bot(SetWebhook setWebhook, String webHookPath, String botName, String botToken) {
        super(setWebhook);
        this.webHookPath = webHookPath;
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (update.hasCallbackQuery()) {
            message = update.getCallbackQuery().getMessage();
        }

        if (StringHelper.isNullOrEmpty(message.getChat().getUserName())) {
            log.error("Отсутствует имя польхователя у userId: {}", message.getFrom().getId());
            return SendMessage.builder()
                    .chatId(message.getChatId().toString())
                    .text("Для работы бота требуется имя пользователя." +
                            "\nПожалуйста перейдите в найстроки и задайте его.")
                    .build();
        }

        log.debug("Получено сообщение от {}, текст: {}", message.getChat().getUserName(), message.getText());
        Long userId = message.getFrom().getId();
        BotStateEnum botStateEnum;
        switch (message.getText()) {
            case "Главное меню":
                botStateEnum = BotStateEnum.MAIN_MENU;
                break;
            case "Меню перевода денег":
                botStateEnum = BotStateEnum.TRANSFER_MONEY_MENU;
                break;
            case "Узнать баланс":
                botStateEnum = BotStateEnum.MAIN_MENU_BALANCE;
                break;
            case "Получить историю переводов":
                botStateEnum = BotStateEnum.MAIN_MENU_TRANSACTIONS_HISTORY;
                break;
            case "Курсы валют":
                botStateEnum = BotStateEnum.VALUTE_MENU;
                break;
            default:
                botStateEnum = cache.getBotStateByUserId(userId);
                break;
        }

        if (update.hasCallbackQuery()) {
            botStateEnum = BotStateEnum.VALUTE_MENU;
        }

        cache.setBotStateForUser(userId, botStateEnum);
        return botStateProcessor.processInputMessage(botStateEnum, update);
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
