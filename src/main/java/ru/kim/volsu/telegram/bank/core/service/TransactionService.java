package ru.kim.volsu.telegram.bank.core.service;

import ru.kim.volsu.telegram.bank.core.model.TransactionHistory;
import ru.kim.volsu.telegram.bank.telegram.dto.TransferMoneyDto;

import java.util.List;

public interface TransactionService {
    void transferMoney(TransferMoneyDto transferMoneyDto);

    void saveTransaction(TransactionHistory transactionHistory);

    List<TransactionHistory> getTransactionsByCardId(Integer currentCardId);

    TransactionHistory getLastTransactionToCard(Integer cardId);
}
