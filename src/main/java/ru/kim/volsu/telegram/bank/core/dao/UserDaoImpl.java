package ru.kim.volsu.telegram.bank.core.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import ru.kim.volsu.telegram.bank.core.configuration.HibernateUtil;
import ru.kim.volsu.telegram.bank.core.model.User;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private static Logger log = LogManager.getLogger(UserDaoImpl.class);

    @Override
    public User getByChatId(String chatId) {
        Session session = sessionFactory.openSession();
        List<User> resultList = session.createQuery("from User u where u.chatId = :chatId")
                .setParameter("chatId", chatId)
                .setMaxResults(1)
                .getResultList();
        session.close();

        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @Override
    public void saveUser(User user) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            log.error("Ошибка при добавлении пользователя в бд: {}, userName: {}",
                    e.getMessage(), user.getUserName());
            transaction.rollback();
        } finally {
            session.close();
        }
    }

    @Override
    public void update(User user) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(user);
            transaction.commit();
        } catch (Exception e) {
            log.error("Ошибка при обновлении данных пользователя в бд: {}, userName: {}",
                    e.getMessage(), user.getUserName());
            transaction.rollback();
        } finally {
            session.close();
        }
    }

    @Override
    public User getByUsername(String username) {
        Session session = sessionFactory.openSession();
        List<User> resultList = session.createQuery("from User u where u.userName = :username")
                .setParameter("username", username)
                .setMaxResults(1)
                .getResultList();
        session.close();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @Override
    public User getByCardId(Integer to) {
        Session session = sessionFactory.openSession();
        User user = session.createQuery("from User u where u.card.cardId = :to", User.class)
                .setParameter("to", to)
                .getSingleResult();
        session.close();
        return user;
    }
}
