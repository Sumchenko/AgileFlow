package ru.sfedu.agileflow.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.dao.ProjectDAO;
import ru.sfedu.agileflow.dao.SprintDAO;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.Sprint;
import ru.sfedu.agileflow.constants.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Тестовый класс для SprintDAO.
 */
public class SprintDAOTest {
    private SprintDAO sprintDAO;
    private ProjectDAO projectDAO;

    @Before
    public void setUp() {
        sprintDAO = new SprintDAO();
        projectDAO = new ProjectDAO();
        clearSprintsTable();
        clearProjectsTable();
    }

    @After
    public void tearDown() {
        clearSprintsTable();
        clearProjectsTable();
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
     * Тест создания спринта
     * Тип: позитивный
     */
    @Test
    public void testCreateSprintPositive() {
        Project project = new Project(Constants.TEST_PROJECT_NAME, "Test Description");
        projectDAO.create(project);
        Sprint sprint = new Sprint(new Date(), new Date(), project);
        sprintDAO.create(sprint);
        assertNotNull(sprint.getId());
        Sprint foundSprint = sprintDAO.findById(sprint.getId());
        assertNotNull(foundSprint);
        assertEquals(project.getId(), foundSprint.getProject().getId());
    }

    /**
     * Тест создания спринта с несуществующим проектом
     * Тип: негативный
     */
    @Test(expected = RuntimeException.class)
    public void testCreateSprintNegative() {
        Project project = new Project();
        project.setId(9999); // Несуществующий проект
        Sprint sprint = new Sprint(new Date(), new Date(), project);
        sprintDAO.create(sprint); // Должно выбросить исключение из-за внешнего ключа
    }

    /**
     * Тест поиска спринта по ID
     * Тип: позитивный
     */
    @Test
    public void testFindByIdPositive() {
        Project project = new Project(Constants.TEST_PROJECT_NAME, "Test Description");
        projectDAO.create(project);
        Sprint sprint = new Sprint(new Date(), new Date(), project);
        sprintDAO.create(sprint);
        Sprint foundSprint = sprintDAO.findById(sprint.getId());
        assertNotNull(foundSprint);
        assertEquals(sprint.getId(), foundSprint.getId());
    }

    /**
     * Тест поиска несуществующего спринта
     * Тип: негативный
     */
    @Test
    public void testFindByIdNegative() {
        Sprint foundSprint = sprintDAO.findById(9999);
        assertNull(foundSprint);
    }
}