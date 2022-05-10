package ru.kim.volsu.telegram.bank.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kim.volsu.telegram.bank.core.dao.CardDao;
import ru.kim.volsu.telegram.bank.core.model.Card;

@Service
public class CardServiceImpl implements CardService {

    private final CardDao cardDao;

    @Autowired
    public CardServiceImpl(CardDao cardDao) {
        this.cardDao = cardDao;
    }

    @Override
    public void saveCard(Card card) {
        cardDao.saveCard(card);
    }
}
