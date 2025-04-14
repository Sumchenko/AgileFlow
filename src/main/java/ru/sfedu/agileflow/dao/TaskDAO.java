package ru.sfedu.agileflow.dao;

import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Sprint;
import ru.sfedu.agileflow.models.Task;
import ru.sfedu.agileflow.models.TaskStatus;
import ru.sfedu.agileflow.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO-класс для управления задачами в базе данных.
 */
public class TaskDAO implements GenericDAO<Task, Integer> {
    private static final Logger log = Logger.getLogger(TaskDAO.class);
    private final SprintDAO sprintDAO = new SprintDAO();
    private final UserDAO userDAO = new UserDAO();

    /**
     * Создает новую задачу в базе данных.
     * @param task Задача для сохранения
     */
    @Override
    public void create(Task task) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, task.toString()));

        String sql = "INSERT INTO tasks (title, description, status, priority, sprint_id, assigned_user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus().name());
            stmt.setInt(4, task.getPriority());
            stmt.setInt(5, task.getSprint().getId());
            stmt.setObject(6, task.getAssignedUser() != null ? task.getAssignedUser().getId() : null);

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Rows affected: " + rowsAffected));

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    task.setId(rs.getInt(1));
                }
            }
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to create task", e);
        }
    }

    /**
     * Находит задачу по идентификатору.
     * @param id Идентификатор задачи
     * @return Задача или null, если не найдена
     */
    @Override
    public Task findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        String sql = "SELECT * FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            try (ResultSet rs = stmt.executeQuery()) {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Query executed"));
                if (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setStatus(TaskStatus.valueOf(rs.getString("status")));
                    task.setPriority(rs.getInt("priority"));
                    Sprint sprint = sprintDAO.findById(rs.getInt("sprint_id"));
                    task.setSprint(sprint);
                    int userId = rs.getInt("assigned_user_id");
                    if (!rs.wasNull()) {
                        User user = userDAO.findById(userId);
                        task.setAssignedUser(user);
                    }
                    log.info(String.format(Constants.LOG_METHOD_END, methodName));
                    return task;
                }
            }
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return null;
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to find task by id", e);
        }
    }

    /**
     * Возвращает список всех задач.
     * @return Список задач
     */
    @Override
    public List<Task> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(TaskStatus.valueOf(rs.getString("status")));
                task.setPriority(rs.getInt("priority"));
                Sprint sprint = sprintDAO.findById(rs.getInt("sprint_id"));
                task.setSprint(sprint);
                int userId = rs.getInt("assigned_user_id");
                if (!rs.wasNull()) {
                    User user = userDAO.findById(userId);
                    task.setAssignedUser(user);
                }
                tasks.add(task);
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Found " + tasks.size() + " tasks"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return tasks;
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to retrieve all tasks", e);
        }
    }

    /**
     * Обновляет данные задачи.
     * @param task Обновленная задача
     */
    @Override
    public void update(Task task) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, task.toString()));

        String sql = "UPDATE tasks SET title = ?, description = ?, status = ?, priority = ?, sprint_id = ?, assigned_user_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus().name());
            stmt.setInt(4, task.getPriority());
            stmt.setInt(5, task.getSprint().getId());
            stmt.setObject(6, task.getAssignedUser() != null ? task.getAssignedUser().getId() : null);
            stmt.setInt(7, task.getId());

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Rows affected: " + rowsAffected));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to update task", e);
        }
    }

    /**
     * Удаляет задачу по идентификатору.
     * @param id Идентификатор задачи
     */
    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Rows affected: " + rowsAffected));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to delete task", e);
        }
    }
}
