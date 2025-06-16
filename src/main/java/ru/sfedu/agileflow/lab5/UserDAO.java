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
 * DAO-класс для управления пользователями в базе данных.
 */
public class UserDAO implements GenericDAO<User, Integer> {
    private static final Logger log = Logger.getLogger(UserDAO.class);

    @Override
    public void create(User user) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, user.toString()));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            em.persist(user);
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "User persisted with ID: " + user.getId()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать пользователя: " + e.getMessage()), e);
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    public Optional<User> findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            User user = em.find(User.class, id);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, user != null ? "User found" : "User not found"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.ofNullable(user);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти пользователя: " + e.getMessage()), e);
            throw new RuntimeException("Failed to find user by id", e);
        }
    }

    @Override
    public List<User> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
            List<User> users = query.getResultList();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Found " + users.size() + " users"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return users;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить список пользователей: " + e.getMessage()), e);
            throw new RuntimeException("Failed to retrieve all users", e);
        }
    }

    @Override
    public void update(User user) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, user.toString()));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            em.getTransaction().begin();
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            em.merge(user);
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "User updated"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить пользователя: " + e.getMessage()), e);
            throw new RuntimeException("Failed to update user", e);
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
            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
            }
            em.getTransaction().commit();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, user != null ? "User deleted" : "User not found"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить пользователя: " + e.getMessage()), e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}