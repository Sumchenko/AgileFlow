package ru.sfedu.agileflow.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.constants.Constants;

import java.io.IOException;
import java.util.Properties;

/**
 * Класс для конфигурации и управления подключением к базе данных через Hibernate.
 */
public class DatabaseConfig {
    private static final Logger log = Logger.getLogger(DatabaseConfig.class);
    private static final EntityManagerFactory emf;

    static {
        String methodName = "static_initializer";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Properties props = new Properties();
            log.info("static_initializer [1] Загрузка свойств базы данных");
            props.load(DatabaseConfig.class.getClassLoader().getResourceAsStream(Constants.DB_PROPERTIES_PATH));
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Свойства загружены: " + props.toString()));

            log.info("static_initializer [2] Инициализация EntityManagerFactory");
            emf = Persistence.createEntityManagerFactory("AgileFlowPU", props);
            log.info("static_initializer [3] EntityManagerFactory успешно инициализирован");
        } catch (IOException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось загрузить свойства базы данных: " + e.getMessage()));
            throw new RuntimeException("Не удалось загрузить свойства базы данных", e);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось инициализировать EntityManagerFactory: " + e.getMessage()));
            throw new RuntimeException("Не удалось инициализировать Hibernate", e);
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Получает EntityManager для выполнения операций с базой данных.
     * @return EntityManager для работы с базой данных
     * @throws RuntimeException если не удалось создать EntityManager
     */
    public static EntityManager getEntityManager() {
        String methodName = "getEntityManager";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            if (emf == null || !emf.isOpen()) {
                log.error(String.format(Constants.LOG_ERROR, methodName, "EntityManagerFactory не инициализирован или закрыт"));
                throw new RuntimeException("EntityManagerFactory не инициализирован или закрыт");
            }
            log.info("getEntityManager [1] Создание EntityManager");
            EntityManager em = emf.createEntityManager();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "EntityManager успешно создан"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return em;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать EntityManager: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось создать EntityManager", e);
        }
    }

    /**
     * Выполняет тестовое соединение к базе данных и проверяет доступность.
     * @throws RuntimeException если не удалось установить соединение
     */
    public static void testConnection() {
        String methodName = "testConnection";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try (EntityManager em = getEntityManager()) {
            log.info("testConnection [1] Проверка соединения с базой данных");
            // Выполняем простой запрос для проверки соединения
            em.createNativeQuery("SELECT 1").getSingleResult();
            log.info("testConnection [2] Соединение успешно проверено");
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Соединение успешно протестировано"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось протестировать соединение: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось протестировать соединение: " + e.getMessage(), e);
        }
    }

    /**
     * Закрывает EntityManagerFactory и освобождает ресурсы.
     */
    public static void close() {
        String methodName = "close";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            if (emf != null && emf.isOpen()) {
                log.info("close [1] Закрытие EntityManagerFactory");
                emf.close();
                log.info("close [2] EntityManagerFactory успешно закрыт");
            } else {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "EntityManagerFactory уже закрыт или не инициализирован"));
            }
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось закрыть EntityManagerFactory: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось закрыть EntityManagerFactory", e);
        }
    }
}