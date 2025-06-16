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
    private static final EntityManagerFactory lab3SingleTableEmf;
    private static final EntityManagerFactory lab3TablePerClassEmf;
    private static final EntityManagerFactory lab3JoinedTableEmf;
    private static final EntityManagerFactory lab3MappedSuperclassEmf;
    private static final EntityManagerFactory lab4Emf;
    private static final EntityManagerFactory lab5Emf;

    static {
        String methodName = "static_initializer";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Properties props = new Properties();
            log.info("static_initializer [1] Загрузка свойств базы данных");
            props.load(DatabaseConfig.class.getClassLoader().getResourceAsStream(Constants.DB_PROPERTIES_PATH));
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Свойства загружены: " + props.toString()));

            log.info("static_initializer [2] Инициализация EntityManagerFactory для AgileFlowPU");
            emf = Persistence.createEntityManagerFactory("AgileFlowPU", props);
            log.info("static_initializer [3] EntityManagerFactory для AgileFlowPU успешно инициализирован");

            log.info("static_initializer [4] Инициализация EntityManagerFactory для Lab3SingleTablePU");
            lab3SingleTableEmf = Persistence.createEntityManagerFactory("Lab3SingleTablePU", props);
            log.info("static_initializer [5] EntityManagerFactory для Lab3SingleTablePU успешно инициализирован");

            log.info("static_initializer [6] Инициализация EntityManagerFactory для Lab3TablePerClassPU");
            lab3TablePerClassEmf = Persistence.createEntityManagerFactory("Lab3TablePerClassPU", props);
            log.info("static_initializer [7] EntityManagerFactory для Lab3TablePerClassPU успешно инициализирован");

            log.info("static_initializer [8] Инициализация EntityManagerFactory для Lab3JoinedTablePU");
            lab3JoinedTableEmf = Persistence.createEntityManagerFactory("Lab3JoinedTablePU", props);
            log.info("static_initializer [9] EntityManagerFactory для Lab3JoinedTablePU успешно инициализирован");

            log.info("static_initializer [10] Инициализация EntityManagerFactory для Lab3MappedSuperclassPU");
            lab3MappedSuperclassEmf = Persistence.createEntityManagerFactory("Lab3MappedSuperclassPU", props);
            log.info("static_initializer [11] EntityManagerFactory для Lab3MappedSuperclassPU успешно инициализирован");

            log.info("static_initializer [12] Инициализация EntityManagerFactory для Lab4PU");
            lab4Emf = Persistence.createEntityManagerFactory("Lab4PU", props);
            log.info("static_initializer [13] EntityManagerFactory для Lab4PU успешно инициализирован");

            log.info("static_initializer [14] Инициализация EntityManagerFactory для Lab5PU");
            lab5Emf = Persistence.createEntityManagerFactory("Lab5PU", props);
            log.info("static_initializer [15] EntityManagerFactory для Lab5PU успешно инициализирован");
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
     * Получает EntityManager для выполнения операций с базой данных (основная PU).
     * @return EntityManager для работы с базой данных
     * @throws RuntimeException если не удалось создать EntityManager
     */
    public static EntityManager getEntityManager() {
        return getEntityManager("AgileFlowPU");
    }

    /**
     * Получает EntityManager для выполнения операций с базой данных для лабораторной работы (Single Table).
     * @return EntityManager для работы с базой данных
     * @throws RuntimeException если не удалось создать EntityManager
     */
    public static EntityManager getLab3SingleTableEntityManager() {
        return getEntityManager("Lab3SingleTablePU");
    }

    /**
     * Получает EntityManager для выполнения операций с базой данных для лабораторной работы (Table Per Class).
     * @return EntityManager для работы с базой данных
     * @throws RuntimeException если не удалось создать EntityManager
     */
    public static EntityManager getLab3TablePerClassEntityManager() {
        return getEntityManager("Lab3TablePerClassPU");
    }

    /**
     * Получает EntityManager для выполнения операций с базой данных для лабораторной работы (Joined Table).
     * @return EntityManager для работы с базой данных
     * @throws RuntimeException если не удалось создать EntityManager
     */
    public static EntityManager getLab3JoinedTableEntityManager() {
        return getEntityManager("Lab3JoinedTablePU");
    }

    /**
     * Получает EntityManager для выполнения операций с базой данных для лабораторной работы (MappedSuperclass).
     * @return EntityManager для работы с базой данных
     * @throws RuntimeException если не удалось создать EntityManager
     */
    public static EntityManager getLab3MappedSuperclassEntityManager() {
        return getEntityManager("Lab3MappedSuperclassPU");
    }

    /**
     * Получает EntityManager для выполнения операций с базой данных для лабораторной работы 4.
     * @return EntityManager для работы с базой данных
     * @throws RuntimeException если не удалось создать EntityManager
     */
    public static EntityManager getLab4EntityManager() {
        return getEntityManager("Lab4PU");
    }

    /**
     * Получает EntityManager для выполнения операций с базой данных для лабораторной работы 5.
     * @return EntityManager для работы с базой данных
     * @throws RuntimeException если не удалось создать EntityManager
     */
    public static EntityManager getLab5EntityManager() {
        return getEntityManager("Lab5PU");
    }

    private static EntityManager getEntityManager(String persistenceUnitName) {
        String methodName = "getEntityManager_" + persistenceUnitName;
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            EntityManagerFactory factory;
            switch (persistenceUnitName) {
                case "Lab3SingleTablePU":
                    factory = lab3SingleTableEmf;
                    break;
                case "Lab3TablePerClassPU":
                    factory = lab3TablePerClassEmf;
                    break;
                case "Lab3JoinedTablePU":
                    factory = lab3JoinedTableEmf;
                    break;
                case "Lab3MappedSuperclassPU":
                    factory = lab3MappedSuperclassEmf;
                    break;
                case "Lab4PU":
                    factory = lab4Emf;
                    break;
                case "Lab5PU":
                    factory = lab5Emf;
                    break;
                default:
                    factory = emf;
            }
            if (factory == null || !factory.isOpen()) {
                log.error(String.format(Constants.LOG_ERROR, methodName, "EntityManagerFactory не инициализирован или закрыт"));
                throw new RuntimeException("EntityManagerFactory не инициализирован или закрыт");
            }
            log.info(methodName + " [1] Создание EntityManager");
            EntityManager em = factory.createEntityManager();
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
                log.info("close [1] Закрытие EntityManagerFactory для AgileFlowPU");
                emf.close();
                log.info("close [2] EntityManagerFactory для AgileFlowPU успешно закрыт");
            }
            if (lab3SingleTableEmf != null && lab3SingleTableEmf.isOpen()) {
                log.info("close [3] Закрытие EntityManagerFactory для Lab3SingleTablePU");
                lab3SingleTableEmf.close();
                log.info("close [4] EntityManagerFactory для Lab3SingleTablePU успешно закрыт");
            }
            if (lab3TablePerClassEmf != null && lab3TablePerClassEmf.isOpen()) {
                log.info("close [5] Закрытие EntityManagerFactory для Lab3TablePerClassPU");
                lab3TablePerClassEmf.close();
                log.info("close [6] EntityManagerFactory для Lab3TablePerClassPU успешно закрыт");
            }
            if (lab3JoinedTableEmf != null && lab3JoinedTableEmf.isOpen()) {
                log.info("close [7] Закрытие EntityManagerFactory для Lab3JoinedTablePU");
                lab3JoinedTableEmf.close();
                log.info("close [8] EntityManagerFactory для Lab3JoinedTablePU успешно закрыт");
            }
            if (lab3MappedSuperclassEmf != null && lab3MappedSuperclassEmf.isOpen()) {
                log.info("close [9] Закрытие EntityManagerFactory для Lab3MappedSuperclassPU");
                lab3MappedSuperclassEmf.close();
                log.info("close [10] EntityManagerFactory для Lab3MappedSuperclassPU успешно закрыт");
            }
            if (lab4Emf != null && lab4Emf.isOpen()) {
                log.info("close [11] Закрытие EntityManagerFactory для Lab4PU");
                lab4Emf.close();
                log.info("close [12] EntityManagerFactory для Lab4PU успешно закрыт");
            }
            if (lab5Emf != null && lab5Emf.isOpen()) {
                log.info("close [13] Закрытие EntityManagerFactory для Lab5PU");
                lab5Emf.close();
                log.info("close [14] EntityManagerFactory для Lab5PU успешно закрыт");
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "EntityManagerFactory уже закрыт или не инициализирован"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось закрыть EntityManagerFactory: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось закрыть EntityManagerFactory", e);
        }
    }
}