package ru.sfedu.agileflow.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.dao.ProjectDAO;
import ru.sfedu.agileflow.dao.RetrospectiveDAO;
import ru.sfedu.agileflow.dao.SprintDAO;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.Retrospective;
import ru.sfedu.agileflow.models.Sprint;
import ru.sfedu.agileflow.constants.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Тестовый класс для RetrospectiveDAO.
 */
public class RetrospectiveDAOTest {
    private RetrospectiveDAO retrospectiveDAO;
    private SprintDAO sprintDAO;
    private ProjectDAO projectDAO;

    @Before
    public void setUp() {
        retrospectiveDAO = new RetrospectiveDAO();
        sprintDAO = new SprintDAO();
        projectDAO = new ProjectDAO();
        clearRetrospectivesTable();
        clearSprintsTable();
        clearProjectsTable();
    }

    @After
    public void tearDown() {
        clearRetrospectivesTable();
        clearSprintsTable();
        clearProjectsTable();
    }

    /**
     * Очищает таблицу retrospectives в базе данных.
     */
    private void clearRetrospectivesTable() {
        String sql1 = "DELETE FROM retrospective_improvements";
        String sql2 = "DELETE FROM retrospective_positives";
        String sql3 = "DELETE FROM retrospectives";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(sql1);
             PreparedStatement stmt2 = conn.prepareStatement(sql2);
             PreparedStatement stmt3 = conn.prepareStatement(sql3)) {
            stmt1.executeUpdate();
            stmt2.executeUpdate();
            stmt3.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear retrospectives table", e);
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
     * Тест создания ретроспективы
     * Тип: позитивный
     */
    @Test
    public void testCreateRetrospectivePositive() {
        Project project = new Project(Constants.TEST_PROJECT_NAME, "Test Description");
        projectDAO.create(project);
        Sprint sprint = new Sprint(new Date(), new Date(), project);
        sprintDAO.create(sprint);
        Retrospective retrospective = new Retrospective(sprint, Constants.TEST_SPRINT_SUMMARY,
                Arrays.asList("Improvement 1"), Arrays.asList("Positive 1"));
        retrospectiveDAO.create(retrospective);
        assertNotNull(retrospective.getId());
        Retrospective foundRetrospective = retrospectiveDAO.findById(retrospective.getId());
        assertNotNull(foundRetrospective);
        assertEquals(Constants.TEST_SPRINT_SUMMARY, foundRetrospective.getSummary());
    }

    /**
     * Тест создания ретроспективы с несуществующим спринтом
     * Тип: негативный
     */
    @Test(expected = RuntimeException.class)
    public void testCreateRetrospectiveNegative() {
        Sprint sprint = new Sprint();
        sprint.setId(9999); // Несуществующий спринт
        Retrospective retrospective = new Retrospective(sprint, Constants.TEST_SPRINT_SUMMARY,
                Arrays.asList("Improvement 1"), Arrays.asList("Positive 1"));
        retrospectiveDAO.create(retrospective); // Должно выбросить исключение из-за внешнего ключа
    }

    /**
     * Тест поиска ретроспективы по ID
     * Тип: позитивный
     */
    @Test
    public void testFindByIdPositive() {
        Project project = new Project(Constants.TEST_PROJECT_NAME, "Test Description");
        projectDAO.create(project);
        Sprint sprint = new Sprint(new Date(), new Date(), project);
        sprintDAO.create(sprint);
        Retrospective retrospective = new Retrospective(sprint, Constants.TEST_SPRINT_SUMMARY,
                Arrays.asList("Improvement 1"), Arrays.asList("Positive 1"));
        retrospectiveDAO.create(retrospective);
        Retrospective foundRetrospective = retrospectiveDAO.findById(retrospective.getId());
        assertNotNull(foundRetrospective);
        assertEquals(retrospective.getId(), foundRetrospective.getId());
    }

    /**
     * Тест поиска несуществующей ретроспективы
     * Тип: негативный
     */
    @Test
    public void testFindByIdNegative() {
        Retrospective foundRetrospective = retrospectiveDAO.findById(9999);
        assertNull(foundRetrospective);
    }
}