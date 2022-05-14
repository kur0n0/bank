package ru.kim.volsu.telegram.bank.telegram.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kim.volsu.telegram.bank.core.model.Card;
import ru.kim.volsu.telegram.bank.core.model.TransactionHistory;
import ru.kim.volsu.telegram.bank.core.model.User;
import ru.kim.volsu.telegram.bank.core.service.RegistrationService;
import ru.kim.volsu.telegram.bank.core.service.TransactionService;
import ru.kim.volsu.telegram.bank.core.service.UserService;
import ru.kim.volsu.telegram.bank.telegram.cache.Cache;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;
import ru.kim.volsu.telegram.bank.telegram.handler.keyboard.TransferMoneyMenuKeyboard;
import ru.kim.volsu.telegram.bank.telegram.service.SendMessageService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Component
public class TransferMoneyHandler implements MessageHandler {

    private static final Logger log = LogManager.getLogger(TransferMoneyHandler.class);

    @Autowired
    private Cache cache;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransferMoneyMenuKeyboard keyboard;
    @Autowired
    private SendMessageService sendMessageService;

    @Override
    public SendMessage handle(Message message) {
        Long userId = message.getFrom().getId();
        String chatId = message.getChatId().toString();
        String userName = message.getFrom().getUserName();

        BotStateEnum state = cache.getBotStateByUserId(userId);
        String text = message.getText();
        if (text.equals("Узнать баланс")) {
            state = BotStateEnum.TRANSFER_MONEY_BALANCE;
        } else if (text.equals("Перевод денег")) {
            state = BotStateEnum.TRANSFER_MONEY_ASK_USERNAME;
        } else if (text.equals("История переводов")) {
            state = BotStateEnum.TRANSFER_MONEY_HISTORY;
        } else if (text.equals("Меню перевода денег")) {
            state = BotStateEnum.TRANSFER_MONEY_MENU;
        }

        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(keyboard.getKeyBoard())
                .parseMode("Markdown");

        switch (state) {
            case TRANSFER_MONEY_MENU: {
                cache.removeTransaction(userId);
                cache.removeCardCache(userId);

                User user = userService.getByChatId(chatId);
                BotStateEnum botState = BotStateEnum.TRANSFER_MONEY_ASK_USERNAME;
                if (Objects.isNull(user)) {
                    registrationService.registrateUser(message);
                    user = userService.getByChatId(chatId);
                    log.info("Регистрация пользователя с chatId: {} прошла успешно", message.getChatId());

                }

                if (Objects.isNull(user.getCard())) {
                    botState = BotStateEnum.TRANSFER_MONEY_REGISTRATE_CARD;
                }

                cache.setBotStateForUser(userId, botState);
                return messageBuilder.text("Выберите действие на клавиатуре")
                        .build();
            }
            case TRANSFER_MONEY_REGISTRATE_CARD: {
                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_ASK_CARD_NUMBER);
                return messageBuilder.text("Для того чтобы переводить деньги нужно добавить карту.\n" +
                        "Введите номер карты, например 1234 5678 0000 1111")
                        .build();
            }
            case TRANSFER_MONEY_ASK_CARD_NUMBER: {
                String cardNumber = message.getText();
                String[] numbers = cardNumber.split(" ");
                if (numbers.length != 4) {
                    log.error("Введен неправильный формат карты: {}, username: {}", cardNumber, userName);
                    return messageBuilder.text("Введен неправильный формат номера карты, попробуйте заново")
                            .build();
                }

                for (String v : numbers) {
                    try {
                        Integer.parseInt(v);
                    } catch (NumberFormatException e) {
                        log.error("Введен неправильный формат карты: {}, username: {}", cardNumber, userName);
                        return messageBuilder.text("Введен неправильный формат номера карты, попробуйте заново")
                                .build();
                    }
                }

                Card card = new Card();
                card.setCardNumber(cardNumber);
                cache.setUserCard(userId, card);
                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_ASK_EXPIRE_DATE);

                return messageBuilder.text("Введите дату действия карты, например 01/23")
                        .build();
            }
            case TRANSFER_MONEY_ASK_EXPIRE_DATE: {
                String expiryDate = message.getText();
                String[] date = expiryDate.split("/");
                if (date.length != 2) {
                    log.error("Введен неправильный формат даты действия карты: {}, username: {}", expiryDate, userName);
                    return messageBuilder.text("Введен неправильный формат даты действия карты, попробуйте заново")
                            .build();
                }

                int month;
                int year;
                try {
                    month = Integer.parseInt(date[0]);
                    year = Integer.parseInt(date[1]);
                } catch (NumberFormatException e) {
                    log.error("Введен неправильный формат даты действия карты: {}, username: {}", expiryDate, userName);
                    return messageBuilder.text("Введен неправильный формат даты действия карты, попробуйте заново")
                            .build();
                }

                LocalDate now = LocalDate.now();
                int currentYear = now.getYear() % 100;

                if (month < 1 || month > 12 || currentYear > year) {
                    log.error("Введен неправильный формат даты действия карты: {}, username: {}", expiryDate, userName);
                    return messageBuilder.text("Введен неправильный формат даты действия карты, попробуйте заново")
                            .build();
                }

                Card card = cache.getCardByUserId(userId);
                card.setExpiredTime(expiryDate);
                cache.setUserCard(userId, card);

                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_ASK_CVV);
                return messageBuilder.text("Введите cvv код карты, например 123")
                        .build();
            }
            case TRANSFER_MONEY_ASK_CVV: {
                String cvv = message.getText();

                try {
                    Integer.parseInt(cvv);
                } catch (NumberFormatException e) {
                    log.error("Введен неправильный формат cvv кода: {}, username: {}", cvv, userName);
                    return messageBuilder.text("Введен неправильный формат cvv кода, попробуйте заново")
                            .build();
                }

                Card card = cache.getCardByUserId(userId);
                card.setCvv(cvv);

                User user = userService.getByChatId(chatId);

                registrationService.registrateCard(user, card);

                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_ASK_USERNAME);
                cache.removeCardCache(userId);
                return messageBuilder.build();
            }
            case TRANSFER_MONEY_ASK_USERNAME: {
                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_TRANSACTION);
                return messageBuilder.text("Введите имя пользователя, которому нужно перевести деньги, например: kur0n0")
                        .build();
            }
            case TRANSFER_MONEY_TRANSACTION: {
                String username = message.getText();

                User toUser = userService.getByUsername(username);
                User fromUser = userService.getByChatId(chatId);
                if (Objects.isNull(toUser)) {
                    log.error("Отсутсвует пользователь с username: {}", username);
                    return messageBuilder.text("Отсутсвует пользователь с username: " + username + "\nПопробуйте заново")
                            .build();
                }

                cache.setTransaction(userId, fromUser.getUserName(), toUser.getUserName());
                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_ASK_AMOUNT);
                return messageBuilder.text("Введите сумму для перевода средств")
                        .build();
            }
            case TRANSFER_MONEY_ASK_AMOUNT: {
                String stringAmount = message.getText();

                BigDecimal amount;
                try {
                    amount = new BigDecimal(stringAmount);
                } catch (NumberFormatException e) {
                    log.error("Введена неправильная сумма: {}", stringAmount);
                    return messageBuilder.text("Введена неправильная сумма, попробуйте снова")
                            .build();
                }

                if (amount.compareTo(BigDecimal.ZERO) < 0) {
                    log.error("Введена отрицательная сумма перевода: {}", amount.toPlainString());
                    return messageBuilder.text("Введена отрицательная сумма перевода, попробуйте снова")
                            .build();
                }

                // пара ключ - от кого перевод, значени - кому перевод
                Map.Entry<String, String> entry = cache.getTransaction(userId);
                String toUserName = entry.getValue();

                BigDecimal actualBalance = userService.getByUsername(entry.getKey()).getCard().getActualBalance();
                if(actualBalance.compareTo(amount) < 0) {
                    log.error("Не хватает средств для перевода");
                    return messageBuilder.text("На вашем счете не хватает средств для перевода, попробуйте снова")
                            .build();
                }

                transactionService.transferMoney(entry.getKey(), toUserName, amount);
                cache.removeTransaction(userId);
                cache.setBotStateForUser(userId, BotStateEnum.MAIN_MENU);

                log.info("Перевод пользователю с username: {} прошел успешно", toUserName);

                User toUser = userService.getByUsername(toUserName);
                TransactionHistory transactionHistory = transactionService.getLastTransactionToCard(toUser.getCard().getCardId());

                String textMessage = userService.buildTextMessageForTransaction(transactionHistory, toUser.getCard().getCardId());
                try {
                    sendMessageService.sendMessage(toUser.getChatId(), textMessage);
                } catch (TelegramApiException e) {
                    log.error("Ошибка при отправлении уведомлении о транзакции пользователю: {} " +
                            "с chatId: {}, username: {}, сообщение: {}", e.getMessage(), toUser.getChatId(), toUser.getUserName(), textMessage);
                }

//                messageBuilder.text(String.format("Ваш баланс по карте %s составляет %s",
//                        cardNumber, actualBalance.toPlainString()))
//                        .build()

                return messageBuilder.text(String.format("Перевод пользователю с username: %s прошел успешно", toUserName))
                        .build();
            }
            case TRANSFER_MONEY_BALANCE: {
                Card card = userService.getByUsername(userName).getCard();
                BigDecimal actualBalance = card.getActualBalance();
                String cardNumber = card.getCardNumber();

                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_MENU);
                return messageBuilder.text(String.format("Ваш баланс по карте %s составляет %s",
                        cardNumber, actualBalance.toPlainString()))
                        .build();
            }
            case TRANSFER_MONEY_HISTORY: {
                User user = userService.getByUsername(userName);
                Integer currentCardId = user.getCard().getCardId();
                List<TransactionHistory> transactionList = transactionService.getTransactionsByCardId(currentCardId);

                int size = transactionList.size();
                TransactionHistory last = transactionList.get(size - 1);
                transactionList.remove(last);

                try {
                    sendMessageService.sendMessage(chatId, "Ваша история транзакций:");
                } catch (TelegramApiException e) {
                    log.error("Ошибка при отправлении сообщения: {} " +
                            "с chatId: {}, username: {}", e.getMessage(), user.getChatId(), user.getUserName());
                }

                transactionList.forEach(transaction -> {
                    String textMessage = userService.buildTextMessageForTransaction(transaction, currentCardId);

                    try {
                        sendMessageService.sendMessage(chatId, textMessage);
                    } catch (TelegramApiException e) {
                        log.error("Ошибка при отправлении сообщения по получении истории транзакций пользователю: {} " +
                                "с chatId: {}, username: {}, сообщение: {}", e.getMessage(), user.getChatId(), user.getUserName(), textMessage);
                    }
                });

                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_MENU);
                return messageBuilder.text(userService.buildTextMessageForTransaction(last, currentCardId))
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
