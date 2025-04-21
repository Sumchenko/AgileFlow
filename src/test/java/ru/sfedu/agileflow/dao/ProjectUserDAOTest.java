package ru.sfedu.agileflow.dao;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.User;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Тестовый класс для проверки операций DAO с связями проектов и пользователей.
 */
public class ProjectUserDAOTest {
    private static final Logger log = Logger.getLogger(ProjectUserDAOTest.class);
    private ProjectUserDAO projectUserDAO;
    private ProjectDAO projectDAO;
    private UserDAO userDAO;

    /**
     * Подготовка перед каждым тестом.
     */
    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        projectUserDAO = new ProjectUserDAO();
        projectDAO = new ProjectDAO();
        userDAO = new UserDAO();
        log.info("setUp [1] Инициализация DAO завершена");
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Очистка после каждого теста.
     */
    @After
    public void tearDown() {
        String methodName = "tearDown";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            List<Project> projects = projectDAO.findAll();
            for (Project project : projects) {
                projectDAO.delete(project.getId());
            }
            List<User> users = userDAO.findAll();
            for (User user : users) {
                userDAO.delete(user.getId());
            }
            log.info("tearDown [1] Все проекты и пользователи удалены");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось очистить данные: " + e.getMessage()), e);
        }
    }

    /**
     * Тестирование добавления пользователя в проект.
     * Тип: Позитивный
     */
    @Test
    public void testAddUserToProject() {
        String methodName = "testAddUserToProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user = new User("Тестовый пользователь", "test@example.com", "Био", true, new Date());
            Project project = new Project("Тестовый проект", "Описание");
            log.info("testAddUserToProject [1] Создание пользователя и проекта");
            userDAO.create(user);
            projectDAO.create(project);

            log.info("testAddUserToProject [2] Добавление пользователя в проект");
            projectUserDAO.addUserToProject(project.getId(), user.getId());

            log.info("testAddUserToProject [3] Проверка связи");
            List<User> users = projectUserDAO.getUsersByProject(project.getId());
            assertEquals("Должен быть 1 пользователь в проекте", 1, users.size());
            assertEquals("Email пользователя должен совпадать", "test@example.com", users.get(0).getEmail());
            log.info("testAddUserToProject [4] Пользователь успешно добавлен в проект");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось добавить пользователя в проект: " + e.getMessage()), e);
            fail("Не удалось добавить пользователя в проект: " + e.getMessage());
        }
    }

    /**
     * Тестирование добавления пользователя в несуществующий проект.
     * Тип: Негативный
     */
    @Test
    public void testAddUserToNonExistentProject() {
        String methodName = "testAddUserToNonExistentProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user = new User("Тестовый пользователь", "test@example.com", "Био", true, new Date());
            log.info("testAddUserToNonExistentProject [1] Создание пользователя");
            userDAO.create(user);

            log.info("testAddUserToNonExistentProject [2] Попытка добавить пользователя в проект с ID: 999");
            projectUserDAO.addUserToProject(999, user.getId());

            List<User> users = projectUserDAO.getUsersByProject(999);
            assertEquals("Список пользователей должен быть пустым", 0, users.size());
            log.info("testAddUserToNonExistentProject [3] Пользователь не добавлен, как ожидалось");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить тест: " + e.getMessage()), e);
            fail("Не удалось выполнить тест: " + e.getMessage());
        }
    }

    /**
     * Тестирование удаления пользователя из проекта.
     * Тип: Позитивный
     */
    @Test
    public void testRemoveUserFromProject() {
        String methodName = "testRemoveUserFromProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user = new User("Тестовый пользователь", "test@example.com", "Био", true, new Date());
            Project project = new Project("Тестовый проект", "Описание");
            log.info("testRemoveUserFromProject [1] Создание пользователя и проекта");
            userDAO.create(user);
            projectDAO.create(project);

            log.info("testRemoveUserFromProject [2] Добавление пользователя в проект");
            projectUserDAO.addUserToProject(project.getId(), user.getId());

            log.info("testRemoveUserFromProject [3] Удаление пользователя из проекта");
            projectUserDAO.removeUserFromProject(project.getId(), user.getId());

            log.info("testRemoveUserFromProject [4] Проверка удаления");
            List<User> users = projectUserDAO.getUsersByProject(project.getId());
            assertEquals("Список пользователей должен быть пустым", 0, users.size());
            log.info("testRemoveUserFromProject [5] Пользователь успешно удален из проекта");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить пользователя из проекта: " + e.getMessage()), e);
            fail("Не удалось удалить пользователя из проекта: " + e.getMessage());
        }
    }

    /**
     * Тестирование получения пользователей проекта.
     * Тип: Позитивный
     */
    @Test
    public void testGetUsersByProject() {
        String methodName = "testGetUsersByProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user1 = new User("Пользователь 1", "user1@example.com", "Био 1", true, new Date());
            User user2 = new User("Пользователь 2", "user2@example.com", "Био 2", true, new Date());
            Project project = new Project("Тестовый проект", "Описание");
            log.info("testGetUsersByProject [1] Создание пользователей и проекта");
            userDAO.create(user1);
            userDAO.create(user2);
            projectDAO.create(project);

            log.info("testGetUsersByProject [2] Добавление пользователей в проект");
            projectUserDAO.addUserToProject(project.getId(), user1.getId());
            projectUserDAO.addUserToProject(project.getId(), user2.getId());

            log.info("testGetUsersByProject [3] Получение списка пользователей");
            List<User> users = projectUserDAO.getUsersByProject(project.getId());
            assertEquals("Должно быть найдено 2 пользователя", 2, users.size());
            log.info("testGetUsersByProject [4] Найдено пользователей: " + users.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить пользователей проекта: " + e.getMessage()), e);
            fail("Не удалось получить пользователей проекта: " + e.getMessage());
        }
    }

    /**
     * Тестирование получения проектов пользователя.
     * Тип: Позитивный
     */
    @Test
    public void testGetProjectsByUser() {
        String methodName = "testGetProjectsByUser";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user = new User("Тестовый пользователь", "test@example.com", "Био", true, new Date());
            Project project1 = new Project("Проект 1", "Описание 1");
            Project project2 = new Project("Проект 2", "Описание 2");
            log.info("testGetProjectsByUser [1] Создание пользователя и проектов");
            userDAO.create(user);
            projectDAO.create(project1);
            projectDAO.create(project2);

            log.info("testGetProjectsByUser [2] Добавление пользователя в проекты");
            projectUserDAO.addUserToProject(project1.getId(), user.getId());
            projectUserDAO.addUserToProject(project2.getId(), user.getId());

            log.info("testGetProjectsByUser [3] Получение списка проектов");
            List<Project> projects = projectUserDAO.getProjectsByUser(user.getId());
            assertEquals("Должно быть найдено 2 проекта", 2, projects.size());
            log.info("testGetProjectsByUser [4] Найдено проектов: " + projects.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить проекты пользователя: " + e.getMessage()), e);
            fail("Не удалось получить проекты пользователя: " + e.getMessage());
        }
    }
}