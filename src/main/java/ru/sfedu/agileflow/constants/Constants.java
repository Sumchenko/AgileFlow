package ru.sfedu.agileflow.constants;

/**
 * Класс для хранения констант, используемых в проекте.
 */
public class Constants {

    // Константы для подключения к базе данных
    public static final String DB_PROPERTIES_PATH = "database.properties";
    public static final String DB_URL_KEY = "db.url";
    public static final String DB_USERNAME_KEY = "db.username";
    public static final String DB_PASSWORD_KEY = "db.password";

    // Константы для логирования
    public static final String LOG_METHOD_START = "%s [1] Начало выполнения метода";
    public static final String LOG_METHOD_DEBUG = "%s [1] Параметры метода: %s";
    public static final String LOG_DB_OPERATION = "%s [2] Выполнение операции с базой данных";
    public static final String LOG_DB_DEBUG = "%s [2] Детали операции с базой данных: %s";
    public static final String LOG_METHOD_END = "%s [3] Метод успешно завершен";
    public static final String LOG_ERROR = "%s [3] Произошла ошибка: %s";

    // Константы для тестов
    public static final String TEST_USER_NAME = "Тестовый Пользователь";
    public static final String TEST_USER_EMAIL = "test@example.com";
    public static final String TEST_PROJECT_NAME = "Тестовый Проект";
    public static final String TEST_SPRINT_SUMMARY = "Тестовое Резюме";
}