package ru.kim.volsu.telegram.bank.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kim.volsu.telegram.bank.core.dao.TransactionDao;
import ru.kim.volsu.telegram.bank.core.model.TransactionHistory;
import ru.kim.volsu.telegram.bank.core.model.User;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private UserService userService;

    private TransactionDao transactionDao;

    private CardService cardService;

    @Autowired
    public TransactionServiceImpl(UserService userService, TransactionDao transactionDao, CardService cardService) {
        this.userService = userService;
        this.transactionDao = transactionDao;
        this.cardService = cardService;
    }

    @Override
    public void transferMoney(String fromUsername, String toUsername, BigDecimal amount) {
        User fromUser = userService.getByUsername(fromUsername);
        User toUser = userService.getByUsername(toUsername);

        if (fromUser != null && toUser != null) {
            cardService.makeTransaction(fromUser.getCard(), toUser.getCard(), amount);

            TransactionHistory transactionHistory = new TransactionHistory();
            transactionHistory.setAmount(amount);
            transactionHistory.setFrom(fromUser.getCard());
            transactionHistory.setTo(toUser.getCard());
            saveTransaction(transactionHistory);
        }
    }

    @Override
    public void saveTransaction(TransactionHistory transactionHistory) {
        transactionDao.saveTransaction(transactionHistory);
    }

    @Override
    public List<TransactionHistory> getTransactionsByCardId(Integer currentCardId) {
        return transactionDao.getTransactionsByCardId(currentCardId);
    }

    @Override
    public TransactionHistory getLastTransactionToCard(Integer cardId) {
        return transactionDao.getLastTransactionToCard(cardId);
    }
}
