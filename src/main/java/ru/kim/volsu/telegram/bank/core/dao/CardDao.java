package ru.kim.volsu.telegram.bank.core.dao;

import ru.kim.volsu.telegram.bank.core.model.Card;

import java.math.BigDecimal;

public interface CardDao {
    Card getById(Integer cardId);

    void saveCard(Card card);

    void update(Card card);

    void makeTransaction(Card from, Card to, BigDecimal amount);
}
