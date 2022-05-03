package ru.kim.volsu.telegram.bank.telegram.handler;

import org.springframework.stereotype.Component;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;

@Component
public class MessageHandler {

    public void processState(BotStateEnum state) {
        switch (state) {
            case ACCOUNT_DETAILS:
                break;
            case TRANSFER_MONEY:
                break;
        }
    }
}
