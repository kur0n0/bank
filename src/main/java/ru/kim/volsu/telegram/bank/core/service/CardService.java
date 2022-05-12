package ru.kim.volsu.telegram.bank.core.service;

import ru.kim.volsu.telegram.bank.core.model.Card;

import java.math.BigDecimal;

public interface CardService {
    void saveCard(Card card);

    void makeTransaction(Card from, Card to, BigDecimal amount);
}
