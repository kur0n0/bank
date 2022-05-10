package ru.kim.volsu.telegram.bank.core.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import ru.kim.volsu.telegram.bank.core.configuration.HibernateUtil;
import ru.kim.volsu.telegram.bank.core.model.Card;

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
}
