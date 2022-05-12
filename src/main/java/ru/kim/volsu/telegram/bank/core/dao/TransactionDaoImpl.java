package ru.kim.volsu.telegram.bank.core.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import ru.kim.volsu.telegram.bank.core.configuration.HibernateUtil;
import ru.kim.volsu.telegram.bank.core.model.TransactionHistory;

@Repository
public class TransactionDaoImpl implements TransactionDao {
    private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private static Logger log = LogManager.getLogger(TransactionDaoImpl.class);

    @Override
    public void saveTransaction(TransactionHistory transactionHistory) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.save(transactionHistory);
            transaction.commit();
        } catch (Exception e) {
            log.error("Ошибка при добавлении пользователя в бд: {}", e.getMessage());
            transaction.rollback();
        } finally {
            session.close();
        }
    }
}
