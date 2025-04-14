package ru.sfedu.agileflow.dao;

import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Retrospective;
import ru.sfedu.agileflow.models.Sprint;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO-класс для управления ретроспективами в базе данных.
 */
public class RetrospectiveDAO implements GenericDAO<Retrospective, Integer> {
    private static final Logger log = Logger.getLogger(RetrospectiveDAO.class);
    private final SprintDAO sprintDAO = new SprintDAO();

    /**
     * Создает новую ретроспективу в базе данных.
     * @param retrospective Ретроспектива для сохранения
     */
    @Override
    public void create(Retrospective retrospective) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, retrospective.toString()));

        String sql = "INSERT INTO retrospectives (sprint_id, summary) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, retrospective.getSprint().getId());
            stmt.setString(2, retrospective.getSummary());

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Rows affected: " + rowsAffected));

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    retrospective.setId(rs.getInt(1));
                }
            }

            // Сохранение improvements
            String sqlImprovements = "INSERT INTO retrospective_improvements (retrospective_id, improvement) VALUES (?, ?)";
            try (PreparedStatement stmtImprovements = conn.prepareStatement(sqlImprovements)) {
                for (String improvement : retrospective.getImprovements()) {
                    stmtImprovements.setInt(1, retrospective.getId());
                    stmtImprovements.setString(2, improvement);
                    stmtImprovements.executeUpdate();
                }
            }

            // Сохранение positives
            String sqlPositives = "INSERT INTO retrospective_positives (retrospective_id, positive) VALUES (?, ?)";
            try (PreparedStatement stmtPositives = conn.prepareStatement(sqlPositives)) {
                for (String positive : retrospective.getPositives()) {
                    stmtPositives.setInt(1, retrospective.getId());
                    stmtPositives.setString(2, positive);
                    stmtPositives.executeUpdate();
                }
            }

            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to create retrospective", e);
        }
    }

    /**
     * Находит ретроспективу по идентификатору.
     * @param id Идентификатор ретроспективы
     * @return Ретроспектива или null, если не найдена
     */
    @Override
    public Retrospective findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        String sql = "SELECT * FROM retrospectives WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            try (ResultSet rs = stmt.executeQuery()) {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Query executed"));
                if (rs.next()) {
                    Retrospective retrospective = new Retrospective();
                    retrospective.setId(rs.getInt("id"));
                    Sprint sprint = sprintDAO.findById(rs.getInt("sprint_id"));
                    retrospective.setSprint(sprint);
                    retrospective.setSummary(rs.getString("summary"));

                    // Загрузка improvements
                    String sqlImprovements = "SELECT improvement FROM retrospective_improvements WHERE retrospective_id = ?";
                    try (PreparedStatement stmtImprovements = conn.prepareStatement(sqlImprovements)) {
                        stmtImprovements.setInt(1, id);
                        try (ResultSet rsImprovements = stmtImprovements.executeQuery()) {
                            List<String> improvements = new ArrayList<>();
                            while (rsImprovements.next()) {
                                improvements.add(rsImprovements.getString("improvement"));
                            }
                            retrospective.setImprovements(improvements);
                        }
                    }

                    // Загрузка positives
                    String sqlPositives = "SELECT positive FROM retrospective_positives WHERE retrospective_id = ?";
                    try (PreparedStatement stmtPositives = conn.prepareStatement(sqlPositives)) {
                        stmtPositives.setInt(1, id);
                        try (ResultSet rsPositives = stmtPositives.executeQuery()) {
                            List<String> positives = new ArrayList<>();
                            while (rsPositives.next()) {
                                positives.add(rsPositives.getString("positive"));
                            }
                            retrospective.setPositives(positives);
                        }
                    }

                    log.info(String.format(Constants.LOG_METHOD_END, methodName));
                    return retrospective;
                }
            }
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return null;
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to find retrospective by id", e);
        }
    }

    /**
     * Возвращает список всех ретроспектив.
     * @return Список ретроспектив
     */
    @Override
    public List<Retrospective> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        List<Retrospective> retrospectives = new ArrayList<>();
        String sql = "SELECT * FROM retrospectives";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            while (rs.next()) {
                Retrospective retrospective = new Retrospective();
                retrospective.setId(rs.getInt("id"));
                Sprint sprint = sprintDAO.findById(rs.getInt("sprint_id"));
                retrospective.setSprint(sprint);
                retrospective.setSummary(rs.getString("summary"));

                // Загрузка improvements
                String sqlImprovements = "SELECT improvement FROM retrospective_improvements WHERE retrospective_id = ?";
                try (PreparedStatement stmtImprovements = conn.prepareStatement(sqlImprovements)) {
                    stmtImprovements.setInt(1, retrospective.getId());
                    try (ResultSet rsImprovements = stmtImprovements.executeQuery()) {
                        List<String> improvements = new ArrayList<>();
                        while (rsImprovements.next()) {
                            improvements.add(rsImprovements.getString("improvement"));
                        }
                        retrospective.setImprovements(improvements);
                    }
                }

                // Загрузка positives
                String sqlPositives = "SELECT positive FROM retrospective_positives WHERE retrospective_id = ?";
                try (PreparedStatement stmtPositives = conn.prepareStatement(sqlPositives)) {
                    stmtPositives.setInt(1, retrospective.getId());
                    try (ResultSet rsPositives = stmtPositives.executeQuery()) {
                        List<String> positives = new ArrayList<>();
                        while (rsPositives.next()) {
                            positives.add(rsPositives.getString("positive"));
                        }
                        retrospective.setPositives(positives);
                    }
                }

                retrospectives.add(retrospective);
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Found " + retrospectives.size() + " retrospectives"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return retrospectives;
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to retrieve all retrospectives", e);
        }
    }

    /**
     * Обновляет данные ретроспективы.
     * @param retrospective Обновленная ретроспектива
     */
    @Override
    public void update(Retrospective retrospective) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, retrospective.toString()));

        String sql = "UPDATE retrospectives SET sprint_id = ?, summary = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, retrospective.getSprint().getId());
            stmt.setString(2, retrospective.getSummary());
            stmt.setInt(3, retrospective.getId());

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Rows affected: " + rowsAffected));

            // Обновление improvements
            String sqlDeleteImprovements = "DELETE FROM retrospective_improvements WHERE retrospective_id = ?";
            try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDeleteImprovements)) {
                stmtDelete.setInt(1, retrospective.getId());
                stmtDelete.executeUpdate();
            }

            String sqlInsertImprovements = "INSERT INTO retrospective_improvements (retrospective_id, improvement) VALUES (?, ?)";
            try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsertImprovements)) {
                for (String improvement : retrospective.getImprovements()) {
                    stmtInsert.setInt(1, retrospective.getId());
                    stmtInsert.setString(2, improvement);
                    stmtInsert.executeUpdate();
                }
            }

            // Обновление positives
            String sqlDeletePositives = "DELETE FROM retrospective_positives WHERE retrospective_id = ?";
            try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDeletePositives)) {
                stmtDelete.setInt(1, retrospective.getId());
                stmtDelete.executeUpdate();
            }

            String sqlInsertPositives = "INSERT INTO retrospective_positives (retrospective_id, positive) VALUES (?, ?)";
            try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsertPositives)) {
                for (String positive : retrospective.getPositives()) {
                    stmtInsert.setInt(1, retrospective.getId());
                    stmtInsert.setString(2, positive);
                    stmtInsert.executeUpdate();
                }
            }

            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to update retrospective", e);
        }
    }

    /**
     * Удаляет ретроспективу по идентификатору.
     * @param id Идентификатор ретроспективы
     */
    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        String sql = "DELETE FROM retrospectives WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            int rowsAffected = stmt.executeUpdate();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Rows affected: " + rowsAffected));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (SQLException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to delete retrospective", e);
        }
    }
}
