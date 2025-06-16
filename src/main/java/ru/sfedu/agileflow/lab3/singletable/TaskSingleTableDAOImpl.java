package ru.sfedu.agileflow.lab3.singletable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.TaskStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация DAO для управления задачами в базе данных с использованием стратегии Single Table.
 */
public class TaskSingleTableDAOImpl implements TaskSingleTableDAO {
    private static final Logger log = Logger.getLogger(TaskSingleTableDAOImpl.class);

    /**
     * Создает новую задачу в базе данных.
     * @param task Задача для сохранения
     */
    @Override
    public void create(Task task) {
        String methodName = "create_" + UUID.randomUUID().toString();
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, task.toString()));

        try (EntityManager em = DatabaseConfig.getLab3SingleTableEntityManager()) {
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
     * @return Optional с задачей, если найдена, иначе пустой Optional
     */
    @Override
    public Optional<Task> findById(Integer id) {
        String methodName = "findById_" + UUID.randomUUID().toString();
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try (EntityManager em = DatabaseConfig.getLab3SingleTableEntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Task task = em.find(Task.class, id);
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
     * @return Список задач
     */
    @Override
    public List<Task> findAll() {
        String methodName = "findAll_" + UUID.randomUUID().toString();
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try (EntityManager em = DatabaseConfig.getLab3SingleTableEntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            TypedQuery<Task> query = em.createQuery("SELECT t FROM Task t", Task.class);
            List<Task> tasks = query.getResultList();
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

        try (EntityManager em = DatabaseConfig.getLab3SingleTableEntityManager()) {
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
     */
    @Override
    public void delete(Integer id) {
        String methodName = "delete_" + UUID.randomUUID().toString();
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try (EntityManager em = DatabaseConfig.getLab3SingleTableEntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Task task = em.find(Task.class, id);
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

        try (EntityManager em = DatabaseConfig.getLab3SingleTableEntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            TypedQuery<Task> query = em.createQuery("SELECT t FROM Task t WHERE t.status = :status", Task.class);
            query.setParameter("status", status);
            List<Task> tasks = query.getResultList();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Found " + tasks.size() + " tasks with status " + status));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return tasks;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()), e);
            throw new RuntimeException("Failed to find tasks by status", e);
        }
    }
}