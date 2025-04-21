package ru.sfedu.agileflow.csv;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.User;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Тестовый класс для ProjectUserCsvDAO.
 */
public class ProjectUserCsvDAOTest {
    private static final Logger log = Logger.getLogger(ProjectUserCsvDAOTest.class);
    private ProjectUserCsvDAO projectUserDAO;
    private ProjectCsvDAO projectDAO;
    private UserCsvDAO userDAO;
    private static final String CSV_DIR = "src/main/resources/dataCSV";

    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        projectUserDAO = new ProjectUserCsvDAO();
        projectDAO = new ProjectCsvDAO();
        userDAO = new UserCsvDAO();
        try {
            Files.deleteIfExists(Paths.get(CSV_DIR, "project_users.csv"));
            Files.deleteIfExists(Paths.get(CSV_DIR, "projects.csv"));
            Files.deleteIfExists(Paths.get(CSV_DIR, "users.csv"));
            log.info("setUp [1] CSV-файлы очищены");
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось очистить CSV: " + e.getMessage()), e);
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    @After
    public void tearDown() {
        String methodName = "tearDown";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Files.deleteIfExists(Paths.get(CSV_DIR, "project_users.csv"));
            Files.deleteIfExists(Paths.get(CSV_DIR, "projects.csv"));
            Files.deleteIfExists(Paths.get(CSV_DIR, "users.csv"));
            log.info("tearDown [1] CSV-файлы удалены");
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить CSV: " + e.getMessage()), e);
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    @Test
    public void testAddUserToProject() {
        String methodName = "testAddUserToProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            User user = new User(Constants.TEST_USER_NAME, Constants.TEST_USER_EMAIL, "Био", true, new Date());
            userDAO.create(user);
            projectUserDAO.addUserToProject(project.getId(), user.getId());
            List<User> users = projectUserDAO.getUsersByProject(project.getId());
            assertEquals("Должен быть 1 пользователь в проекте", 1, users.size());
            assertEquals("Email должен совпадать", Constants.TEST_USER_EMAIL, users.get(0).getEmail());
            log.info("testAddUserToProject [1] Пользователь добавлен в проект");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось добавить пользователя в проект: " + e.getMessage()), e);
            fail("Не удалось добавить пользователя в проект: " + e.getMessage());
        }
    }

    @Test
    public void testRemoveUserFromProject() {
        String methodName = "testRemoveUserFromProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            User user = new User(Constants.TEST_USER_NAME, Constants.TEST_USER_EMAIL, "Био", true, new Date());
            userDAO.create(user);
            projectUserDAO.addUserToProject(project.getId(), user.getId());
            projectUserDAO.removeUserFromProject(project.getId(), user.getId());
            List<User> users = projectUserDAO.getUsersByProject(project.getId());
            assertEquals("Пользователей в проекте не должно быть", 0, users.size());
            log.info("testRemoveUserFromProject [1] Пользователь удален из проекта");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить пользователя из проекта: " + e.getMessage()), e);
            fail("Не удалось удалить пользователя из проекта: " + e.getMessage());
        }
    }

    @Test
    public void testGetUsersByProject() {
        String methodName = "testGetUsersByProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            User user1 = new User("Пользователь 1", "user1@example.com", "Био1", true, new Date());
            User user2 = new User("Пользователь 2", "user2@example.com", "Био2", true, new Date());
            userDAO.create(user1);
            userDAO.create(user2);
            projectUserDAO.addUserToProject(project.getId(), user1.getId());
            projectUserDAO.addUserToProject(project.getId(), user2.getId());
            List<User> users = projectUserDAO.getUsersByProject(project.getId());
            assertEquals("Должно быть 2 пользователя в проекте", 2, users.size());
            log.info("testGetUsersByProject [1] Найдено пользователей: " + users.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить пользователей проекта: " + e.getMessage()), e);
            fail("Не удалось получить пользователей проекта: " + e.getMessage());
        }
    }

    @Test
    public void testGetProjectsByUser() {
        String methodName = "testGetProjectsByUser";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project1 = new Project("Проект 1", "Описание 1");
            Project project2 = new Project("Проект 2", "Описание 2");
            projectDAO.create(project1);
            projectDAO.create(project2);
            User user = new User(Constants.TEST_USER_NAME, Constants.TEST_USER_EMAIL, "Био", true, new Date());
            userDAO.create(user);
            projectUserDAO.addUserToProject(project1.getId(), user.getId());
            projectUserDAO.addUserToProject(project2.getId(), user.getId());
            List<Project> projects = projectUserDAO.getProjectsByUser(user.getId());
            assertEquals("Должно быть 2 проекта для пользователя", 2, projects.size());
            log.info("testGetProjectsByUser [1] Найдено проектов: " + projects.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить проекты пользователя: " + e.getMessage()), e);
            fail("Не удалось получить проекты пользователя: " + e.getMessage());
        }
    }
}
