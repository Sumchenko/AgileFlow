package ru.sfedu.agileflow.xml;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.config.XmlConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.User;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Тестовый класс для проверки операций XML DAO с пользователями.
 */
public class UserXmlDAOTest {
    private static final Logger log = Logger.getLogger(UserXmlDAOTest.class);
    private UserXmlDAO userDAO;

    /**
     * Подготовка перед каждым тестом.
     */
    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        userDAO = new UserXmlDAO();
        log.info("setUp [1] Инициализация UserXmlDAO завершена");
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Очистка после каждого теста.
     */
    @After
    public void tearDown() {
        String methodName = "tearDown";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            List<User> users = userDAO.findAll();
            for (User user : users) {
                userDAO.delete(user.getId());
            }
            File file = new File(XmlConfig.getFilePath(User.class));
            if (file.exists()) {
                file.delete();
            }
            log.info("tearDown [1] Все пользователи удалены, XML файл очищен");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось очистить данные: " + e.getMessage()), e);
        }
    }

    /**
     * Тестирование создания пользователя.
     * Тип: Позитивный
     */
    @Test
    public void testCreateUser() {
        String methodName = "testCreateUser";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            log.info("testCreateUser [1] Создание пользователя");
            userDAO.create(user);
            assertNotNull("Идентификатор пользователя должен быть установлен", user.getId());
            log.info("testCreateUser [2] Пользователь успешно создан с ID: " + user.getId());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать пользователя: " + e.getMessage()), e);
            fail("Не удалось создать пользователя: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска пользователя по идентификатору.
     * Тип: Позитивный
     */
    @Test
    public void testFindById() {
        String methodName = "testFindById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            log.info("testFindById [1] Создание пользователя");
            userDAO.create(user);
            Integer id = user.getId();

            log.info("testFindById [2] Поиск пользователя по ID: " + id);
            Optional<User> found = userDAO.findById(id);
            assertTrue("Пользователь должен быть найден", found.isPresent());
            assertEquals("Email пользователя должен совпадать", "test@example.com", found.get().getEmail());
            log.info("testFindById [3] Пользователь успешно найден");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти пользователя: " + e.getMessage()), e);
            fail("Не удалось найти пользователя: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска несуществующего пользователя.
     * Тип: Негативный
     */
    @Test
    public void testFindByIdNotFound() {
        String methodName = "testFindByIdNotFound";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testFindByIdNotFound [1] Поиск пользователя с ID: 999");
            Optional<User> found = userDAO.findById(999);
            assertFalse("Пользователь не должен быть найден", found.isPresent());
            log.info("testFindByIdNotFound [2] Пользователь не найден, как ожидалось");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить поиск: " + e.getMessage()), e);
            fail("Не удалось выполнить поиск: " + e.getMessage());
        }
    }

    /**
     * Тестирование получения списка всех пользователей.
     * Тип: Позитивный
     */
    @Test
    public void testFindAll() {
        String methodName = "testFindAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user1 = new User("Пользователь 1", "user1@example.com", "Биография 1", true, new Date());
            User user2 = new User("Пользователь 2", "user2@example.com", "Биография 2", true, new Date());
            log.info("testFindAll [1] Создание двух пользователей");
            userDAO.create(user1);
            userDAO.create(user2);

            log.info("testFindAll [2] Получение списка всех пользователей");
            List<User> users = userDAO.findAll();
            assertEquals("Должно быть найдено 2 пользователя", 2, users.size());
            log.info("testFindAll [3] Найдено пользователей: " + users.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить список пользователей: " + e.getMessage()), e);
            fail("Не удалось получить список пользователей: " + e.getMessage());
        }
    }

    /**
     * Тестирование обновления пользователя.
     * Тип: Позитивный
     */
    @Test
    public void testUpdateUser() {
        String methodName = "testUpdateUser";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            log.info("testUpdateUser [1] Создание пользователя");
            userDAO.create(user);
            Integer id = user.getId();

            user.setName("Обновленный пользователь");
            user.setEmail("updated@example.com");
            log.info("testUpdateUser [2] Обновление пользователя");
            userDAO.update(user);

            log.info("testUpdateUser [3] Проверка обновленного пользователя");
            Optional<User> updated = userDAO.findById(id);
            assertTrue("Пользователь должен быть найден", updated.isPresent());
            assertEquals("Имя должно быть обновлено", "Обновленный пользователь", updated.get().getName());
            assertEquals("Email должен быть обновлен", "updated@example.com", updated.get().getEmail());
            log.info("testUpdateUser [4] Пользователь успешно обновлен");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить пользователя: " + e.getMessage()), e);
            fail("Не удалось обновить пользователя: " + e.getMessage());
        }
    }

    /**
     * Тестирование удаления пользователя.
     * Тип: Позитивный
     */
    @Test
    public void testDeleteUser() {
        String methodName = "testDeleteUser";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            log.info("testDeleteUser [1] Создание пользователя");
            userDAO.create(user);
            Integer id = user.getId();

            log.info("testDeleteUser [2] Удаление пользователя с ID: " + id);
            userDAO.delete(id);

            log.info("testDeleteUser [3] Проверка удаления");
            Optional<User> deleted = userDAO.findById(id);
            assertFalse("Пользователь не должен быть найден", deleted.isPresent());
            log.info("testDeleteUser [4] Пользователь успешно удален");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить пользователя: " + e.getMessage()), e);
            fail("Не удалось удалить пользователя: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска пользователя по email.
     * Тип: Позитивный
     */
    @Test
    public void testFindByEmail() {
        String methodName = "testFindByEmail";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            log.info("testFindByEmail [1] Создание пользователя");
            userDAO.create(user);

            log.info("testFindByEmail [2] Поиск пользователя по email: test@example.com");
            Optional<User> found = userDAO.findByEmail("test@example.com");
            assertTrue("Пользователь должен быть найден", found.isPresent());
            assertEquals("Имя пользователя должно совпадать", "Тестовый пользователь", found.get().getName());
            log.info("testFindByEmail [3] Пользователь успешно найден");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти пользователя: " + e.getMessage()), e);
            fail("Не удалось найти пользователя: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска пользователя по несуществующему email.
     * Тип: Негативный
     */
    @Test
    public void testFindByEmailNotFound() {
        String methodName = "testFindByEmailNotFound";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testFindByEmailNotFound [1] Поиск пользователя по email: nonexistent@example.com");
            Optional<User> found = userDAO.findByEmail("nonexistent@example.com");
            assertFalse("Пользователь не должен быть найден", found.isPresent());
            log.info("testFindByEmailNotFound [2] Пользователь не найден, как ожидалось");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить поиск: " + e.getMessage()), e);
            fail("Не удалось выполнить поиск: " + e.getMessage());
        }
    }
}
