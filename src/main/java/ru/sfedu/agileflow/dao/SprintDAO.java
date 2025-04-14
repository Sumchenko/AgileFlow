package ru.sfedu.agileflow.dao;

import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.Sprint;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO-класс для управления спринтами в базе данных.
 */
public class SprintDAO implements GenericDAO<Sprint, Integer> {
    private static final Logger log = Logger.getLogger(SprintDAO.class);
    private final ProjectDAO projectDAO = new ProjectDAO();

    /**
     * Создает новый спринт в базе данных.
     * @param sprint Спринт для сохранения
     */
    @Override
    public void create(Sprint sprint) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, sprint.toString()));

        String sql = "INSERT INTO sprints (start_date, end_date, project_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, new java.sql.Date(sprint.getStartDate().getTime()));
            stmt.setDate(2, new java.sql.Date(sprint.getEndDate().getTime()));
            stmt.setInt(3, sprint.getProject().getId());

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Rows affected: " + rowsAffected));

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    sprint.setId(rs.getInt(1));
                }
            }
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to create sprint", e);
        }
    }

    /**
     * Находит спринт по идентификатору.
     * @param id Идентификатор спринта
     * @return Спринт или null, если не найден
     */
    @Override
    public Sprint findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        String sql = "SELECT * FROM sprints WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            try (ResultSet rs = stmt.executeQuery()) {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Query executed"));
                if (rs.next()) {
                    Sprint sprint = new Sprint();
                    sprint.setId(rs.getInt("id"));
                    sprint.setStartDate(rs.getDate("start_date"));
                    sprint.setEndDate(rs.getDate("end_date"));
                    Project project = projectDAO.findById(rs.getInt("project_id"));
                    sprint.setProject(project);
                    log.info(String.format(Constants.LOG_METHOD_END, methodName));
                    return sprint;
                }
            }
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return null;
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to find sprint by id", e);
        }
    }

    /**
     * Возвращает список всех спринтов.
     * @return Список спринтов
     */
    @Override
    public List<Sprint> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        List<Sprint> sprints = new ArrayList<>();
        String sql = "SELECT * FROM sprints";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            while (rs.next()) {
                Sprint sprint = new Sprint();
                sprint.setId(rs.getInt("id"));
                sprint.setStartDate(rs.getDate("start_date"));
                sprint.setEndDate(rs.getDate("end_date"));
                Project project = projectDAO.findById(rs.getInt("project_id"));
                sprint.setProject(project);
                sprints.add(sprint);
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Found " + sprints.size() + " sprints"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return sprints;
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to retrieve all sprints", e);
        }
    }

    /**
     * Обновляет данные спринта.
     * @param sprint Обновленный спринт
     */
    @Override
    public void update(Sprint sprint) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, sprint.toString()));

        String sql = "UPDATE sprints SET start_date = ?, end_date = ?, project_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(sprint.getStartDate().getTime()));
            stmt.setDate(2, new java.sql.Date(sprint.getEndDate().getTime()));
            stmt.setInt(3, sprint.getProject().getId());
            stmt.setInt(4, sprint.getId());

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Rows affected: " + rowsAffected));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to update sprint", e);
        }
    }

    /**
     * Удаляет спринт по идентификатору.
     * @param id Идентификатор спринта
     */
    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        String sql = "DELETE FROM sprints WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Rows affected: " + rowsAffected));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to delete sprint", e);
        }
    }
}