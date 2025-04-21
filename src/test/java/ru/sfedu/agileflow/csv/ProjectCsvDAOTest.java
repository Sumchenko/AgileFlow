package ru.sfedu.agileflow.csv;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Тестовый класс для ProjectCsvDAO.
 */
public class ProjectCsvDAOTest {
    private static final Logger log = Logger.getLogger(ProjectCsvDAOTest.class);
    private ProjectCsvDAO projectDAO;
    private static final String CSV_DIR = "data/csv";

    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        projectDAO = new ProjectCsvDAO();
        // Очистка директории перед тестом
        try {
            Files.deleteIfExists(Paths.get(CSV_DIR, "projects.csv"));
            log.info("setUp [1] CSV-файл очищен");
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
            Files.deleteIfExists(Paths.get(CSV_DIR, "projects.csv"));
            log.info("tearDown [1] CSV-файл удален");
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить CSV: " + e.getMessage()), e);
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    @Test
    public void testCreateProject() {
        String methodName = "testCreateProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            assertNotNull("Идентификатор должен быть установлен", project.getId());
            log.info("testCreateProject [1] Проект создан с ID: " + project.getId());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать проект: " + e.getMessage()), e);
            fail("Не удалось создать проект: " + e.getMessage());
        }
    }

    @Test
    public void testFindById() {
        String methodName = "testFindById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Optional<Project> found = projectDAO.findById(project.getId());
            assertTrue("Проект должен быть найден", found.isPresent());
            assertEquals("Название должно совпадать", "Тестовый проект", found.get().getName());
            log.info("testFindById [1] Проект найден");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти проект: " + e.getMessage()), e);
            fail("Не удалось найти проект: " + e.getMessage());
        }
    }

    @Test
    public void testFindAll() {
        String methodName = "testFindAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            projectDAO.create(new Project("Проект 1", "Описание 1"));
            projectDAO.create(new Project("Проект 2", "Описание 2"));
            List<Project> projects = projectDAO.findAll();
            assertEquals("Должно быть 2 проекта", 2, projects.size());
            log.info("testFindAll [1] Найдено проектов: " + projects.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить проекты: " + e.getMessage()), e);
            fail("Не удалось получить проекты: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateProject() {
        String methodName = "testUpdateProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            project.setName("Обновленный проект");
            projectDAO.update(project);
            Optional<Project> updated = projectDAO.findById(project.getId());
            assertTrue("Проект должен быть найден", updated.isPresent());
            assertEquals("Название должно быть обновлено", "Обновленный проект", updated.get().getName());
            log.info("testUpdateProject [1] Проект обновлен");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить проект: " + e.getMessage()), e);
            fail("Не удалось обновить проект: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteProject() {
        String methodName = "testDeleteProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            projectDAO.delete(project.getId());
            Optional<Project> deleted = projectDAO.findById(project.getId());
            assertFalse("Проект не должен быть найден", deleted.isPresent());
            log.info("testDeleteProject [1] Проект удален");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить проект: " + e.getMessage()), e);
            fail("Не удалось удалить проект: " + e.getMessage());
        }
    }
}