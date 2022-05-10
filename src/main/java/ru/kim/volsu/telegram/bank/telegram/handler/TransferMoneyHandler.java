package ru.kim.volsu.telegram.bank.telegram.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kim.volsu.telegram.bank.core.model.Card;
import ru.kim.volsu.telegram.bank.core.model.User;
import ru.kim.volsu.telegram.bank.core.service.RegistrationService;
import ru.kim.volsu.telegram.bank.core.service.UserService;
import ru.kim.volsu.telegram.bank.telegram.cache.Cache;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;

import java.time.LocalDate;
import java.util.Objects;


@Component
public class TransferMoneyHandler implements MessageHandler {

    @Autowired
    private Cache cache;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserService userService;

    private static Logger log = LogManager.getLogger(TransferMoneyHandler.class);

    @Override
    public SendMessage handle(Message message) {
        Long userId = message.getFrom().getId();
        String chatId = message.getChatId().toString();
        String userName = message.getFrom().getUserName();
        BotStateEnum state = cache.getBotStateByUserId(userId);
        switch (state) {
            case TRANSFER_MONEY_MENU: {
                User user = userService.getByChatId(chatId);
                if (Objects.isNull(user)) {
                    registrationService.registrateUser(message);
                    user = userService.getByChatId(chatId);
                    log.info("Регистрация пользователя с chatId: {} прошла успешно", message.getChatId());
                }

                BotStateEnum botState = BotStateEnum.TRANSFER_MONEY_ASK_USERNAME;
                if (Objects.isNull(user.getCard())) {
                    botState = BotStateEnum.TRANSFER_MONEY_REGISTRATE_CARD;
                }

                cache.setBotStateForUser(userId, botState);
                return SendMessage.builder()
                        .text("Введите любой символ")
                        .chatId(chatId)
                        .build();
            }
            case TRANSFER_MONEY_REGISTRATE_CARD: {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Для того чтобы переводить деньги нужно добавить карту.\n" +
                        "Введите номер карты, например 1234 5678 0000 1111");
                sendMessage.setChatId(chatId);
                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_ASK_CARD_NUMBER);
                return sendMessage;
            }
            case TRANSFER_MONEY_ASK_CARD_NUMBER: {
                String cardNumber = message.getText();
                String[] numbers = cardNumber.split(" ");
                if (numbers.length != 4) {
                    log.error("Введен неправильный формат карты: {}, username: {}", cardNumber, userName);
                    return SendMessage.builder()
                            .chatId(chatId)
                            .text("Введен неправильный формат номера карты, попробуйте заново")
                            .build();
                }

                for (String v : numbers) {
                    try {
                        Integer.parseInt(v);
                    } catch (NumberFormatException e) {
                        log.error("Введен неправильный формат карты: {}, username: {}", cardNumber, userName);
                        return SendMessage.builder()
                                .chatId(chatId)
                                .text("Введен неправильный формат номера карты, попробуйте заново")
                                .build();
                    }
                }

                Card card = new Card();
                card.setCardNumber(cardNumber);
                cache.setUserCard(userId, card);
                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_ASK_EXPIRE_DATE);

                return SendMessage.builder()
                        .text("Введите дату действия карты, например 01/23")
                        .chatId(chatId)
                        .build();
            }
            case TRANSFER_MONEY_ASK_EXPIRE_DATE: {
                String expiryDate = message.getText();
                String[] date = expiryDate.split("/");
                if (date.length != 2) {
                    log.error("Введен неправильный формат даты действия карты: {}, username: {}", expiryDate, userName);
                    return SendMessage.builder()
                            .chatId(chatId)
                            .text("Введен неправильный формат даты действия карты, попробуйте заново")
                            .build();
                }

                int month;
                int year;
                try {
                    month = Integer.parseInt(date[0]);
                    year = Integer.parseInt(date[1]);
                } catch (NumberFormatException e) {
                    log.error("Введен неправильный формат даты действия карты: {}, username: {}", expiryDate, userName);
                    return SendMessage.builder()
                            .chatId(chatId)
                            .text("Введен неправильный формат даты действия карты, попробуйте заново")
                            .build();
                }

                LocalDate now = LocalDate.now();
                int currentYear = now.getYear() % 100;

                if (month < 1 || month > 12 || currentYear > year) {
                    log.error("Введен неправильный формат даты действия карты: {}, username: {}", expiryDate, userName);
                    return SendMessage.builder()
                            .chatId(chatId)
                            .text("Введен неправильный формат даты действия карты, попробуйте заново")
                            .build();
                }

                Card card = cache.getCardByUserId(userId);
                card.setExpiredTime(expiryDate);
                cache.setUserCard(userId, card);

                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_ASK_CVV);
                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Введите cvv код карты, например 123")
                        .build();
            }
            case TRANSFER_MONEY_ASK_CVV: {
                String cvv = message.getText();

                try {
                    Integer.parseInt(cvv);
                } catch (NumberFormatException e) {
                    log.error("Введен неправильный формат cvv кода: {}, username: {}", cvv, userName);
                    return SendMessage.builder()
                            .chatId(chatId)
                            .text("Введен неправильный формат cvv кода, попробуйте заново")
                            .build();
                }

                Card card = cache.getCardByUserId(userId);
                card.setCvv(cvv);

                User user = userService.getByChatId(chatId);

                registrationService.registrateCard(user, card);

                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_ASK_USERNAME);
                cache.removeCardCache(userId);
                return null;
            }
            case TRANSFER_MONEY_ASK_USERNAME: {
                cache.setBotStateForUser(userId, BotStateEnum.MAIN_MENU);
                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Введите username")
                        .build();
            }
            default:
                return SendMessage.builder()
                        .text("Что-то пошло не так")
                        .chatId(chatId)
                        .build();
        }
    }

    @Override
    public BotStateEnum getBotState() {
        return BotStateEnum.TRANSFER_MONEY_MENU;
    }
}
