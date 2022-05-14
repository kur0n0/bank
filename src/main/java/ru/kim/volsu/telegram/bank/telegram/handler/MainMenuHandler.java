package ru.kim.volsu.telegram.bank.telegram.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kim.volsu.telegram.bank.core.model.Card;
import ru.kim.volsu.telegram.bank.core.model.User;
import ru.kim.volsu.telegram.bank.core.service.UserService;
import ru.kim.volsu.telegram.bank.telegram.cache.Cache;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;
import ru.kim.volsu.telegram.bank.telegram.handler.keyboard.MainMenuKeyboard;

import java.math.BigDecimal;
import java.util.Objects;

@Component
public class MainMenuHandler implements MessageHandler {

    private static final Logger log = LogManager.getLogger(TransferMoneyHandler.class);

    @Autowired
    private Cache cache;

    @Autowired
    private MainMenuKeyboard mainMenuKeyboard;

    @Autowired
    private UserService userService;

    @Override
    public SendMessage handle(Message message) {
        Long userId = message.getFrom().getId();
        String userName = message.getFrom().getUserName();
        String chatId = message.getChat().getId().toString();
        BotStateEnum state = cache.getBotStateByUserId(userId);

        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(mainMenuKeyboard.getKeyBoard())
                .parseMode("Markdown");

        switch (state) {
            case MAIN_MENU_BALANCE: {
                User user = userService.getByUsername(userName);
                if (Objects.isNull(user) || Objects.isNull(user.getCard())) {
                    log.error("Нельзя получить баланс отсутсвует пользователь/карта");

                    cache.setBotStateForUser(userId, BotStateEnum.MAIN_MENU);
                    return messageBuilder.text("Чтобы получить баланс нужно пройти регистрацию, пожалуйста перейдите в раздел \"Перевод денег\"")
                            .build();
                }

                Card card = user.getCard();
                BigDecimal actualBalance = card.getActualBalance();
                String cardNumber = card.getCardNumber();

                cache.setBotStateForUser(userId, BotStateEnum.MAIN_MENU);
                return messageBuilder.text(String.format("Ваш баланс по карте %s составляет %s рублей",
                        cardNumber, actualBalance.toPlainString()))
                        .build();
            }
        }

        cache.setBotStateForUser(userId, BotStateEnum.MAIN_MENU);
        return messageBuilder.text("Выберете действие на клавиатуре")
                .build();
    }

    @Override
    public BotStateEnum getBotState() {
        return BotStateEnum.MAIN_MENU;
    }
}
