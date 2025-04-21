package ru.sfedu.agileflow.xml;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.config.XmlConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Тестовый класс для проверки операций XML DAO со связями проектов и пользователей.
 */
public class ProjectUserXmlDAOTest {
    private static final Logger log = Logger.getLogger(ProjectUserXmlDAOTest.class);
    private ProjectUserXmlDAO projectUserDAO;
    private ProjectXmlDAO projectDAO;
    private UserXmlDAO userDAO;

    /**
     * Подготовка перед каждым тестом.
     */
    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        projectUserDAO = new ProjectUserXmlDAO();
        projectDAO = new ProjectXmlDAO();
        userDAO = new UserXmlDAO();
        log.info("setUp [1] Инициализация DAO классов завершена");
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
            File projectFile = new File(XmlConfig.getFilePath(Project.class));
            File userFile = new File(XmlConfig.getFilePath(User.class));
            if (projectFile.exists()) {
                projectFile.delete();
            }
            if (userFile.exists()) {
                userFile.delete();
            }
            log.info("tearDown [1] Все проекты и пользователи удалены, XML файлы очищены");
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
            Project project = new Project("Тестовый проект", "Описание");
            project.setUsers(new ArrayList<>());
            projectDAO.create(project);
            User user = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            userDAO.create(user);
            log.info("testAddUserToProject [1] Создание проекта и пользователя");

            log.info("testAddUserToProject [2] Добавление пользователя в проект");
            projectUserDAO.addUserToProject(project.getId(), user.getId());

            log.info("testAddUserToProject [3] Проверка добавления");
            List<User> users = projectUserDAO.getUsersByProject(project.getId());
            assertEquals("Должен быть добавлен 1 пользователь", 1, users.size());
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
            User user = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            userDAO.create(user);
            log.info("testAddUserToNonExistentProject [1] Создание пользователя");

            log.info("testAddUserToNonExistentProject [2] Попытка добавления пользователя в несуществующий проект");
            projectUserDAO.addUserToProject(999, user.getId());
            fail("Должно быть выброшено исключение");
        } catch (RuntimeException e) {
            log.info("testAddUserToNonExistentProject [3] Исключение выброшено, как ожидалось: " + e.getMessage());
            assertTrue("Сообщение об ошибке должно содержать информацию о проекте", e.getCause().getMessage().contains("Проект с ID 999 не найден"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Неожиданная ошибка: " + e.getMessage()), e);
            fail("Неожиданная ошибка: " + e.getMessage());
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
            Project project = new Project("Тестовый проект", "Описание");
            project.setUsers(new ArrayList<>());
            projectDAO.create(project);
            User user = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            userDAO.create(user);
            projectUserDAO.addUserToProject(project.getId(), user.getId());
            log.info("testRemoveUserFromProject [1] Создание проекта, пользователя и добавление в проект");

            log.info("testRemoveUserFromProject [2] Удаление пользователя из проекта");
            projectUserDAO.removeUserFromProject(project.getId(), user.getId());

            log.info("testRemoveUserFromProject [3] Проверка удаления");
            List<User> users = projectUserDAO.getUsersByProject(project.getId());
            assertTrue("Список пользователей должен быть пуст", users.isEmpty());
            log.info("testRemoveUserFromProject [4] Пользователь успешно удален из проекта");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить пользователя из проекта: " + e.getMessage()), e);
            fail("Не удалось удалить пользователя из проекта: " + e.getMessage());
        }
    }

    /**
     * Тестирование удаления пользователя из несуществующего проекта.
     * Тип: Негативный
     */
    @Test
    public void testRemoveUserFromNonExistentProject() {
        String methodName = "testRemoveUserFromNonExistentProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            User user = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            userDAO.create(user);
            log.info("testRemoveUserFromNonExistentProject [1] Создание пользователя");

            log.info("testRemoveUserFromNonExistentProject [2] Попытка удаления пользователя из несуществующего проекта");
            projectUserDAO.removeUserFromProject(999, user.getId());
            fail("Должно быть выброшено исключение");
        } catch (RuntimeException e) {
            log.info("testRemoveUserFromNonExistentProject [3] Исключение выброшено, как ожидалось: " + e.getMessage());
            assertTrue("Сообщение об ошибке должно содержать информацию о проекте", e.getCause().getMessage().contains("Проект с ID 999 не найден"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Неожиданная ошибка: " + e.getMessage()), e);
            fail("Неожиданная ошибка: " + e.getMessage());
        }
    }

    /**
     * Тестирование получения списка пользователей проекта.
     * Тип: Позитивный
     */
    @Test
    public void testGetUsersByProject() {
        String methodName = "testGetUsersByProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            project.setUsers(new ArrayList<>());
            projectDAO.create(project);
            User user1 = new User("Пользователь 1", "user1@example.com", "Биография 1", true, new Date());
            User user2 = new User("Пользователь 2", "user2@example.com", "Биография 2", true, new Date());
            userDAO.create(user1);
            userDAO.create(user2);
            projectUserDAO.addUserToProject(project.getId(), user1.getId());
            projectUserDAO.addUserToProject(project.getId(), user2.getId());
            log.info("testGetUsersByProject [1] Создание проекта и добавление двух пользователей");

            log.info("testGetUsersByProject [2] Получение списка пользователей проекта");
            List<User> users = projectUserDAO.getUsersByProject(project.getId());
            assertEquals("Должно быть найдено 2 пользователя", 2, users.size());
            assertTrue("Список должен содержать user1@example.com", users.stream().anyMatch(u -> u.getEmail().equals("user1@example.com")));
            assertTrue("Список должен содержать user2@example.com", users.stream().anyMatch(u -> u.getEmail().equals("user2@example.com")));
            log.info("testGetUsersByProject [3] Пользователи успешно найдены");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить пользователей проекта: " + e.getMessage()), e);
            fail("Не удалось получить пользователей проекта: " + e.getMessage());
        }
    }

    /**
     * Тестирование получения списка пользователей несуществующего проекта.
     * Тип: Негативный
     */
    @Test
    public void testGetUsersByNonExistentProject() {
        String methodName = "testGetUsersByNonExistentProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetUsersByNonExistentProject [1] Попытка получения пользователей несуществующего проекта");
            projectUserDAO.getUsersByProject(999);
            fail("Должно быть выброшено исключение");
        } catch (RuntimeException e) {
            log.info("testGetUsersByNonExistentProject [2] Исключение выброшено, как ожидалось: " + e.getMessage());
            assertTrue("Сообщение об ошибке должно содержать информацию о проекте", e.getCause().getMessage().contains("Проект с ID 999 не найден"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Неожиданная ошибка: " + e.getMessage()), e);
            fail("Неожиданная ошибка: " + e.getMessage());
        }
    }

    /**
     * Тестирование получения списка проектов пользователя.
     * Тип: Позитивный
     */
    @Test
    public void testGetProjectsByUser() {
        String methodName = "testGetProjectsByUser";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project1 = new Project("Проект 1", "Описание 1");
            Project project2 = new Project("Проект 2", "Описание 2");
            project1.setUsers(new ArrayList<>());
            project2.setUsers(new ArrayList<>());
            projectDAO.create(project1);
            projectDAO.create(project2);
            User user = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            userDAO.create(user);
            projectUserDAO.addUserToProject(project1.getId(), user.getId());
            projectUserDAO.addUserToProject(project2.getId(), user.getId());
            log.info("testGetProjectsByUser [1] Создание двух проектов и добавление пользователя");

            log.info("testGetProjectsByUser [2] Получение списка проектов пользователя");
            List<Project> projects = projectUserDAO.getProjectsByUser(user.getId());
            assertEquals("Должно быть найдено 2 проекта", 2, projects.size());
            assertTrue("Список должен содержать Проект 1", projects.stream().anyMatch(p -> p.getName().equals("Проект 1")));
            assertTrue("Список должен содержать Проект 2", projects.stream().anyMatch(p -> p.getName().equals("Проект 2")));
            log.info("testGetProjectsByUser [3] Проекты успешно найдены");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить проекты пользователя: " + e.getMessage()), e);
            fail("Не удалось получить проекты пользователя: " + e.getMessage());
        }
    }

    /**
     * Тестирование получения списка проектов несуществующего пользователя.
     * Тип: Негативный
     */
    @Test
    public void testGetProjectsByNonExistentUser() {
        String methodName = "testGetProjectsByNonExistentUser";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testGetProjectsByNonExistentUser [1] Попытка получения проектов несуществующего пользователя");
            projectUserDAO.getProjectsByUser(999);
            fail("Должно быть выброшено исключение");
        } catch (RuntimeException e) {
            log.info("testGetProjectsByNonExistentUser [2] Исключение выброшено, как ожидалось: " + e.getMessage());
            assertTrue("Сообщение об ошибке должно содержать информацию о пользователе", e.getCause().getMessage().contains("Пользователь с ID 999 не найден"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Неожиданная ошибка: " + e.getMessage()), e);
            fail("Неожиданная ошибка: " + e.getMessage());
        }
    }
}