package ru.kim.volsu.telegram.bank.telegram.cache;

import org.springframework.stereotype.Component;
import ru.kim.volsu.telegram.bank.core.model.Card;
import ru.kim.volsu.telegram.bank.telegram.dto.TransferMoneyDto;
import ru.kim.volsu.telegram.bank.telegram.dto.TransferMoneyRequestDto;
import ru.kim.volsu.telegram.bank.telegram.enums.BotStateEnum;

import java.util.HashMap;
import java.util.Map;

@Component
public class Cache {
    private Map<Long, BotStateEnum> userState = new HashMap<>();
    private Map<Long, Card> userCard = new HashMap<>();
    private Map<Long, TransferMoneyRequestDto> fromUserToUserTransaction = new HashMap<>();
    private Map<Long, TransferMoneyDto> transferMoneyDtoMap = new HashMap<>();

    public BotStateEnum getBotStateByUserId(Long userId) {
        return userState.getOrDefault(userId, BotStateEnum.MAIN_MENU);
    }

    public void setBotStateForUser(Long userId, BotStateEnum botState) {
        if (userState.containsKey(userId)) {
            userState.replace(userId, botState);
        } else {
            userState.put(userId, botState);
        }
    }

    public void setUserCard(Long userId, Card card) {
        userCard.put(userId, card);
    }

    public Card getCardByUserId(Long userId) {
        return userCard.get(userId);
    }

    public void removeCardCache(Long userId) {
        userCard.remove(userId);
    }

    public void setFromTo(Long userId, TransferMoneyRequestDto transferMoneyRequestDto) {
        fromUserToUserTransaction.put(userId, transferMoneyRequestDto);
    }

    public TransferMoneyRequestDto getFromTo(Long userId) {
        return fromUserToUserTransaction.get(userId);
    }

    public void removeFromTo(Long userId) {
        fromUserToUserTransaction.remove(userId);
    }

    public void setTransferMoneyDto(Long userId, TransferMoneyDto transferMoneyDto) {
        transferMoneyDtoMap.put(userId, transferMoneyDto);
    }

    public TransferMoneyDto getTransferMoneyDto(Long userId) {
        return transferMoneyDtoMap.get(userId);
    }

    public void removeTransferMoneyDto(Long userId) {
        transferMoneyDtoMap.remove(userId);
    }
}
