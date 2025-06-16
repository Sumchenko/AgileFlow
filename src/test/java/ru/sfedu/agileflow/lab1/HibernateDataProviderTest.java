package ru.sfedu.agileflow.lab1;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.sfedu.agileflow.constants.Constants;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Тестовый класс для проверки методов HibernateDataProvider.
 */
public class HibernateDataProviderTest {
    private static final Logger log = Logger.getLogger(HibernateDataProviderTest.class);
    private HibernateDataProvider provider;

    /**
     * Подготовка перед выполнением всех тестов.
     * Проверяет доступность конфигурации.
     */
    @BeforeClass
    public static void setUpClass() {
        String methodName = "setUpClass";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            // Проверяем, что hibernate.cfg.xml доступен
            HibernateUtil.getSessionFactory();
            log.info("setUpClass [1] Конфигурация Hibernate успешно загружена");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось загрузить конфигурацию Hibernate: " + e.getMessage()), e);
            fail("Не удалось загрузить конфигурацию Hibernate: " + e.getMessage());
        }
    }

    /**
     * Подготовка перед каждым тестом.
     * Инициализирует HibernateDataProvider.
     */
    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            provider = new HibernateDataProvider();
            log.info("setUp [1] HibernateDataProvider успешно инициализирован");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось инициализировать HibernateDataProvider: " + e.getMessage()), e);
            fail("Не удалось инициализировать HibernateDataProvider: " + e.getMessage());
        }
    }

    /**
     * Очистка после выполнения всех тестов.
     * Закрывает SessionFactory.
     */
    @AfterClass
    public static void tearDown() {
        String methodName = "tearDown";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            HibernateUtil.close();
            log.info("tearDown [1] SessionFactory закрыт");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось закрыть SessionFactory: " + e.getMessage()), e);
        }
    }

    /**
     * Тестирование получения имени базы данных.
     * Тип: Позитивный
     */
    @Test
    public void testGetDatabaseNameSuccess() {
        String methodName = "testGetDatabaseNameSuccess";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetDatabaseNameSuccess [1] Выполнение запроса имени базы данных");
            Optional<String> result = provider.getDatabaseName();
            assertTrue("Имя базы данных должно быть возвращено", result.isPresent());
            assertEquals("AgileFlow", result.get());
            log.info("testGetDatabaseNameSuccess [2] Имя базы данных успешно получено: " + result.get());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить имя базы данных: " + e.getMessage()), e);
            fail("Не удалось получить имя базы данных: " + e.getMessage());
        }
    }

    /**
     * Тестирование получения имени базы данных после закрытия SessionFactory.
     * Тип: Негативный
     */
    @Test
    public void testGetDatabaseNameFailure() {
        String methodName = "testGetDatabaseNameFailure";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetDatabaseNameFailure [1] Закрытие SessionFactory");
            HibernateUtil.close();
            log.info("testGetDatabaseNameFailure [2] Попытка получения имени базы данных");
            provider.getDatabaseName();
            fail("Ожидалось исключение при попытке получения имени базы данных после закрытия SessionFactory");
        } catch (RuntimeException e) {
            log.info("testGetDatabaseNameFailure [3] Ожидаемое исключение получено: " + e.getMessage());
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Детали исключения: " + e.toString()));
            assertTrue("Сообщение об ошибке должно указывать на проблему с SessionFactory",
                    e.getMessage().contains("Не удалось получить имя базы данных"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } finally {
            // Переинициализируем SessionFactory для следующих тестов
            log.info("testGetDatabaseNameFailure [4] Переинициализация SessionFactory");
            provider = new HibernateDataProvider();
            log.info("testGetDatabaseNameFailure [5] SessionFactory переинициализирован");
        }
    }

    /**
     * Тестирование получения размера базы данных.
     * Тип: Позитивный
     */
    @Test
    public void testGetDatabaseSizeSuccess() {
        String methodName = "testGetDatabaseSizeSuccess";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetDatabaseSizeSuccess [1] Выполнение запроса размера базы данных");
            Optional<Double> result = provider.getDatabaseSize();
            assertTrue("Размер базы данных должен быть возвращен", result.isPresent());
            assertTrue("Размер базы данных должен быть больше 0", result.get() > 0);
            log.info("testGetDatabaseSizeSuccess [2] Размер базы данных успешно получен: " + result.get());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить размер базы данных: " + e.getMessage()), e);
            fail("Не удалось получить размер базы данных: " + e.getMessage());
        }
    }

    /**
     * Тестирование получения размера базы данных после закрытия SessionFactory.
     * Тип: Негативный
     */
    @Test
    public void testGetDatabaseSizeFailure() {
        String methodName = "testGetDatabaseSizeFailure";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetDatabaseSizeFailure [1] Закрытие SessionFactory");
            HibernateUtil.close();
            log.info("testGetDatabaseSizeFailure [2] Попытка получения размера базы данных");
            provider.getDatabaseSize();
            fail("Ожидалось исключение при попытке получения размера базы данных после закрытия SessionFactory");
        } catch (RuntimeException e) {
            log.info("testGetDatabaseSizeFailure [3] Ожидаемое исключение получено: " + e.getMessage());
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Детали исключения: " + e.toString()));
            assertTrue("Сообщение об ошибке должно указывать на проблему с SessionFactory",
                    e.getMessage().contains("Не удалось получить размер базы данных"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } finally {
            // Переинициализируем SessionFactory для следующих тестов
            log.info("testGetDatabaseSizeFailure [4] Переинициализация SessionFactory");
            provider = new HibernateDataProvider();
            log.info("testGetDatabaseSizeFailure [5] SessionFactory переинициализирован");
        }
    }

    /**
     * Тестирование получения списка таблиц.
     * Тип: Позитивный
     */
    @Test
    public void testGetTableNamesSuccess() {
        String methodName = "testGetTableNamesSuccess";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetTableNamesSuccess [1] Выполнение запроса списка таблиц");
            List<String> result = provider.getTableNames();
            assertFalse("Список таблиц не должен быть пустым", result.isEmpty());
            assertTrue("Список должен содержать таблицу 'users'", result.contains("users"));
            log.info("testGetTableNamesSuccess [2] Список таблиц успешно получен: " + result);
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить список таблиц: " + e.getMessage()), e);
            fail("Не удалось получить список таблиц: " + e.getMessage());
        }
    }

    /**
     * Тестирование получения списка таблиц после закрытия SessionFactory.
     * Тип: Негативный
     */
    @Test
    public void testGetTableNamesFailure() {
        String methodName = "testGetTableNamesFailure";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetTableNamesFailure [1] Закрытие SessionFactory");
            HibernateUtil.close();
            log.info("testGetTableNamesFailure [2] Попытка получения списка таблиц");
            provider.getTableNames();
            fail("Ожидалось исключение при попытке получения списка таблиц после закрытия SessionFactory");
        } catch (RuntimeException e) {
            log.info("testGetTableNamesFailure [3] Ожидаемое исключение получено: " + e.getMessage());
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Детали исключения: " + e.toString()));
            assertTrue("Сообщение об ошибке должно указывать на проблему с SessionFactory",
                    e.getMessage().contains("Не удалось получить список таблиц"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } finally {
            // Переинициализируем SessionFactory для следующих тестов
            log.info("testGetTableNamesFailure [4] Переинициализация SessionFactory");
            provider = new HibernateDataProvider();
            log.info("testGetTableNamesFailure [5] SessionFactory переинициализирован");
        }
    }

    /**
     * Тестирование получения версии сервера.
     * Тип: Позитивный
     */
    @Test
    public void testGetServerVersionSuccess() {
        String methodName = "testGetServerVersionSuccess";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetServerVersionSuccess [1] Выполнение запроса версии сервера");
            Optional<String> result = provider.getServerVersion();
            assertTrue("Версия сервера должна быть возвращена", result.isPresent());
            assertTrue("Версия сервера должна содержать 'PostgreSQL'", result.get().contains("PostgreSQL"));
            log.info("testGetServerVersionSuccess [2] Версия сервера успешно получена: " + result.get());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить версию сервера: " + e.getMessage()), e);
            fail("Не удалось получить версию сервера: " + e.getMessage());
        }
    }

    /**
     * Тестирование получения версии сервера после закрытия SessionFactory.
     * Тип: Негативный
     */
    @Test
    public void testGetServerVersionFailure() {
        String methodName = "testGetServerVersionFailure";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetServerVersionFailure [1] Закрытие SessionFactory");
            HibernateUtil.close();
            log.info("testGetServerVersionFailure [2] Попытка получения версии сервера");
            provider.getServerVersion();
            fail("Ожидалось исключение при попытке получения версии сервера после закрытия SessionFactory");
        } catch (RuntimeException e) {
            log.info("testGetServerVersionFailure [3] Ожидаемое исключение получено: " + e.getMessage());
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Детали исключения: " + e.toString()));
            assertTrue("Сообщение об ошибке должно указывать на проблему с SessionFactory",
                    e.getMessage().contains("Не удалось получить версию сервера"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } finally {
            // Переинициализируем SessionFactory для следующих тестов
            log.info("testGetServerVersionFailure [4] Переинициализация SessionFactory");
            provider = new HibernateDataProvider();
            log.info("testGetServerVersionFailure [5] SessionFactory переинициализирован");
        }
    }
}