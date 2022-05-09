package ru.kim.volsu.telegram.bank.telegram.cache;

import org.springframework.stereotype.Component;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;

import java.util.HashMap;
import java.util.Map;

@Component
public class Cache {
    private Map<Long, BotStateEnum> userState = new HashMap<>();

    public BotStateEnum getBotStateByUserId(Long userId) {
        return userState.getOrDefault(userId, BotStateEnum.MAIN_MENU);
    }

    public void setBotStateForUser(Long userId, BotStateEnum botState) {
        if(userState.containsKey(userId)) {
            userState.replace(userId, botState);
        } else {
            userState.put(userId, botState);
        }
    }

    public void clearCache() {
        userState = new HashMap<>();
    }
}
