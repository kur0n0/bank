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
import ru.kim.volsu.telegram.bank.core.service.TransactionService;
import ru.kim.volsu.telegram.bank.core.service.UserService;
import ru.kim.volsu.telegram.bank.telegram.cache.Cache;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;
import ru.kim.volsu.telegram.bank.telegram.handler.keyboard.MainMenuKeyboard;
import ru.kim.volsu.telegram.bank.telegram.service.SendMessageService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class MainMenuHandler implements MessageHandler {

    private static final Logger log = LogManager.getLogger(TransferMoneyHandler.class);

    @Autowired
    private Cache cache;

    @Autowired
    private MainMenuKeyboard mainMenuKeyboard;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private SendMessageService sendMessageService;

    @Override
    public SendMessage handle(Message message) {
        Long userId = message.getFrom().getId();
        String userName = message.getFrom().getUserName();
        String chatId = message.getChat().getId().toString();

        BotStateEnum state = cache.getBotStateByUserId(userId);
        String text = message.getText();
        if (text.equals("Вся история")) {
            state = BotStateEnum.MAIN_MENU_TRANSACTIONS_HISTORY_ALL;
        } else if (text.equals("Последние 5 переводов")) {
            state = BotStateEnum.MAIN_MENU_TRANSACTIONS_HISTORY_LAST;
        }

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
            case MAIN_MENU_TRANSACTIONS_HISTORY: {
                User user = userService.getByUsername(userName);
                if (Objects.isNull(user) || Objects.isNull(user.getCard())) {
                    log.error("Нельзя получить историю переводов отсутсвует пользователь/карта");

                    cache.setBotStateForUser(userId, BotStateEnum.MAIN_MENU);
                    return messageBuilder.text("Чтобы получить историю переводов нужно пройти регистрацию, пожалуйста перейдите в раздел \"Перевод денег\"")
                            .build();
                }

                Integer currentCardId = user.getCard().getCardId();
                List<TransactionHistory> transactionList = transactionService.getTransactionsByCardId(currentCardId);

                int size = transactionList.size();
                if (size == 0) {
                    log.warn("Еще нет транзакций");
                    cache.setBotStateForUser(userId, BotStateEnum.MAIN_MENU);
                    return messageBuilder.text("У вас еще нет тразакций")
                            .build();
                }

                KeyboardRow row1 = new KeyboardRow();
                row1.add(new KeyboardButton("Вся история"));
                KeyboardRow row2 = new KeyboardRow();
                row2.add(new KeyboardButton("Последние 5 переводов"));
                List<KeyboardRow> keyboard = List.of(row1, row2);

                ReplyKeyboardMarkup transactionSize = new ReplyKeyboardMarkup();
                transactionSize.setKeyboard(keyboard);
                transactionSize.setSelective(true);
                transactionSize.setResizeKeyboard(true);
                transactionSize.setOneTimeKeyboard(false);

                cache.setTransactionList(userId, transactionList);
                return SendMessage.builder()
                        .text("Выберете действие на клавиатуре")
                        .chatId(chatId)
                        .parseMode("Markdown")
                        .replyMarkup(transactionSize)
                        .build();
            }
            case MAIN_MENU_TRANSACTIONS_HISTORY_ALL: {
                User user = userService.getByUsername(userName);
                List<TransactionHistory> transactionList = cache.getTransactionList(userId);
                TransactionHistory last = transactionList.get(transactionList.size() - 1);
                transactionList.remove(last);

                try {
                    sendMessageService.sendMessage(chatId, "Ваша история транзакций:");
                } catch (TelegramApiException e) {
                    log.error("Ошибка при отправлении сообщения: {} " +
                            "с chatId: {}, username: {}", e.getMessage(), user.getChatId(), user.getUserName());
                }

                try {
                    sendMessageService.sendTransactionHistory(chatId, transactionList, user.getCard().getCardId());
                } catch (TelegramApiException e) {
                    log.error("Ошибка при отправлении сообщения по получении истории транзакций пользователю: {} " +
                            "с chatId: {}, username: {}", e.getMessage(), user.getChatId(), user.getUserName());
                }

                cache.removeTransactionList(userId);
                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_MENU);
                return messageBuilder.text(userService.buildTextMessageForTransaction(last, user.getCard().getCardId()))
                        .build();
            }
            case MAIN_MENU_TRANSACTIONS_HISTORY_LAST: {
                User user = userService.getByUsername(userName);
                List<TransactionHistory> transactionList = cache.getTransactionList(userId).stream()
                        .sorted((o1, o2) -> {
                            if (o1.getProcessDate().compareTo(o2.getProcessDate()) > 0) {
                                return -1;
                            } else if (o1.getProcessDate().compareTo(o2.getProcessDate()) < 0) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }).limit(5)
                        .collect(Collectors.toList());
                TransactionHistory last = transactionList.get(transactionList.size() - 1);
                transactionList.remove(last);

                try {
                    sendMessageService.sendMessage(chatId, "Ваша история транзакций:");
                } catch (TelegramApiException e) {
                    log.error("Ошибка при отправлении сообщения: {} " +
                            "с chatId: {}, username: {}", e.getMessage(), user.getChatId(), user.getUserName());
                }

                try {
                    sendMessageService.sendTransactionHistory(chatId, transactionList, user.getCard().getCardId());
                } catch (TelegramApiException e) {
                    log.error("Ошибка при отправлении сообщения по получении истории транзакций пользователю: {} " +
                            "с chatId: {}, username: {}", e.getMessage(), user.getChatId(), user.getUserName());
                }

                cache.removeTransactionList(userId);
                cache.setBotStateForUser(userId, BotStateEnum.TRANSFER_MONEY_MENU);
                return messageBuilder.text(userService.buildTextMessageForTransaction(last, user.getCard().getCardId()))
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
