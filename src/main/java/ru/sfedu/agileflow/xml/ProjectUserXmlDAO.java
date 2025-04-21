package ru.sfedu.agileflow.xml;

import jakarta.xml.bind.JAXBException;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.XmlConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO-класс для управления связями между проектами и пользователями в XML хранилище.
 */
public class ProjectUserXmlDAO {
    private static final Logger log = Logger.getLogger(ProjectUserXmlDAO.class);
    private final ProjectXmlDAO projectDAO = new ProjectXmlDAO();
    private final UserXmlDAO userDAO = new UserXmlDAO();

    /**
     * Добавляет пользователя в проект.
     * @param projectId Идентификатор проекта
     * @param userId Идентификатор пользователя
     */
    public void addUserToProject(int projectId, int userId) {
        String methodName = "addUserToProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId + ", userId: " + userId));

        try {
            Optional<Project> projectOpt = projectDAO.findById(projectId);
            Optional<User> userOpt = userDAO.findById(userId);

            if (!projectOpt.isPresent()) {
                log.error(String.format(Constants.LOG_ERROR, methodName, "Проект с ID " + projectId + " не найден"));
                throw new RuntimeException("Проект с ID " + projectId + " не найден");
            }
            if (!userOpt.isPresent()) {
                log.error(String.format(Constants.LOG_ERROR, methodName, "Пользователь с ID " + userId + " не найден"));
                throw new RuntimeException("Пользователь с ID " + userId + " не найден");
            }

            Project project = projectOpt.get();
            User user = userOpt.get();

            if (project.getUsers() == null) {
                project.setUsers(new ArrayList<>());
            }

            if (!project.getUsers().contains(user)) {
                project.getUsers().add(user);
                projectDAO.update(project);
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Пользователь добавлен в проект"));
            } else {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Пользователь уже в проекте"));
            }
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось добавить пользователя в проект: " + e.getMessage()));
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

        try {
            Optional<Project> projectOpt = projectDAO.findById(projectId);
            Optional<User> userOpt = userDAO.findById(userId);

            if (!projectOpt.isPresent()) {
                log.error(String.format(Constants.LOG_ERROR, methodName, "Проект с ID " + projectId + " не найден"));
                throw new RuntimeException("Проект с ID " + projectId + " не найден");
            }
            if (!userOpt.isPresent()) {
                log.error(String.format(Constants.LOG_ERROR, methodName, "Пользователь с ID " + userId + " не найден"));
                throw new RuntimeException("Пользователь с ID " + userId + " не найден");
            }

            Project project = projectOpt.get();
            User user = userOpt.get();

            if (project.getUsers() != null && project.getUsers().contains(user)) {
                project.getUsers().remove(user);
                projectDAO.update(project);
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Пользователь удален из проекта"));
            } else {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Пользователь не найден в проекте"));
            }
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить пользователя из проекта: " + e.getMessage()));
            throw new RuntimeException("Не удалось удалить пользователя из проекта", e);
        }
    }

    /**
     * Возвращает список пользователей, участвующих в проекте.
     * @param projectId Идентификатор проекта
     * @return Список пользователей
     */
    public List<User> getUsersByProject(int projectId) {
        String methodName = "getUsersByProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId));

        try {
            Optional<Project> projectOpt = projectDAO.findById(projectId);
            if (!projectOpt.isPresent()) {
                log.error(String.format(Constants.LOG_ERROR, methodName, "Проект с ID " + projectId + " не найден"));
                throw new RuntimeException("Проект с ID " + projectId + " не найден");
            }

            Project project = projectOpt.get();
            List<User> users = project.getUsers() != null ? project.getUsers() : new ArrayList<>();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено пользователей: " + users.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return users;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить пользователей проекта: " + e.getMessage()));
            throw new RuntimeException("Не удалось получить пользователей проекта", e);
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

        try {
            Optional<User> userOpt = userDAO.findById(userId);
            if (!userOpt.isPresent()) {
                log.error(String.format(Constants.LOG_ERROR, methodName, "Пользователь с ID " + userId + " не найден"));
                throw new RuntimeException("Пользователь с ID " + userId + " не найден");
            }

            List<Project> allProjects = projectDAO.findAll();
            List<Project> userProjects = new ArrayList<>();
            for (Project project : allProjects) {
                if (project.getUsers() != null && project.getUsers().stream().anyMatch(u -> u.getId() == userId)) {
                    userProjects.add(project);
                }
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено проектов: " + userProjects.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return userProjects;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить проекты пользователя: " + e.getMessage()));
            throw new RuntimeException("Не удалось получить проекты пользователя", e);
        }
    }

    private XmlDataWrapper loadData() throws JAXBException {
        File file = new File(XmlConfig.getFilePath(Project.class));
        if (!file.exists()) {
            return new XmlDataWrapper();
        }
        return (XmlDataWrapper) XmlConfig.getUnmarshaller().unmarshal(file);
    }

    private void saveData(XmlDataWrapper wrapper) throws JAXBException {
        File file = new File(XmlConfig.getFilePath(Project.class));
        XmlConfig.getMarshaller().marshal(wrapper, file);
    }
}