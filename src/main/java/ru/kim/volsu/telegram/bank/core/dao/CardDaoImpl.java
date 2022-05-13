package ru.kim.volsu.telegram.bank.core.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import ru.kim.volsu.telegram.bank.core.configuration.HibernateUtil;
import ru.kim.volsu.telegram.bank.core.model.Card;

import java.math.BigDecimal;

@Repository
public class CardDaoImpl implements CardDao {

    private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private static Logger log = LogManager.getLogger(CardDaoImpl.class);

    @Override
    public Card getById(Integer cardId) {
        return sessionFactory.openSession().get(Card.class, cardId);
    }

    @Override
    public void saveCard(Card card) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.save(card);
            transaction.commit();
        } catch (Exception e) {
            log.error("Ошибка при добавлении карты в бд: {}", e.getMessage());
            transaction.rollback();
        }
    }

    @Override
    public void update(Card card) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(card);
            transaction.commit();
        } catch (Exception e) {
            log.error("Ошибка при обновлении данных карты: {}, cardId: {}",
                    e.getMessage(), card.getCardId());
            transaction.rollback();
        } finally {
            session.close();
        }
    }

    @Override
    public void makeTransaction(Card from, Card to, BigDecimal amount) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            BigDecimal subtractedAmount = from.getActualBalance().subtract(amount);
            from.setActualBalance(subtractedAmount);

            BigDecimal addedAmount = to.getActualBalance().add(amount);
            to.setActualBalance(addedAmount);

            update(from);
            update(to);
            transaction.commit();
        } catch (Exception e) {
            log.error("Ошибка при выполнении транзакции: {}, с карты {} на карту {}",
                    e.getMessage(), from.getCardId(), to.getCardId());
            transaction.rollback();
        } finally {
            session.close();
        }
    }
}
