package ru.kim.volsu.telegram.bank.telegram.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStateProcessor {

    private final Map<BotStateEnum, MessageHandler> messageHandlers = new HashMap<>();

    public BotStateProcessor(List<MessageHandler> messageHandlerList) {
        messageHandlerList.forEach(
                handler -> messageHandlers.put(handler.getBotState(), handler)
        );
    }

    public SendMessage processInputMessage(BotStateEnum botState, Update update) {
        return findMessageHandler(botState).handle(update);
    }

    public MessageHandler findMessageHandler(BotStateEnum botState) {
        if (isTransferMoneyHandler(botState)) {
            return messageHandlers.get(BotStateEnum.TRANSFER_MONEY_MENU);
        }

        if (isValuteHandler(botState)) {
            return messageHandlers.get(BotStateEnum.VALUTE_MENU);
        }

        return messageHandlers.get(BotStateEnum.MAIN_MENU);
    }

    private boolean isTransferMoneyHandler(BotStateEnum botState) {
        switch (botState) {
            case TRANSFER_MONEY_MENU:
            case TRANSFER_MONEY_ASK_USERNAME:
            case TRANSFER_MONEY_REGISTRATE_CARD:
            case TRANSFER_MONEY_ASK_CARD_NUMBER:
            case TRANSFER_MONEY_ASK_EXPIRE_DATE:
            case TRANSFER_MONEY_ASK_CVV:
            case TRANSFER_MONEY_TRANSACTION:
            case TRANSFER_MONEY_ASK_AMOUNT:
            case TRANSFER_MONEY_CONFIRMATION:
            case MAIN_MENU_TRANSACTIONS_HISTORY_LAST:
            case MAIN_MENU_TRANSACTIONS_HISTORY_ALL:
                return true;
            default:
                return false;
        }
    }

    private boolean isValuteHandler(BotStateEnum botState) {
        switch (botState) {
            case VALUTE_MENU:
            case VALUTE_PRINT:
                return true;
            default:
                return false;
        }
    }
}
