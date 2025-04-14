package ru.sfedu.agileflow.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.dao.UserDAO;
import ru.sfedu.agileflow.models.User;
import ru.sfedu.agileflow.constants.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Тестовый класс для UserDAO.
 */
public class UserDAOTest {
    private UserDAO userDAO;

    @Before
    public void setUp() {
        userDAO = new UserDAO();
        // Очистка таблицы users перед каждым тестом
        clearUsersTable();
    }

    @After
    public void tearDown() {
        // Очистка таблицы users после каждого теста
        clearUsersTable();
    }

    /**
     * Очищает таблицу users в базе данных.
     */
    private void clearUsersTable() {
        String sql = "DELETE FROM users";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear users table", e);
        }
    }

    /**
     * Генерирует уникальный email для тестов.
     * @return Уникальный email
     */
    private String generateUniqueEmail() {
        return "test" + System.currentTimeMillis() + "@example.com";
    }

    /**
     * Тест создания пользователя
     * Тип: позитивный
     */
    @Test
    public void testCreateUserPositive() {
        String uniqueEmail = generateUniqueEmail();
        User user = new User(Constants.TEST_USER_NAME, uniqueEmail, "", true, new Date());
        userDAO.create(user);
        assertNotNull(user.getId());
        User foundUser = userDAO.findById(user.getId());
        assertNotNull(foundUser);
        assertEquals(Constants.TEST_USER_NAME, foundUser.getName());
        assertEquals(uniqueEmail, foundUser.getEmail());
    }

    /**
     * Тест создания пользователя с дублирующимся email
     * Тип: негативный
     */
    @Test(expected = RuntimeException.class)
    public void testCreateUserNegative() {
        String uniqueEmail = generateUniqueEmail();
        User user1 = new User(Constants.TEST_USER_NAME, uniqueEmail, "", true, new Date());
        userDAO.create(user1);
        User user2 = new User("Another User", uniqueEmail, "", true, new Date());
        userDAO.create(user2); // Должно выбросить исключение из-за уникального email
    }

    /**
     * Тест поиска пользователя по ID
     * Тип: позитивный
     */
    @Test
    public void testFindByIdPositive() {
        String uniqueEmail = generateUniqueEmail();
        User user = new User(Constants.TEST_USER_NAME, uniqueEmail, "", true, new Date());
        userDAO.create(user);
        User foundUser = userDAO.findById(user.getId());
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
    }

    /**
     * Тест поиска несуществующего пользователя
     * Тип: негативный
     */
    @Test
    public void testFindByIdNegative() {
        User foundUser = userDAO.findById(9999);
        assertNull(foundUser);
    }
}