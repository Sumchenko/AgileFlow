package ru.sfedu.agileflow.lab4.componentset;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.constants.Constants;

import java.util.List;
import java.util.Optional;

/**
 * Реализация DAO для управления задачами с коллекцией вложений типа Set.
 */
public class TaskComponentSetDAOImpl implements TaskComponentSetDAO {
    private static final Logger log = Logger.getLogger(TaskComponentSetDAOImpl.class);

    @Override
    public void create(Task task) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, task.toString()));

        try (EntityManager em = DatabaseConfig.getLab4EntityManager()) {
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

    @Override
    public Optional<Task> findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try (EntityManager em = DatabaseConfig.getLab4EntityManager()) {
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

    @Override
    public List<Task> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try (EntityManager em = DatabaseConfig.getLab4EntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            TypedQuery<Task> query = em.createQuery("SELECT t FROM ComponentSetTask t", Task.class);
            List<Task> tasks = query.getResultList();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Found " + tasks.size() + " tasks"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return tasks;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()), e);
            throw new RuntimeException("Failed to retrieve all tasks", e);
        }
    }

    @Override
    public void update(Task task) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, task.toString()));

        try (EntityManager em = DatabaseConfig.getLab4EntityManager()) {
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

    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try (EntityManager em = DatabaseConfig.getLab4EntityManager()) {
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
}