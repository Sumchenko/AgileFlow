package ru.sfedu.agileflow.lab5;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.constants.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * DAO-класс для выполнения суммарных запросов по сущностям с использованием NativeSQL, Criteria API и HQL.
 */
public class SummaryDAO {
    private static final Logger log = Logger.getLogger(SummaryDAO.class);

    /**
     * Выполняет NativeSQL-запрос для получения количества задач в проекте.
     * @param projectId Идентификатор проекта
     * @return Количество задач
     */
    public Long getTaskCountByProjectNativeSQL(int projectId) {
        String methodName = "getTaskCountByProjectNativeSQL";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Long count = (Long) em.createNativeQuery(
                            "SELECT COUNT(t.id) " +
                                    "FROM lab5_tasks t " +
                                    "JOIN lab5_sprints s ON t.sprint_id = s.id " +
                                    "WHERE s.project_id = :projectId")
                    .setParameter("projectId", projectId)
                    .getSingleResult();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Task count: " + count));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return count;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to execute NativeSQL query", e);
        }
    }

    /**
     * Выполняет HQL-запрос для получения количества задач в проекте.
     * @param projectId Идентификатор проекта
     * @return Количество задач
     */
    public Long getTaskCountByProjectHQL(int projectId) {
        String methodName = "getTaskCountByProjectHQL";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            Session session = em.unwrap(Session.class);
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Long count = (Long) session.createQuery(
                            "SELECT COUNT(t.id) " +
                                    "FROM Task t " +
                                    "JOIN t.sprint s " +
                                    "WHERE s.project.id = :projectId")
                    .setParameter("projectId", projectId)
                    .uniqueResult();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Task count: " + count));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return count;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to execute HQL query", e);
        }
    }

    /**
     * Выполняет Criteria-запрос для получения количества задач в проекте.
     * @param projectId Идентификатор проекта
     * @return Количество задач
     */
    public Long getTaskCountByProjectCriteria(int projectId) {
        String methodName = "getTaskCountByProjectCriteria";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Task> taskRoot = cq.from(Task.class);
            Join<Task, Sprint> sprintJoin = taskRoot.join("sprint");
            Join<Sprint, Project> projectJoin = sprintJoin.join("project");

            cq.select(cb.count(taskRoot.get("id")))
                    .where(cb.equal(projectJoin.get("id"), projectId));

            Long count = em.createQuery(cq).getSingleResult();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Task count: " + count));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return count;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to execute Criteria query", e);
        }
    }

    /**
     * Выполняет NativeSQL-запрос для получения количества пользователей в проекте.
     * @param projectId Идентификатор проекта
     * @return Количество пользователей
     */
    public Long getUserCountByProjectNativeSQL(int projectId) {
        String methodName = "getUserCountByProjectNativeSQL";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Long count = (Long) em.createNativeQuery(
                            "SELECT COUNT(u.id) " +
                                    "FROM lab5_users u " +
                                    "JOIN lab5_project_users pu ON u.id = pu.user_id " +
                                    "WHERE pu.project_id = :projectId")
                    .setParameter("projectId", projectId)
                    .getSingleResult();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "User count: " + count));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return count;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to execute NativeSQL query", e);
        }
    }

    /**
     * Выполняет HQL-запрос для получения количества пользователей в проекте.
     * @param projectId Идентификатор проекта
     * @return Количество пользователей
     */
    public Long getUserCountByProjectHQL(int projectId) {
        String methodName = "getUserCountByProjectHQL";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            Session session = em.unwrap(Session.class);
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Long count = (Long) session.createQuery(
                            "SELECT COUNT(u.id) " +
                                    "FROM Project p " +
                                    "JOIN p.users u " +
                                    "WHERE p.id = :projectId")
                    .setParameter("projectId", projectId)
                    .uniqueResult();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "User count: " + count));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return count;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to execute HQL query", e);
        }
    }

    /**
     * Выполняет Criteria-запрос для получения количества пользователей в проекте.
     * @param projectId Идентификатор проекта
     * @return Количество пользователей
     */
    public Long getUserCountByProjectCriteria(int projectId) {
        String methodName = "getUserCountByProjectCriteria";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Project> projectRoot = cq.from(Project.class);
            Join<Project, User> userJoin = projectRoot.join("users");

            cq.select(cb.count(userJoin.get("id")))
                    .where(cb.equal(projectRoot.get("id"), projectId));

            Long count = em.createQuery(cq).getSingleResult();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "User count: " + count));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return count;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to execute Criteria query", e);
        }
    }

    /**
     * Выполняет NativeSQL-запрос для получения количества спринтов в проекте.
     * @param projectId Идентификатор проекта
     * @return Количество спринтов
     */
    public Long getSprintCountByProjectNativeSQL(int projectId) {
        String methodName = "getSprintCountByProjectNativeSQL";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Long count = (Long) em.createNativeQuery(
                            "SELECT COUNT(s.id) " +
                                    "FROM lab5_sprints s " +
                                    "WHERE s.project_id = :projectId")
                    .setParameter("projectId", projectId)
                    .getSingleResult();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Sprint count: " + count));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return count;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to execute NativeSQL query", e);
        }
    }

    /**
     * Выполняет HQL-запрос для получения количества спринтов в проекте.
     * @param projectId Идентификатор проекта
     * @return Количество спринтов
     */
    public Long getSprintCountByProjectHQL(int projectId) {
        String methodName = "getSprintCountByProjectHQL";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            Session session = em.unwrap(Session.class);
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            Long count = (Long) session.createQuery(
                            "SELECT COUNT(s.id) " +
                                    "FROM Sprint s " +
                                    "WHERE s.project.id = :projectId")
                    .setParameter("projectId", projectId)
                    .uniqueResult();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Sprint count: " + count));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return count;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to execute HQL query", e);
        }
    }

    /**
     * Выполняет Criteria-запрос для получения количества спринтов в проекте.
     * @param projectId Идентификатор проекта
     * @return Количество спринтов
     */
    public Long getSprintCountByProjectCriteria(int projectId) {
        String methodName = "getSprintCountByProjectCriteria";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId));

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Sprint> sprintRoot = cq.from(Sprint.class);
            Join<Sprint, Project> projectJoin = sprintRoot.join("project");

            cq.select(cb.count(sprintRoot.get("id")))
                    .where(cb.equal(projectJoin.get("id"), projectId));

            Long count = em.createQuery(cq).getSingleResult();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Sprint count: " + count));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return count;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to execute Criteria query", e);
        }
    }

    /**
     * Анализирует производительность запроса для подсчета задач в проекте.
     * @param projectId Идентификатор проекта
     * @param iterations Количество итераций для замера
     * @return Map с результатами времени выполнения (в наносекундах)
     */
    public Map<String, Long> analyzeTaskCountPerformance(int projectId, int iterations) {
        String methodName = "analyzeTaskCountPerformance";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "projectId: " + projectId + ", iterations: " + iterations));

        Map<String, Long> results = new HashMap<>();
        long nativeSqlTotalTime = 0;
        long hqlTotalTime = 0;
        long criteriaTotalTime = 0;

        try (EntityManager em = DatabaseConfig.getLab5EntityManager()) {
            for (int i = 0; i < iterations; i++) {
                // NativeSQL
                long startTime = System.nanoTime();
                getTaskCountByProjectNativeSQL(projectId);
                long endTime = System.nanoTime();
                nativeSqlTotalTime += (endTime - startTime);

                // HQL
                startTime = System.nanoTime();
                getTaskCountByProjectHQL(projectId);
                endTime = System.nanoTime();
                hqlTotalTime += (endTime - startTime);

                // Criteria
                startTime = System.nanoTime();
                getTaskCountByProjectCriteria(projectId);
                endTime = System.nanoTime();
                criteriaTotalTime += (endTime - startTime);
            }

            results.put("NativeSQL", nativeSqlTotalTime / iterations);
            results.put("HQL", hqlTotalTime / iterations);
            results.put("Criteria", criteriaTotalTime / iterations);

            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName,
                    "Performance results: NativeSQL=" + results.get("NativeSQL") + "ns, " +
                            "HQL=" + results.get("HQL") + "ns, " +
                            "Criteria=" + results.get("Criteria") + "ns"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return results;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Failed to analyze performance", e);
        }
    }
}