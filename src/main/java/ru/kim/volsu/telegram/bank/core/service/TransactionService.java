package ru.kim.volsu.telegram.bank.core.service;

import ru.kim.volsu.telegram.bank.core.model.TransactionHistory;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    void transferMoney(String fromUser, String toUser, BigDecimal amount);

    void saveTransaction(TransactionHistory transactionHistory);

    List<TransactionHistory> getTransactionsByCardId(Integer currentCardId);

    TransactionHistory getLastTransactionToCard(Integer cardId);
}
