package ru.sfedu.agileflow.dao;

import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO-класс для управления проектами в базе данных.
 */
public class ProjectDAO implements GenericDAO<Project, Integer> {
    private static final Logger log = Logger.getLogger(ProjectDAO.class);

    /**
     * Создает новый проект в базе данных.
     * @param project Проект для сохранения
     */
    @Override
    public void create(Project project) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, project.toString()));

        String sql = "INSERT INTO projects (name, description) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Rows affected: " + rowsAffected));

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    project.setId(rs.getInt(1));
                }
            }
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to create project", e);
        }
    }

    /**
     * Находит проект по идентификатору.
     * @param id Идентификатор проекта
     * @return Проект или null, если не найден
     */
    @Override
    public Project findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        String sql = "SELECT * FROM projects WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            try (ResultSet rs = stmt.executeQuery()) {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Query executed"));
                if (rs.next()) {
                    Project project = new Project();
                    project.setId(rs.getInt("id"));
                    project.setName(rs.getString("name"));
                    project.setDescription(rs.getString("description"));
                    log.info(String.format(Constants.LOG_METHOD_END, methodName));
                    return project;
                }
            }
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return null;
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to find project by id", e);
        }
    }

    /**
     * Возвращает список всех проектов.
     * @return Список проектов
     */
    @Override
    public List<Project> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            while (rs.next()) {
                Project project = new Project();
                project.setId(rs.getInt("id"));
                project.setName(rs.getString("name"));
                project.setDescription(rs.getString("description"));
                projects.add(project);
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Found " + projects.size() + " projects"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return projects;
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to retrieve all projects", e);
        }
    }

    /**
     * Обновляет данные проекта.
     * @param project Обновленный проект
     */
    @Override
    public void update(Project project) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, project.toString()));

        String sql = "UPDATE projects SET name = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setInt(3, project.getId());

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Rows affected: " + rowsAffected));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to update project", e);
        }
    }

    /**
     * Удаляет проект по идентификатору.
     * @param id Идентификатор проекта
     */
    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        String sql = "DELETE FROM projects WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Rows affected: " + rowsAffected));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to delete project", e);
        }
    }
}
