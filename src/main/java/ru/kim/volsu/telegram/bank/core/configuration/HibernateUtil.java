package ru.kim.volsu.telegram.bank.core.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static Logger log = LogManager.getLogger(HibernateUtil.class);

    public static SessionFactory getSessionFactory() {
        if (sessionFactory != null) {
            return sessionFactory;
        }

        try {
            Configuration configuration = new Configuration().configure();
            return configuration.buildSessionFactory();
        } catch (HibernateException e) {
            log.error("Ошибка при конфигурации подключения к бд: {}", e.getMessage());
            throw e;
        }
    }
}
