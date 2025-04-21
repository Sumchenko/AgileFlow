package ru.sfedu.agileflow.config;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.sfedu.agileflow.constants.Constants;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Тестовый класс для проверки конфигурации базы данных.
 */
public class DatabaseConfigTest {
    private static final Logger log = Logger.getLogger(DatabaseConfigTest.class);

    /**
     * Подготовка перед выполнением тестов.
     * Проверяет наличие свойств базы данных.
     */
    @BeforeClass
    public static void setUp() {
        String methodName = "setUp";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Properties props = new Properties();
            props.load(DatabaseConfigTest.class.getClassLoader().getResourceAsStream(Constants.DB_PROPERTIES_PATH));
            assertNotNull("Файл database.properties должен существовать", props);
            assertFalse("Свойства не должны быть пустыми", props.isEmpty());
            log.info("setUp [1] Свойства базы данных успешно загружены");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (IOException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось загрузить свойства базы данных: " + e.getMessage()), e);
            fail("Не удалось загрузить свойства базы данных: " + e.getMessage());
        }
    }

    /**
     * Очистка после выполнения тестов.
     * Закрывает EntityManagerFactory.
     */
    @AfterClass
    public static void tearDown() {
        String methodName = "tearDown";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            DatabaseConfig.close();
            log.info("tearDown [1] EntityManagerFactory закрыт");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось закрыть EntityManagerFactory: " + e.getMessage()), e);
        }
    }

    /**
     * Тестирование успешного соединения с базой данных.
     * Тип: Позитивный
     */
    @Test
    public void testConnectionSuccess() {
        String methodName = "testConnectionSuccess";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testConnectionSuccess [1] Выполнение тестового соединения");
            DatabaseConfig.testConnection();
            log.info("testConnectionSuccess [2] Тестовое соединение успешно выполнено");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить тестовое соединение: " + e.getMessage()), e);
            fail("Не удалось выполнить тестовое соединение: " + e.getMessage());
        }
    }

    /**
     * Тестирование попытки получения EntityManager после закрытия EntityManagerFactory.
     * Тип: Негативный
     */
    @Test
    public void testConnectionFailure() {
        String methodName = "testConnectionFailure";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testConnectionFailure [1] Закрытие EntityManagerFactory");
            DatabaseConfig.close();
            log.info("testConnectionFailure [2] Попытка получения EntityManager");
            DatabaseConfig.getEntityManager();
            fail("Ожидалось исключение при попытке получения EntityManager после закрытия EntityManagerFactory");
        } catch (RuntimeException e) {
            log.info("testConnectionFailure [3] Ожидаемое исключение получено: " + e.getMessage());
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Детали исключения: " + e.toString()));
            assertTrue("Сообщение об ошибке должно указывать на проблему с EntityManager",
                    e.getMessage().contains("Не удалось создать EntityManager"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        }
    }
}