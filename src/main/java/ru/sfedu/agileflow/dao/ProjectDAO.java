package ru.sfedu.agileflow.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;

import java.util.List;
import java.util.Optional;

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

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            em.persist(project);
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Project persisted with ID: " + project.getId()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to create project", e);
        }
    }

    /**
     * Находит проект по идентификатору.
     * @param id Идентификатор проекта
     * @return Optional с проектом, если найден, иначе пустой Optional
     */
    @Override
    public Optional<Project> findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Project project = em.find(Project.class, id);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, project != null ? "Project found" : "Project not found"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.ofNullable(project);
        } catch (Exception e) {
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

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            TypedQuery<Project> query = em.createQuery("SELECT p FROM Project p", Project.class);
            List<Project> projects = query.getResultList();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Found " + projects.size() + " projects"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return projects;
        } catch (Exception e) {
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

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            em.merge(project);
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Project updated"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
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

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Project project = em.find(Project.class, id);
            if (project != null) {
                em.remove(project);
            }
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, project != null ? "Project deleted" : "Project not found"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to delete project", e);
        }
    }
}