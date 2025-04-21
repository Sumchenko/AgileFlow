package ru.sfedu.agileflow.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Sprint;

import java.util.List;
import java.util.Optional;

/**
 * DAO-класс для управления спринтами в базе данных.
 */
public class SprintDAO implements GenericDAO<Sprint, Integer> {
    private static final Logger log = Logger.getLogger(SprintDAO.class);

    /**
     * Создает новый спринт в базе данных.
     * @param sprint Спринт для сохранения
     */
    @Override
    public void create(Sprint sprint) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, sprint.toString()));

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            em.persist(sprint);
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Sprint persisted with ID: " + sprint.getId()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to create sprint", e);
        }
    }

    /**
     * Находит спринт по идентификатору.
     * @param id Идентификатор спринта
     * @return Optional с спринтом, если найден, иначе пустой Optional
     */
    @Override
    public Optional<Sprint> findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Sprint sprint = em.find(Sprint.class, id);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, sprint != null ? "Sprint found" : "Sprint not found"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.ofNullable(sprint);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to find sprint by id", e);
        }
    }

    /**
     * Возвращает список всех спринтов.
     * @return Список спринтов
     */
    @Override
    public List<Sprint> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            TypedQuery<Sprint> query = em.createQuery("SELECT s FROM Sprint s", Sprint.class);
            List<Sprint> sprints = query.getResultList();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Found " + sprints.size() + " sprints"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return sprints;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to retrieve all sprints", e);
        }
    }

    /**
     * Обновляет данные спринта.
     * @param sprint Обновленный спринт
     */
    @Override
    public void update(Sprint sprint) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, sprint.toString()));

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            em.merge(sprint);
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Sprint updated"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to update sprint", e);
        }
    }

    /**
     * Удаляет спринт по идентификатору.
     * @param id Идентификатор спринта
     */
    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Sprint sprint = em.find(Sprint.class, id);
            if (sprint != null) {
                em.remove(sprint);
            }
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, sprint != null ? "Sprint deleted" : "Sprint not found"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to delete sprint", e);
        }
    }
}