package ru.sfedu.agileflow.csv;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.User;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Тестовый класс для UserCsvDAO.
 */
public class UserCsvDAOTest {
    private static final Logger log = Logger.getLogger(UserCsvDAOTest.class);
    private UserCsvDAO userDAO;
    private static final String CSV_DIR = "src/main/resources/dataCSV";

    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        userDAO = new UserCsvDAO();
        try {
            Files.deleteIfExists(Paths.get(CSV_DIR, "users.csv"));
            log.info("setUp [1] CSV-файл очищен");
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось очистить CSV: " + e.getMessage()), e);
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    @After
    public void tearDown() {
        String methodName = "tearDown";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Files.deleteIfExists(Paths.get(CSV_DIR, "users.csv"));
            log.info("tearDown [1] CSV-файл удален");
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить CSV: " + e.getMessage()), e);
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    @Test
    public void testCreateUser() {
        String methodName = "testCreateUser";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user = new User(Constants.TEST_USER_NAME, Constants.TEST_USER_EMAIL, "Био", true, new Date());
            userDAO.create(user);
            assertNotNull("Идентификатор должен быть установлен", user.getId());
            log.info("testCreateUser [1] Пользователь создан с ID: " + user.getId());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать пользователя: " + e.getMessage()), e);
            fail("Не удалось создать пользователя: " + e.getMessage());
        }
    }

    @Test
    public void testFindById() {
        String methodName = "testFindById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user = new User(Constants.TEST_USER_NAME, Constants.TEST_USER_EMAIL, "Био", true, new Date());
            userDAO.create(user);
            Optional<User> found = userDAO.findById(user.getId());
            assertTrue("Пользователь должен быть найден", found.isPresent());
            assertEquals("Email должен совпадать", Constants.TEST_USER_EMAIL, found.get().getEmail());
            log.info("testFindById [1] Пользователь найден");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти пользователя: " + e.getMessage()), e);
            fail("Не удалось найти пользователя: " + e.getMessage());
        }
    }

    @Test
    public void testFindByEmail() {
        String methodName = "testFindByEmail";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user = new User(Constants.TEST_USER_NAME, Constants.TEST_USER_EMAIL, "Био", true, new Date());
            userDAO.create(user);
            Optional<User> found = userDAO.findByEmail(Constants.TEST_USER_EMAIL);
            assertTrue("Пользователь должен быть найден по email", found.isPresent());
            assertEquals("Имя должно совпадать", Constants.TEST_USER_NAME, found.get().getName());
            log.info("testFindByEmail [1] Пользователь найден по email");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти пользователя по email: " + e.getMessage()), e);
            fail("Не удалось найти пользователя по email: " + e.getMessage());
        }
    }

    @Test
    public void testFindAll() {
        String methodName = "testFindAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            userDAO.create(new User("Пользователь 1", "user1@example.com", "Био1", true, new Date()));
            userDAO.create(new User("Пользователь 2", "user2@example.com", "Био2", true, new Date()));
            List<User> users = userDAO.findAll();
            assertEquals("Должно быть 2 пользователя", 2, users.size());
            log.info("testFindAll [1] Найдено пользователей: " + users.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить пользователей: " + e.getMessage()), e);
            fail("Не удалось получить пользователей: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateUser() {
        String methodName = "testUpdateUser";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user = new User(Constants.TEST_USER_NAME, Constants.TEST_USER_EMAIL, "Био", true, new Date());
            userDAO.create(user);
            user.setName("Обновленный пользователь");
            userDAO.update(user);
            Optional<User> updated = userDAO.findById(user.getId());
            assertTrue("Пользователь должен быть найден", updated.isPresent());
            assertEquals("Имя должно быть обновлено", "Обновленный пользователь", updated.get().getName());
            log.info("testUpdateUser [1] Пользователь обновлен");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить пользователя: " + e.getMessage()), e);
            fail("Не удалось обновить пользователя: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteUser() {
        String methodName = "testDeleteUser";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user = new User(Constants.TEST_USER_NAME, Constants.TEST_USER_EMAIL, "Био", true, new Date());
            userDAO.create(user);
            userDAO.delete(user.getId());
            Optional<User> deleted = userDAO.findById(user.getId());
            assertFalse("Пользователь не должен быть найден", deleted.isPresent());
            log.info("testDeleteUser [1] Пользователь удален");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить пользователя: " + e.getMessage()), e);
            fail("Не удалось удалить пользователя: " + e.getMessage());
        }
    }
}