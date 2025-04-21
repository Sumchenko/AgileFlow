package ru.sfedu.agileflow.csv;

import org.apache.log4j.Logger;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.User;
import ru.sfedu.agileflow.utils.CsvUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO-класс для работы со связями проектов и пользователей в CSV.
 */
public class ProjectUserCsvDAO {
    private static final Logger log = Logger.getLogger(ProjectUserCsvDAO.class);
    private static final String FILE_NAME = "project_users.csv";

    /**
     * Добавляет пользователя в проект.
     * @param projectId Идентификатор проекта
     * @param userId Идентификатор пользователя
     */
    public void addUserToProject(Integer projectId, Integer userId) {
        String methodName = "addUserToProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId + ", userId: " + userId));

        try {
            List<String[]> records = CsvUtil.readCsv(FILE_NAME);
            boolean exists = records.stream()
                    .anyMatch(record -> record[0].equals(String.valueOf(projectId)) && record[1].equals(String.valueOf(userId)));
            if (!exists) {
                records.add(new String[]{String.valueOf(projectId), String.valueOf(userId)});
                CsvUtil.writeCsv(FILE_NAME, records);
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Пользователь добавлен в проект"));
            } else {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Связь уже существует"));
            }
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось добавить пользователя в проект: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось добавить пользователя в проект", e);
        }
    }

    /**
     * Удаляет пользователя из проекта.
     * @param projectId Идентификатор проекта
     * @param userId Идентификатор пользователя
     */
    public void removeUserFromProject(Integer projectId, Integer userId) {
        String methodName = "removeUserFromProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId + ", userId: " + userId));

        try {
            List<String[]> records = CsvUtil.readCsv(FILE_NAME);
            records.removeIf(record -> record[0].equals(String.valueOf(projectId)) && record[1].equals(String.valueOf(userId)));
            CsvUtil.writeCsv(FILE_NAME, records);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Пользователь удален из проекта"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить пользователя из проекта: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось удалить пользователя из проекта", e);
        }
    }

    /**
     * Возвращает список пользователей проекта.
     * @param projectId Идентификатор проекта
     * @return Список пользователей
     */
    public List<User> getUsersByProject(Integer projectId) {
        String methodName = "getUsersByProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId));

        try {
            List<String[]> records = CsvUtil.readCsv(FILE_NAME);
            UserCsvDAO userDAO = new UserCsvDAO();
            List<User> users = new ArrayList<>();
            for (String[] record : records) {
                if (record[0].equals(String.valueOf(projectId))) {
                    try {
                        int userId = Integer.parseInt(record[1]);
                        userDAO.findById(userId).ifPresent(users::add);
                    } catch (NumberFormatException e) {
                        // Пропускаем некорректные записи
                    }
                }
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено пользователей: " + users.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return users;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить пользователей проекта: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось получить пользователей проекта", e);
        }
    }

    /**
     * Возвращает список проектов пользователя.
     * @param userId Идентификатор пользователя
     * @return Список проектов
     */
    public List<Project> getProjectsByUser(Integer userId) {
        String methodName = "getProjectsByUser";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "userId: " + userId));

        try {
            List<String[]> records = CsvUtil.readCsv(FILE_NAME);
            ProjectCsvDAO projectDAO = new ProjectCsvDAO();
            List<Project> projects = new ArrayList<>();
            for (String[] record : records) {
                if (record[1].equals(String.valueOf(userId))) {
                    try {
                        int projectId = Integer.parseInt(record[0]);
                        projectDAO.findById(projectId).ifPresent(projects::add);
                    } catch (NumberFormatException e) {
                        // Пропускаем некорректные записи
                    }
                }
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено проектов: " + projects.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return projects;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить проекты пользователя: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось получить проекты пользователя", e);
        }
    }
}