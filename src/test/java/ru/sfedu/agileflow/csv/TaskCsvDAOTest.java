package ru.sfedu.agileflow.csv;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.Sprint;
import ru.sfedu.agileflow.models.Task;
import ru.sfedu.agileflow.models.TaskStatus;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Тестовый класс для TaskCsvDAO.
 */
public class TaskCsvDAOTest {
    private static final Logger log = Logger.getLogger(TaskCsvDAOTest.class);
    private TaskCsvDAO taskDAO;
    private SprintCsvDAO sprintDAO;
    private ProjectCsvDAO projectDAO;
    private static final String CSV_DIR = "src/main/resources/dataCSV";

    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        taskDAO = new TaskCsvDAO();
        sprintDAO = new SprintCsvDAO();
        projectDAO = new ProjectCsvDAO();
        try {
            Files.deleteIfExists(Paths.get(CSV_DIR, "tasks.csv"));
            Files.deleteIfExists(Paths.get(CSV_DIR, "sprints.csv"));
            Files.deleteIfExists(Paths.get(CSV_DIR, "projects.csv"));
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
            Files.deleteIfExists(Paths.get(CSV_DIR, "tasks.csv"));
            Files.deleteIfExists(Paths.get(CSV_DIR, "sprints.csv"));
            Files.deleteIfExists(Paths.get(CSV_DIR, "projects.csv"));
            log.info("tearDown [1] CSV-файлы удалены");
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить CSV: " + e.getMessage()), e);
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    @Test
    public void testCreateTask() {
        String methodName = "testCreateTask";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(System.currentTimeMillis() + 86400000), project);
            sprintDAO.create(sprint);
            Task task = new Task("Тестовая задача", "Описание", TaskStatus.TO_DO, 1, sprint, null);
            taskDAO.create(task);
            assertNotNull("Идентификатор должен быть установлен", task.getId());
            log.info("testCreateTask [1] Задача создана с ID: " + task.getId());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать задачу: " + e.getMessage()), e);
            fail("Не удалось создать задачу: " + e.getMessage());
        }
    }

    @Test
    public void testFindById() {
        String methodName = "testFindById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(System.currentTimeMillis() + 86400000), project);
            sprintDAO.create(sprint);
            Task task = new Task("Тестовая задача", "Описание", TaskStatus.TO_DO, 1, sprint, null);
            taskDAO.create(task);
            Optional<Task> found = taskDAO.findById(task.getId());
            assertTrue("Задача должна быть найдена", found.isPresent());
            assertEquals("Название должно совпадать", "Тестовая задача", found.get().getTitle());
            log.info("testFindById [1] Задача найдена");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти задачу: " + e.getMessage()), e);
            fail("Не удалось найти задачу: " + e.getMessage());
        }
    }

    @Test
    public void testFindAll() {
        String methodName = "testFindAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(System.currentTimeMillis() + 86400000), project);
            sprintDAO.create(sprint);
            taskDAO.create(new Task("Задача 1", "Описание 1", TaskStatus.TO_DO, 1, sprint, null));
            taskDAO.create(new Task("Задача 2", "Описание 2", TaskStatus.IN_PROGRESS, 2, sprint, null));
            List<Task> tasks = taskDAO.findAll();
            assertEquals("Должно быть 2 задачи", 2, tasks.size());
            log.info("testFindAll [1] Найдено задач: " + tasks.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить задачи: " + e.getMessage()), e);
            fail("Не удалось получить задачи: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateTask() {
        String methodName = "testUpdateTask";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(System.currentTimeMillis() + 86400000), project);
            sprintDAO.create(sprint);
            Task task = new Task("Тестовая задача", "Описание", TaskStatus.TO_DO, 1, sprint, null);
            taskDAO.create(task);
            task.setStatus(TaskStatus.DONE);
            taskDAO.update(task);
            Optional<Task> updated = taskDAO.findById(task.getId());
            assertTrue("Задача должна быть найдена", updated.isPresent());
            assertEquals("Статус должен быть обновлен", TaskStatus.DONE, updated.get().getStatus());
            log.info("testUpdateTask [1] Задача обновлена");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить задачу: " + e.getMessage()), e);
            fail("Не удалось обновить задачу: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteTask() {
        String methodName = "testDeleteTask";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(System.currentTimeMillis() + 86400000), project);
            sprintDAO.create(sprint);
            Task task = new Task("Тестовая задача", "Описание", TaskStatus.TO_DO, 1, sprint, null);
            taskDAO.create(task);
            taskDAO.delete(task.getId());
            Optional<Task> deleted = taskDAO.findById(task.getId());
            assertFalse("Задача не должна быть найдена", deleted.isPresent());
            log.info("testDeleteTask [1] Задача удалена");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить задачу: " + e.getMessage()), e);
            fail("Не удалось удалить задачу: " + e.getMessage());
        }
    }
}
