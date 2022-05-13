package ru.kim.volsu.telegram.bank.core.dao;

import ru.kim.volsu.telegram.bank.core.model.TransactionHistory;

import java.util.List;

public interface TransactionDao {
    void saveTransaction(TransactionHistory transactionHistory);

    List<TransactionHistory> getTransactionsByCardId(Integer currentCardId);

    TransactionHistory getLastTransactionToCard(Integer cardId);
}
