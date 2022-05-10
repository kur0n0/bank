package ru.kim.volsu.telegram.bank.telegram.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultWebhook;
import ru.kim.volsu.telegram.bank.telegram.Bot;

@Configuration
public class BotConfiguration {

    @Value ("${bot.token}")
    private String botToken;

    @Value ("${bot.username}")
    private String botUsername;

    @Value ("${webhook.path}")
    private String webHookPath;

    @Value ("${internal.url}")
    private String internalUrl;

    @Bean
    public SetWebhook buildWebHook() {
        return SetWebhook.builder().url(webHookPath).build();
    }

    @Bean
    public Bot setWebHook(SetWebhook setWebhookInstance) throws TelegramApiException {
        Bot bot = new Bot(setWebhookInstance, webHookPath, botUsername, botToken);

        DefaultWebhook defaultWebhook = new DefaultWebhook();

        defaultWebhook.setInternalUrl(internalUrl);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class, defaultWebhook);
        telegramBotsApi.registerBot(bot, setWebhookInstance);
        return bot;
    }
}
