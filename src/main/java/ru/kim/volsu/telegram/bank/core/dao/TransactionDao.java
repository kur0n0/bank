package ru.kim.volsu.telegram.bank.core.dao;

import ru.kim.volsu.telegram.bank.core.model.TransactionHistory;

public interface TransactionDao {
    void saveTransaction(TransactionHistory transactionHistory);
}
