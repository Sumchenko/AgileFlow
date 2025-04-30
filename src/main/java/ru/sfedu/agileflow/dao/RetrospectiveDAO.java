package ru.sfedu.agileflow.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Retrospective;

import java.util.List;
import java.util.Optional;

/**
 * DAO-класс для управления ретроспективами в базе данных.
 */
public class RetrospectiveDAO implements GenericDAO<Retrospective, Integer> {
    private static final Logger log = Logger.getLogger(RetrospectiveDAO.class);

    /**
     * Создает новую ретроспективу в базе данных.
     * @param retrospective Ретроспектива для сохранения
     */
    @Override
    public void create(Retrospective retrospective) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, retrospective.toString()));

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            em.persist(retrospective);
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Retrospective persisted with ID: " + retrospective.getId()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to create retrospective", e);
        }
    }

    /**
     * Находит ретроспективу по идентификатору.
     * @param id Идентификатор ретроспективы
     * @return Optional с ретроспективой, если найдена, иначе пустой Optional
     */
    @Override
    public Optional<Retrospective> findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Retrospective retrospective = em.find(Retrospective.class, id);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, retrospective != null ? "Retrospective found" : "Retrospective not found"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.ofNullable(retrospective);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to find retrospective by id", e);
        }
    }

    /**
     * Возвращает список всех ретроспектив.
     * @return Список ретроспектив
     */
    @Override
    public List<Retrospective> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            TypedQuery<Retrospective> query = em.createQuery("SELECT r FROM Retrospective r", Retrospective.class);
            List<Retrospective> retrospectives = query.getResultList();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Found " + retrospectives.size() + " retrospectives"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return retrospectives;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to retrieve all retrospectives", e);
        }
    }

    /**
     * Обновляет данные ретроспективы.
     * @param retrospective Обновленная ретроспектива
     */
    @Override
    public void update(Retrospective retrospective) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, retrospective.toString()));

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            em.merge(retrospective);
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Retrospective updated"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to update retrospective", e);
        }
    }

    /**
     * Удаляет ретроспективу по идентификатору.
     * @param id Идентификатор ретроспективы
     */
    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try (EntityManager em = DatabaseConfig.getEntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Retrospective retrospective = em.find(Retrospective.class, id);
            if (retrospective != null) {
                em.remove(retrospective);
            }
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, retrospective != null ? "Retrospective deleted" : "Retrospective not found"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to delete retrospective", e);
        }
    }

}