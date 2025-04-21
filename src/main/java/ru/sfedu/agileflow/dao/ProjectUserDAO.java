package ru.sfedu.agileflow.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.User;

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

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Project project = em.find(Project.class, projectId);
            User user = em.find(User.class, userId);
            if (project != null && user != null) {
                project.getUsers().add(user);
                em.merge(project);
            }
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "User added to project"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
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

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Project project = em.find(Project.class, projectId);
            User user = em.find(User.class, userId);
            if (project != null && user != null) {
                project.getUsers().remove(user);
                em.merge(project);
            }
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "User removed from project"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
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

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Project project = em.find(Project.class, projectId);
            List<User> users = project != null ? project.getUsers() : List.of();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено пользователей: " + users.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return users;
        } catch (Exception e) {
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

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            User user = em.find(User.class, userId);
            List<Project> projects = user != null ? user.getProjects() : List.of();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено проектов: " + projects.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return projects;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Не удалось получить список проектов пользователя", e);
        }
    }
}