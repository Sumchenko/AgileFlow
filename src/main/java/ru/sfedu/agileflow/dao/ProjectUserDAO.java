package ru.sfedu.agileflow.dao;

import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO-класс для управления связью пользователей и проектов в базе данных.
 */
public class ProjectUserDAO {
    private static final Logger log = Logger.getLogger(ProjectUserDAO.class);
    private final UserDAO userDAO = new UserDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();

    /**
     * Добавляет пользователя в проект.
     * @param projectId Идентификатор проекта
     * @param userId Идентификатор пользователя
     */
    public void addUserToProject(int projectId, int userId) {
        String methodName = "addUserToProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId + ", userId: " + userId));

        String sql = "INSERT INTO project_users (project_id, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, projectId);
            stmt.setInt(2, userId);

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Затронуто строк: " + rowsAffected));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Не удалось добавить пользователя в проект", e);
        }
    }

    /**
     * Удаляет пользователя из проекта.
     * @param projectId Идентификатор проекта
     * @param userId Идентификатор пользователя
     */
    public void removeUserFromProject(int projectId, int userId) {
        String methodName = "removeUserFromProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId + ", userId: " + userId));

        String sql = "DELETE FROM project_users WHERE project_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, projectId);
            stmt.setInt(2, userId);

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Затронуто строк: " + rowsAffected));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Не удалось удалить пользователя из проекта", e);
        }
    }

    /**
     * Возвращает список пользователей, связанных с проектом.
     * @param projectId Идентификатор проекта
     * @return Список пользователей
     */
    public List<User> getUsersByProject(int projectId) {
        String methodName = "getUsersByProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId));

        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id FROM project_users WHERE project_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, projectId);

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    User user = userDAO.findById(userId);
                    if (user != null) {
                        users.add(user);
                    }
                }
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено пользователей: " + users.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return users;
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Не удалось получить список пользователей проекта", e);
        }
    }

    /**
     * Возвращает список проектов, в которых участвует пользователь.
     * @param userId Идентификатор пользователя
     * @return Список проектов
     */
    public List<Project> getProjectsByUser(int userId) {
        String methodName = "getProjectsByUser";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "userId: " + userId));

        List<Project> projects = new ArrayList<>();
        String sql = "SELECT project_id FROM project_users WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int projectId = rs.getInt("project_id");
                    Project project = projectDAO.findById(projectId);
                    if (project != null) {
                        projects.add(project);
                    }
                }
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено проектов: " + projects.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return projects;
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Не удалось получить список проектов пользователя", e);
        }
    }
}