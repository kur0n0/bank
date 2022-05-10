package ru.kim.volsu.telegram.bank.core.dao;

import ru.kim.volsu.telegram.bank.core.model.Card;

public interface CardDao {
    Card getById(Integer cardId);

    void saveCard(Card card);
}
