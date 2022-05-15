package ru.kim.volsu.telegram.bank.telegram.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kim.volsu.telegram.bank.core.model.Card;
import ru.kim.volsu.telegram.bank.core.model.TransactionHistory;
import ru.kim.volsu.telegram.bank.core.model.User;
import ru.kim.volsu.telegram.bank.core.service.RegistrationService;
import ru.kim.volsu.telegram.bank.core.service.TransactionService;
import ru.kim.volsu.telegram.bank.core.service.UserService;
import ru.kim.volsu.telegram.bank.telegram.cache.Cache;
import ru.kim.volsu.telegram.bank.telegram.dto.TransferMoneyDto;
import ru.kim.volsu.telegram.bank.telegram.dto.TransferMoneyRequestDto;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;
import ru.kim.volsu.telegram.bank.telegram.handler.keyboard.TransferMoneyMenuKeyboard;
import ru.kim.volsu.telegram.bank.telegram.service.SendMessageService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
        if (text.equals("Перевод денег")) {
            state = BotStateEnum.TRANSFER_MONEY_ASK_USERNAME;
        } else if (text.equals("Меню перевода денег")) {
            state = BotStateEnum.TRANSFER_MONEY_MENU;
        }

        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(keyboard.getKeyBoard())
                .parseMode("Markdown");

        switch (state) {
            case TRANSFER_MONEY_MENU: {
                cache.removeFromTo(userId);
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
                log.info("Успешно зарегистрирована карта для пользователя {}", user.getUserName());

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

                User currentUser = userService.getByChatId(chatId);
                if (currentUser.getUserName().equals(username)) {
                    log.error("Себе переводить деньги нельзя");
                    return messageBuilder.text("Себе переводить деньги нельзя, введите другое имя пользователся")
                            .build();
                }

                User toUser = userService.getByUsername(username);
                User fromUser = userService.getByChatId(chatId);
                if (Objects.isNull(toUser)) {
                    log.error("Отсутсвует пользователь с username: {}", username);
                    return messageBuilder.text("Отсутсвует пользователь с username: " + username + "\nПопробуйте заново")
                            .build();
                }

                cache.setFromTo(userId, new TransferMoneyRequestDto(fromUser.getUserName(), toUser.getUserName()));
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
                TransferMoneyRequestDto request = cache.getFromTo(userId);
                String toUserName = request.getToUsername();
                String fromUserName = request.getFromUserName();

                User fromUser = userService.getByUsername(fromUserName);
                BigDecimal actualBalance = fromUser.getCard().getActualBalance();
                if (actualBalance.compareTo(amount) < 0) {
                    log.error("Не хватает средств для перевода");
                    return messageBuilder.text("На вашем счете не хватает средств для перевода, попробуйте снова")
                            .build();
                }
                cache.removeFromTo(userId);

                TransferMoneyDto transferMoneyDto = new TransferMoneyDto(fromUserName, toUserName, amount);
                cache.setTransferMoneyDto(userId, transferMoneyDto);
                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_CONFIRMATION);

                KeyboardRow row1 = new KeyboardRow();
                row1.add(new KeyboardButton("Да"));
                KeyboardRow row2 = new KeyboardRow();
                row2.add(new KeyboardButton("Нет"));
                List<KeyboardRow> keyboard = List.of(row1, row2);

                ReplyKeyboardMarkup confirmationKeyboard = new ReplyKeyboardMarkup();
                confirmationKeyboard.setKeyboard(keyboard);
                confirmationKeyboard.setSelective(true);
                confirmationKeyboard.setResizeKeyboard(true);
                confirmationKeyboard.setOneTimeKeyboard(false);

                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Выберите да/нет для подтверждения перевода")
                        .replyMarkup(confirmationKeyboard)
                        .parseMode("Markdown")
                        .build();
            }
            case TRANSFER_MONEY_CONFIRMATION: {
                String confirmationText = message.getText();
                if (!confirmationText.equals("Да")) {
                    cache.removeTransferMoneyDto(userId);
                    cache.setBotStateForUser(userId, BotStateEnum.MAIN_MENU);

                    return messageBuilder.build();
                }

                TransferMoneyDto transferMoneyDto = cache.getTransferMoneyDto(userId);
                transactionService.transferMoney(transferMoneyDto);

                cache.setBotStateForUser(userId, BotStateEnum.MAIN_MENU);
                cache.removeTransferMoneyDto(userId);

                log.info("Перевод пользователю {} прошел успешно", transferMoneyDto.getToUserName());

                User toUser = userService.getByUsername(transferMoneyDto.getToUserName());
                TransactionHistory transactionHistory = transactionService.getLastTransactionToCard(toUser.getCard().getCardId());

                String textMessage = userService.buildTextMessageForTransaction(transactionHistory, toUser.getCard().getCardId());
                try {
                    sendMessageService.sendMessage(toUser.getChatId(), textMessage);
                } catch (TelegramApiException e) {
                    log.error("Ошибка при отправлении уведомлении о транзакции пользователю: {} " +
                            "с chatId: {}, username: {}, сообщение: {}", e.getMessage(), toUser.getChatId(), toUser.getUserName(), textMessage);
                }

                try {
                    sendMessageService.sendMessage(chatId,
                            String.format("Перевод пользователю %s прошел успешно", transferMoneyDto.getToUserName()));
                } catch (TelegramApiException e) {
                    log.error("Ошибка при отправлении сообщения о успешности транзакции пользователю {}", transferMoneyDto.getToUserName());
                }

                User fromUser = userService.getByUsername(transferMoneyDto.getFromUserName());
                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_MENU);

                return messageBuilder.text(String.format("Ваш баланс по карте %s составляет %s",
                        fromUser.getCard().getCardNumber(), fromUser.getCard().getActualBalance().toPlainString()))
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
