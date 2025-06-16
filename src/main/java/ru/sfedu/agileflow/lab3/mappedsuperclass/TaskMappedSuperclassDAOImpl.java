package ru.sfedu.agileflow.lab3.mappedsuperclass;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация DAO для управления задачами в базе данных с использованием стратегии MappedSuperclass.
 */
public class TaskMappedSuperclassDAOImpl implements TaskMappedSuperclassDAO {
    private static final Logger log = Logger.getLogger(TaskMappedSuperclassDAOImpl.class);

    /**
     * Создает новую задачу в базе данных.
     * @param task Задача для сохранения (BugTask или FeatureTask)
     */
    @Override
    public void create(Task task) {
        String methodName = "create_" + UUID.randomUUID().toString();
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, task.toString()));

        try (EntityManager em = DatabaseConfig.getLab3MappedSuperclassEntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            em.persist(task);
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Task persisted with ID: " + task.getId()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()), e);
            throw new RuntimeException("Failed to create task", e);
        }
    }

    /**
     * Находит задачу по идентификатору.
     * @param id Идентификатор задачи
     * @param taskClass Класс задачи (BugTask или FeatureTask)
     * @return Optional с задачей, если найдена, иначе пустой Optional
     */
    @Override
    public <T extends Task> Optional<T> findById(Integer id, Class<T> taskClass) {
        String methodName = "findById_" + UUID.randomUUID().toString();
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id + ", class: " + taskClass.getSimpleName()));

        try (EntityManager em = DatabaseConfig.getLab3MappedSuperclassEntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            T task = em.find(taskClass, id);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, task != null ? "Task found" : "Task not found"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.ofNullable(task);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()), e);
            throw new RuntimeException("Failed to find task by id", e);
        }
    }

    /**
     * Возвращает список всех задач.
     * @return Список задач (BugTask и FeatureTask)
     */
    @Override
    public List<Task> findAll() {
        String methodName = "findAll_" + UUID.randomUUID().toString();
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try (EntityManager em = DatabaseConfig.getLab3MappedSuperclassEntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            List<Task> tasks = new ArrayList<>();
            TypedQuery<BugTask> bugQuery = em.createQuery("SELECT t FROM BugTask t", BugTask.class);
            TypedQuery<FeatureTask> featureQuery = em.createQuery("SELECT t FROM FeatureTask t", FeatureTask.class);
            tasks.addAll(bugQuery.getResultList());
            tasks.addAll(featureQuery.getResultList());
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Found " + tasks.size() + " tasks"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return tasks;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()), e);
            throw new RuntimeException("Failed to retrieve all tasks", e);
        }
    }

    /**
     * Обновляет данные задачи.
     * @param task Обновленная задача
     */
    @Override
    public void update(Task task) {
        String methodName = "update_" + UUID.randomUUID().toString();
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, task.toString()));

        try (EntityManager em = DatabaseConfig.getLab3MappedSuperclassEntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            em.merge(task);
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Task updated"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()), e);
            throw new RuntimeException("Failed to update task", e);
        }
    }

    /**
     * Удаляет задачу по идентификатору.
     * @param id Идентификатор задачи
     * @param taskClass Класс задачи (BugTask или FeatureTask)
     */
    @Override
    public <T extends Task> void delete(Integer id, Class<T> taskClass) {
        String methodName = "delete_" + UUID.randomUUID().toString();
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id + ", class: " + taskClass.getSimpleName()));

        try (EntityManager em = DatabaseConfig.getLab3MappedSuperclassEntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            T task = em.find(taskClass, id);
            if (task != null) {
                em.remove(task);
            }
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, task != null ? "Task deleted" : "Task not found"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()), e);
            throw new RuntimeException("Failed to delete task", e);
        }
    }

    /**
     * Находит задачи по статусу.
     * @param status Статус задачи
     * @return Список задач с указанным статусом
     */
    @Override
    public List<Task> findByStatus(TaskStatus status) {
        String methodName = "findByStatus_" + UUID.randomUUID().toString();
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "status: " + status));

        try (EntityManager em = DatabaseConfig.getLab3MappedSuperclassEntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            List<Task> tasks = new ArrayList<>();
            TypedQuery<BugTask> bugQuery = em.createQuery("SELECT t FROM BugTask t WHERE t.status = :status", BugTask.class);
            TypedQuery<FeatureTask> featureQuery = em.createQuery("SELECT t FROM FeatureTask t WHERE t.status = :status", FeatureTask.class);
            bugQuery.setParameter("status", status);
            featureQuery.setParameter("status", status);
            tasks.addAll(bugQuery.getResultList());
            tasks.addAll(featureQuery.getResultList());
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Found " + tasks.size() + " tasks with status " + status));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return tasks;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()), e);
            throw new RuntimeException("Failed to find tasks by status", e);
        }
    }
}