package ru.sfedu.agileflow.config;

import org.apache.log4j.Logger;
import ru.sfedu.agileflow.constants.Constants;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Класс для управления подключением к базе данных PostgreSQL.
 */
public class DatabaseConfig {
    private static final Logger log = Logger.getLogger(DatabaseConfig.class);
    private static final Properties properties;

    static {
        properties = new Properties();
        try {
            properties.load(DatabaseConfig.class.getClassLoader().getResourceAsStream(Constants.DB_PROPERTIES_PATH));
            log.info("DatabaseConfig [1] Loaded database properties successfully");
        } catch (IOException e) {
            log.error("DatabaseConfig [1] Error loading database properties: " + e.getMessage());
            throw new RuntimeException("Failed to load database properties", e);
        }
    }

    /**
     * Получает новое соединение с базой данных.
     * @return Connection - соединение с базой данных
     * @throws SQLException если не удалось установить соединение
     */
    public static Connection getConnection() throws SQLException {
        String methodName = "getConnection";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "Establishing new connection"));
        String url = properties.getProperty(Constants.DB_URL_KEY);
        String username = properties.getProperty(Constants.DB_USERNAME_KEY);
        String password = properties.getProperty(Constants.DB_PASSWORD_KEY);

        log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
        Connection connection = DriverManager.getConnection(url, username, password);
        log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Connection established with URL: " + url));

        log.info(String.format(Constants.LOG_METHOD_END, methodName));
        return connection;
    }
}