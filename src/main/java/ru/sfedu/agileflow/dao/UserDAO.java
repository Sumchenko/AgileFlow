package ru.sfedu.agileflow.dao;

import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DAO-класс для управления пользователями в базе данных.
 */
public class UserDAO implements GenericDAO<User, Integer> {
    private static final Logger log = Logger.getLogger(UserDAO.class);

    /**
     * Создает нового пользователя в базе данных.
     * @param user Пользователь для сохранения
     */
    @Override
    public void create(User user) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, user.toString()));

        String sql = "INSERT INTO users (name, email, bio, is_active, last_login, date_joined) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getBio());
            stmt.setBoolean(4, user.isActive());
            stmt.setTimestamp(5, user.getLastLogin() != null ? new Timestamp(user.getLastLogin().getTime()) : null);
            stmt.setTimestamp(6, new Timestamp(user.getDateJoined().getTime()));

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Затронуто строк: " + rowsAffected));

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Не удалось создать пользователя", e);
        }
    }

    /**
     * Находит пользователя по идентификатору.
     * @param id Идентификатор пользователя
     * @return Пользователь или null, если не найден
     */
    @Override
    public User findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            try (ResultSet rs = stmt.executeQuery()) {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Запрос выполнен"));
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setBio(rs.getString("bio"));
                    user.setActive(rs.getBoolean("is_active"));
                    user.setLastLogin(rs.getTimestamp("last_login"));
                    user.setDateJoined(rs.getTimestamp("date_joined"));
                    log.info(String.format(Constants.LOG_METHOD_END, methodName));
                    return user;
                }
            }
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return null;
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Не удалось найти пользователя по идентификатору", e);
        }
    }

    /**
     * Находит пользователя по email.
     * @param email Email пользователя
     * @return Пользователь или null, если не найден
     */
    public User findByEmail(String email) {
        String methodName = "findByEmail";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "email: " + email));

        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            try (ResultSet rs = stmt.executeQuery()) {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Запрос выполнен"));
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setBio(rs.getString("bio"));
                    user.setActive(rs.getBoolean("is_active"));
                    user.setLastLogin(rs.getTimestamp("last_login"));
                    user.setDateJoined(rs.getTimestamp("date_joined"));
                    log.info(String.format(Constants.LOG_METHOD_END, methodName));
                    return user;
                }
            }
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return null;
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Не удалось найти пользователя по email", e);
        }
    }

    /**
     * Возвращает список всех пользователей.
     * @return Список пользователей
     */
    @Override
    public List<User> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setBio(rs.getString("bio"));
                user.setActive(rs.getBoolean("is_active"));
                user.setLastLogin(rs.getTimestamp("last_login"));
                user.setDateJoined(rs.getTimestamp("date_joined"));
                users.add(user);
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено пользователей: " + users.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return users;
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Не удалось получить список всех пользователей", e);
        }
    }

    /**
     * Обновляет данные пользователя.
     * @param user Обновленный пользователь
     */
    @Override
    public void update(User user) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, user.toString()));

        String sql = "UPDATE users SET name = ?, email = ?, bio = ?, is_active = ?, last_login = ?, date_joined = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getBio());
            stmt.setBoolean(4, user.isActive());
            stmt.setTimestamp(5, user.getLastLogin() != null ? new Timestamp(user.getLastLogin().getTime()) : null);
            stmt.setTimestamp(6, new Timestamp(user.getDateJoined().getTime()));
            stmt.setInt(7, user.getId());

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Затронуто строк: " + rowsAffected));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Не удалось обновить пользователя", e);
        }
    }

    /**
     * Удаляет пользователя по идентификатору.
     * @param id Идентификатор пользователя
     */
    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Затронуто строк: " + rowsAffected));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Не удалось удалить пользователя", e);
        }
    }
}