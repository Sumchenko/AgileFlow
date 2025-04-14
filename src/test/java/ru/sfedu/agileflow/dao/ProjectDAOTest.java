package ru.sfedu.agileflow.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.dao.ProjectDAO;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.constants.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Тестовый класс для ProjectDAO.
 */
public class ProjectDAOTest {
    private ProjectDAO projectDAO;

    @Before
    public void setUp() {
        projectDAO = new ProjectDAO();
        clearProjectsTable();
    }

    @After
    public void tearDown() {
        clearProjectsTable();
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
     * Тест создания проекта
     * Тип: позитивный
     */
    @Test
    public void testCreateProjectPositive() {
        Project project = new Project(Constants.TEST_PROJECT_NAME, "Test Description");
        projectDAO.create(project);
        assertNotNull(project.getId());
        Project foundProject = projectDAO.findById(project.getId());
        assertNotNull(foundProject);
        assertEquals(Constants.TEST_PROJECT_NAME, foundProject.getName());
    }

    /**
     * Тест создания проекта с null названием
     * Тип: негативный
     */
    @Test(expected = RuntimeException.class)
    public void testCreateProjectNegative() {
        Project project = new Project(null, "Test Description");
        projectDAO.create(project); // Должно выбросить исключение из-за NOT NULL ограничения
    }

    /**
     * Тест поиска проекта по ID
     * Тип: позитивный
     */
    @Test
    public void testFindByIdPositive() {
        Project project = new Project(Constants.TEST_PROJECT_NAME, "Test Description");
        projectDAO.create(project);
        Project foundProject = projectDAO.findById(project.getId());
        assertNotNull(foundProject);
        assertEquals(project.getId(), foundProject.getId());
    }

    /**
     * Тест поиска несуществующего проекта
     * Тип: негативный
     */
    @Test
    public void testFindByIdNegative() {
        Project foundProject = projectDAO.findById(9999);
        assertNull(foundProject);
    }
}