package ru.sfedu.agileflow.lab5;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.config.DatabaseConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.dao.ProjectDAO;
import ru.sfedu.agileflow.dao.SprintDAO;
import ru.sfedu.agileflow.dao.TaskDAO;
import ru.sfedu.agileflow.dao.UserDAO;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Тестовый класс для проверки операций SummaryDAO с суммарными запросами.
 */
public class SummaryDAOTest {
    private static final Logger log = Logger.getLogger(SummaryDAOTest.class);
    private SummaryDAO summaryDAO;
    private ProjectDAO projectDAO;
    private UserDAO userDAO;
    private SprintDAO sprintDAO;
    private TaskDAO taskDAO;
    private Project testProject;
    private User testUser;
    private Sprint testSprint;

    /**
     * Подготовка перед каждым тестом.
     */
    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        DatabaseConfig.testConnection();
        summaryDAO = new SummaryDAO();
        projectDAO = new ProjectDAO();
        userDAO = new UserDAO();
        sprintDAO = new SprintDAO();
        taskDAO = new TaskDAO();

        EntityManager em = DatabaseConfig.getLab5EntityManager();
        try {
            em.getTransaction().begin();
            testProject = new Project("Тестовый проект", "Описание тестового проекта");
            testProject.setUsers(new ArrayList<>());
            em.persist(testProject);

            testUser = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            testUser.setProjects(new ArrayList<>());
            em.persist(testUser);

            // Синхронизация ManyToMany связи
            testProject.getUsers().add(testUser);
            testUser.getProjects().add(testProject);
            em.merge(testProject);
            em.merge(testUser);

            testSprint = new Sprint(new Date(), new Date(), testProject);
            em.persist(testSprint);

            Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.TO_DO, 1, testSprint, testUser);
            Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS, 2, testSprint, testUser);
            em.persist(task1);
            em.persist(task2);

            em.getTransaction().commit();
            log.info("setUp [1] Тестовые данные созданы");
            // Проверка промежуточной таблицы
            Long count = (Long) em.createNativeQuery(
                            "SELECT COUNT(*) FROM lab5_project_users WHERE project_id = :projectId AND user_id = :userId")
                    .setParameter("projectId", testProject.getId())
                    .setParameter("userId", testUser.getId())
                    .getSingleResult();
            log.debug("setUp [2] Количество записей в lab5_project_users: " + count);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать тестовые данные: " + e.getMessage()), e);
            em.getTransaction().rollback();
            fail("Не удалось создать тестовые данные: " + e.getMessage());
        } finally {
            em.close();
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Очистка после каждого теста.
     */
    @After
    public void tearDown() {
        String methodName = "tearDown";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        EntityManager em = DatabaseConfig.getLab5EntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("DELETE FROM lab5_project_users").executeUpdate();
            em.createQuery("DELETE FROM Task").executeUpdate();
            em.createQuery("DELETE FROM Sprint").executeUpdate();
            em.createQuery("DELETE FROM Project").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.getTransaction().commit();
            log.info("tearDown [1] Тестовые данные удалены");
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось очистить данные: " + e.getMessage()), e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Тестирование подсчета задач в проекте с использованием NativeSQL.
     * Тип: Позитивный
     */
    @Test
    public void testGetTaskCountByProjectNativeSQL() {
        String methodName = "testGetTaskCountByProjectNativeSQL";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetTaskCountByProjectNativeSQL [1] Выполнение NativeSQL запроса");
            Long count = summaryDAO.getTaskCountByProjectNativeSQL(testProject.getId());
            assertEquals("Должно быть найдено 2 задачи", 2L, count.longValue());
            log.info("testGetTaskCountByProjectNativeSQL [2] Тест пройден успешно");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить NativeSQL запрос: " + e.getMessage()), e);
            fail("Не удалось выполнить NativeSQL запрос: " + e.getMessage());
        }
    }

    /**
     * Тестирование подсчета задач в проекте с использованием HQL.
     * Тип: Позитивный
     */
    @Test
    public void testGetTaskCountByProjectHQL() {
        String methodName = "testGetTaskCountByProjectHQL";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetTaskCountByProjectHQL [1] Выполнение HQL запроса");
            Long count = summaryDAO.getTaskCountByProjectHQL(testProject.getId());
            assertEquals("Должно быть найдено 2 задачи", 2L, count.longValue());
            log.info("testGetTaskCountByProjectHQL [2] Тест пройден успешно");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить HQL запрос: " + e.getMessage()), e);
            fail("Не удалось выполнить HQL запрос: " + e.getMessage());
        }
    }

    /**
     * Тестирование подсчета задач в проекте с использованием Criteria API.
     * Тип: Позитивный
     */
    @Test
    public void testGetTaskCountByProjectCriteria() {
        String methodName = "testGetTaskCountByProjectCriteria";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetTaskCountByProjectCriteria [1] Выполнение Criteria запроса");
            Long count = summaryDAO.getTaskCountByProjectCriteria(testProject.getId());
            assertEquals("Должно быть найдено 2 задачи", 2L, count.longValue());
            log.info("testGetTaskCountByProjectCriteria [2] Тест пройден успешно");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить Criteria запрос: " + e.getMessage()), e);
            fail("Не удалось выполнить Criteria запрос: " + e.getMessage());
        }
    }

    /**
     * Тестирование подсчета пользователей в проекте с использованием NativeSQL.
     * Тип: Позитивный
     */
    @Test
    public void testGetUserCountByProjectNativeSQL() {
        String methodName = "testGetUserCountByProjectNativeSQL";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            EntityManager em = DatabaseConfig.getLab5EntityManager();
            Long countInJoinTable = (Long) em.createNativeQuery(
                            "SELECT COUNT(*) FROM lab5_project_users WHERE project_id = :projectId")
                    .setParameter("projectId", testProject.getId())
                    .getSingleResult();
            log.debug("testGetUserCountByProjectNativeSQL [1] Количество записей в lab5_project_users: " + countInJoinTable);
            em.close();

            log.info("testGetUserCountByProjectNativeSQL [2] Выполнение NativeSQL запроса");
            Long count = summaryDAO.getUserCountByProjectNativeSQL(testProject.getId());
            assertEquals("Должен быть найден 1 пользователь", 1L, count.longValue());
            log.info("testGetUserCountByProjectNativeSQL [3] Тест пройден успешно");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить NativeSQL запрос: " + e.getMessage()), e);
            fail("Не удалось выполнить NativeSQL запрос: " + e.getMessage());
        }
    }

    /**
     * Тестирование подсчета пользователей в проекте с использованием HQL.
     * Тип: Позитивный
     */
    @Test
    public void testGetUserCountByProjectHQL() {
        String methodName = "testGetUserCountByProjectHQL";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            EntityManager em = DatabaseConfig.getLab5EntityManager();
            Long countInJoinTable = (Long) em.createNativeQuery(
                            "SELECT COUNT(*) FROM lab5_project_users WHERE project_id = :projectId")
                    .setParameter("projectId", testProject.getId())
                    .getSingleResult();
            log.debug("testGetUserCountByProjectHQL [1] Количество записей в lab5_project_users: " + countInJoinTable);
            em.close();

            log.info("testGetUserCountByProjectHQL [2] Выполнение HQL запроса");
            Long count = summaryDAO.getUserCountByProjectHQL(testProject.getId());
            assertEquals("Должен быть найден 1 пользователь", 1L, count.longValue());
            log.info("testGetUserCountByProjectHQL [3] Тест пройден успешно");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить HQL запрос: " + e.getMessage()), e);
            fail("Не удалось выполнить HQL запрос: " + e.getMessage());
        }
    }

    /**
     * Тестирование подсчета пользователей в проекте с использованием Criteria API.
     * Тип: Позитивный
     */
    @Test
    public void testGetUserCountByProjectCriteria() {
        String methodName = "testGetUserCountByProjectCriteria";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            EntityManager em = DatabaseConfig.getLab5EntityManager();
            Long countInJoinTable = (Long) em.createNativeQuery(
                            "SELECT COUNT(*) FROM lab5_project_users WHERE project_id = :projectId")
                    .setParameter("projectId", testProject.getId())
                    .getSingleResult();
            log.debug("testGetUserCountByProjectCriteria [1] Количество записей в lab5_project_users: " + countInJoinTable);
            em.close();

            log.info("testGetUserCountByProjectCriteria [2] Выполнение Criteria запроса");
            Long count = summaryDAO.getUserCountByProjectCriteria(testProject.getId());
            assertEquals("Должен быть найден 1 пользователь", 1L, count.longValue());
            log.info("testGetUserCountByProjectCriteria [3] Тест пройден успешно");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить Criteria запрос: " + e.getMessage()), e);
            fail("Не удалось выполнить Criteria запрос: " + e.getMessage());
        }
    }

    /**
     * Тестирование подсчета спринтов в проекте с использованием NativeSQL.
     * Тип: Позитивный
     */
    @Test
    public void testGetSprintCountByProjectNativeSQL() {
        String methodName = "testGetSprintCountByProjectNativeSQL";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetSprintCountByProjectNativeSQL [1] Выполнение NativeSQL запроса");
            Long count = summaryDAO.getSprintCountByProjectNativeSQL(testProject.getId());
            assertEquals("Должен быть найден 1 спринт", 1L, count.longValue());
            log.info("testGetSprintCountByProjectNativeSQL [2] Тест пройден успешно");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить NativeSQL запрос: " + e.getMessage()), e);
            fail("Не удалось выполнить NativeSQL запрос: " + e.getMessage());
        }
    }

    /**
     * Тестирование подсчета спринтов в проекте с использованием HQL.
     * Тип: Позитивный
     */
    @Test
    public void testGetSprintCountByProjectHQL() {
        String methodName = "testGetSprintCountByProjectHQL";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetSprintCountByProjectHQL [1] Выполнение HQL запроса");
            Long count = summaryDAO.getSprintCountByProjectHQL(testProject.getId());
            assertEquals("Должен быть найден 1 спринт", 1L, count.longValue());
            log.info("testGetSprintCountByProjectHQL [2] Тест пройден успешно");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить HQL запрос: " + e.getMessage()), e);
            fail("Не удалось выполнить HQL запрос: " + e.getMessage());
        }
    }

    /**
     * Тестирование подсчета спринтов в проекте с использованием Criteria API.
     * Тип: Позитивный
     */
    @Test
    public void testGetSprintCountByProjectCriteria() {
        String methodName = "testGetSprintCountByProjectCriteria";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetSprintCountByProjectCriteria [1] Выполнение Criteria запроса");
            Long count = summaryDAO.getSprintCountByProjectCriteria(testProject.getId());
            assertEquals("Должен быть найден 1 спринт", 1L, count.longValue());
            log.info("testGetSprintCountByProjectCriteria [2] Тест пройден успешно");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить Criteria запрос: " + e.getMessage()), e);
            fail("Не удалось выполнить Criteria запрос: " + e.getMessage());
        }
    }

    /**
     * Тестирование анализа производительности запросов подсчета задач.
     * Тип: Позитивный
     */
    @Test
    public void testAnalyzeTaskCountPerformance() {
        String methodName = "testAnalyzeTaskCountPerformance";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testAnalyzeTaskCountPerformance [1] Выполнение анализа производительности");
            Map<String, Long> results = summaryDAO.analyzeTaskCountPerformance(testProject.getId(), 10);
            assertNotNull("Результаты не должны быть null", results);
            assertTrue("Должен содержать результат NativeSQL", results.containsKey("NativeSQL"));
            assertTrue("Должен содержать результат HQL", results.containsKey("HQL"));
            assertTrue("Должен содержать результат Criteria", results.containsKey("Criteria"));
            log.info("testAnalyzeTaskCountPerformance [2] Результаты: " + results);
            log.info("testAnalyzeTaskCountPerformance [3] Тест пройден успешно");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить анализ производительности: " + e.getMessage()), e);
            fail("Не удалось выполнить анализ производительности: " + e.getMessage());
        }
    }

    /**
     * Тестирование подсчета задач для несуществующего проекта.
     * Тип: Негативный
     */
    @Test
    public void testGetTaskCountByNonExistentProject() {
        String methodName = "testGetTaskCountByNonExistentProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetTaskCountByNonExistentProject [1] Выполнение запросов для несуществующего проекта");
            Long countNative = summaryDAO.getTaskCountByProjectNativeSQL(999);
            Long countHQL = summaryDAO.getTaskCountByProjectHQL(999);
            Long countCriteria = summaryDAO.getTaskCountByProjectCriteria(999);
            assertEquals("Должно быть 0 задач для NativeSQL", 0L, countNative.longValue());
            assertEquals("Должно быть 0 задач для HQL", 0L, countHQL.longValue());
            assertEquals("Должно быть 0 задач для Criteria", 0L, countCriteria.longValue());
            log.info("testGetTaskCountByNonExistentProject [2] Тест пройден успешно");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить запросы: " + e.getMessage()), e);
            fail("Не удалось выполнить запросы: " + e.getMessage());
        }
    }
}