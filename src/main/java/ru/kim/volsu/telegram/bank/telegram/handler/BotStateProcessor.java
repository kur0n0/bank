package ru.kim.volsu.telegram.bank.telegram.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStateProcessor {

    private Map<BotStateEnum, MessageHandler> messageHandlers = new HashMap<>();

    public BotStateProcessor(List<MessageHandler> messageHandlerList) {
        messageHandlerList.forEach(
                handler -> messageHandlers.put(handler.getBotState(), handler)
        );
    }

    public SendMessage processInputMessage(BotStateEnum botState, Message message) {
        return findMessageHandler(botState).handle(message);
    }

    public MessageHandler findMessageHandler(BotStateEnum botState) {
        if (isTransferMoneyHandler(botState)) {
            return messageHandlers.get(botState);
        }

        if (isAccountDetailsHandler(botState)) {
            return messageHandlers.get(botState);
        }

        return messageHandlers.get(BotStateEnum.MAIN_MENU);
    }

    private boolean isAccountDetailsHandler(BotStateEnum botState) {
        switch (botState) {
            case ACCOUNT_DETAILS_MENU:
            case ACOUNT_DETAILS_INFO:
                return true;
            default:
                return false;
        }
    }

    private boolean isTransferMoneyHandler(BotStateEnum botState) {
        switch (botState) {
            case TRANSFER_MONEY_MENU:
            case TRANSFER_MONEY_ASK_PHONE_NUMBER:
                return true;
            default:
                return false;
        }
    }
}
