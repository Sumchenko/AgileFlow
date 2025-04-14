package ru.sfedu.agileflow.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.dao.ProjectDAO;
import ru.sfedu.agileflow.dao.SprintDAO;
import ru.sfedu.agileflow.dao.TaskDAO;
import ru.sfedu.agileflow.dao.UserDAO;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.Sprint;
import ru.sfedu.agileflow.models.Task;
import ru.sfedu.agileflow.models.TaskStatus;
import ru.sfedu.agileflow.models.User;
import ru.sfedu.agileflow.constants.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Тестовый класс для TaskDAO.
 */
public class TaskDAOTest {
    private TaskDAO taskDAO;
    private SprintDAO sprintDAO;
    private ProjectDAO projectDAO;
    private UserDAO userDAO;

    @Before
    public void setUp() {
        taskDAO = new TaskDAO();
        sprintDAO = new SprintDAO();
        projectDAO = new ProjectDAO();
        userDAO = new UserDAO();
        clearTasksTable();
        clearSprintsTable();
        clearProjectsTable();
        clearUsersTable();
    }

    @After
    public void tearDown() {
        clearTasksTable();
        clearSprintsTable();
        clearProjectsTable();
        clearUsersTable();
    }

    /**
     * Очищает таблицу tasks в базе данных.
     */
    private void clearTasksTable() {
        String sql = "DELETE FROM tasks";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear tasks table", e);
        }
    }

    /**
     * Очищает таблицу sprints в базе данных.
     */
    private void clearSprintsTable() {
        String sql = "DELETE FROM sprints";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear sprints table", e);
        }
    }

    /**
     * Очищает таблицу projects в базе данных.
     */
    private void clearProjectsTable() {
        String sql = "DELETE FROM projects";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear projects table", e);
        }
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
     * Тест создания задачи
     * Тип: позитивный
     */
    @Test
    public void testCreateTaskPositive() {
        Project project = new Project(Constants.TEST_PROJECT_NAME, "Test Description");
        projectDAO.create(project);
        Sprint sprint = new Sprint(new Date(), new Date(), project);
        sprintDAO.create(sprint);
        User user = new User(Constants.TEST_USER_NAME, generateUniqueEmail(), "", true, new Date());
        userDAO.create(user);
        Task task = new Task("Test Task", "Test Description", TaskStatus.TODO, 1, sprint, user);
        taskDAO.create(task);
        assertNotNull(task.getId());
        Task foundTask = taskDAO.findById(task.getId());
        assertNotNull(foundTask);
        assertEquals("Test Task", foundTask.getTitle());
    }

    /**
     * Тест создания задачи с несуществующим спринтом
     * Тип: негативный
     */
    @Test(expected = RuntimeException.class)
    public void testCreateTaskNegative() {
        Sprint sprint = new Sprint();
        sprint.setId(9999); // Несуществующий спринт
        Task task = new Task("Test Task", "Test Description", TaskStatus.TODO, 1, sprint, null);
        taskDAO.create(task); // Должно выбросить исключение из-за внешнего ключа
    }

    /**
     * Тест поиска задачи по ID
     * Тип: позитивный
     */
    @Test
    public void testFindByIdPositive() {
        Project project = new Project(Constants.TEST_PROJECT_NAME, "Test Description");
        projectDAO.create(project);
        Sprint sprint = new Sprint(new Date(), new Date(), project);
        sprintDAO.create(sprint);
        Task task = new Task("Test Task", "Test Description", TaskStatus.TODO, 1, sprint, null);
        taskDAO.create(task);
        Task foundTask = taskDAO.findById(task.getId());
        assertNotNull(foundTask);
        assertEquals(task.getId(), foundTask.getId());
    }

    /**
     * Тест поиска несуществующей задачи
     * Тип: негативный
     */
    @Test
    public void testFindByIdNegative() {
        Task foundTask = taskDAO.findById(9999);
        assertNull(foundTask);
    }
}