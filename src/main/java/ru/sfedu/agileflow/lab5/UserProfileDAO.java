package ru.sfedu.agileflow.lab5;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.dao.GenericDAO;

import java.util.List;
import java.util.Optional;

/**
 * DAO-класс для управления профилями пользователей в базе данных.
 */
public class UserProfileDAO implements GenericDAO<UserProfile, Integer> {
    private static final Logger log = Logger.getLogger(UserProfileDAO.class);

    @Override
    public void create(UserProfile userProfile) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, userProfile.toString()));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            em.persist(userProfile);
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "UserProfile persisted with ID: " + userProfile.getId()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to create user profile", e);
        }
    }

    @Override
    public Optional<UserProfile> findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            UserProfile userProfile = em.find(UserProfile.class, id);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, userProfile != null ? "UserProfile found" : "UserProfile not found"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.ofNullable(userProfile);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to find user profile by id", e);
        }
    }

    @Override
    public List<UserProfile> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            TypedQuery<UserProfile> query = em.createQuery("SELECT up FROM UserProfile up", UserProfile.class);
            List<UserProfile> userProfiles = query.getResultList();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Found " + userProfiles.size() + " user profiles"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return userProfiles;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to retrieve all user profiles", e);
        }
    }

    @Override
    public void update(UserProfile userProfile) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, userProfile.toString()));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            em.merge(userProfile);
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "UserProfile updated"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to update user profile", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            UserProfile userProfile = em.find(UserProfile.class, id);
            if (userProfile != null) {
                em.remove(userProfile);
            }
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, userProfile != null ? "UserProfile deleted" : "UserProfile not found"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to delete user profile", e);
        }
    }
}