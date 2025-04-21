package ru.sfedu.agileflow.xml;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.config.XmlConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Тестовый класс для проверки операций XML DAO с проектами.
 */
public class ProjectXmlDAOTest {
    private static final Logger log = Logger.getLogger(ProjectXmlDAOTest.class);
    private ProjectXmlDAO projectDAO;

    /**
     * Подготовка перед каждым тестом.
     */
    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        projectDAO = new ProjectXmlDAO();
        log.info("setUp [1] Инициализация ProjectXmlDAO завершена");
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
            File file = new File(XmlConfig.getFilePath(Project.class));
            if (file.exists()) {
                file.delete();
            }
            log.info("tearDown [1] Все проекты удалены, XML файл очищен");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось очистить данные: " + e.getMessage()), e);
        }
    }

    /**
     * Тестирование создания проекта.
     * Тип: Позитивный
     */
    @Test
    public void testCreateProject() {
        String methodName = "testCreateProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание тестового проекта");
            log.info("testCreateProject [1] Создание проекта");
            projectDAO.create(project);
            assertNotNull("Идентификатор проекта должен быть установлен", project.getId());
            log.info("testCreateProject [2] Проект успешно создан с ID: " + project.getId());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать проект: " + e.getMessage()), e);
            fail("Не удалось создать проект: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска проекта по идентификатору.
     * Тип: Позитивный
     */
    @Test
    public void testFindById() {
        String methodName = "testFindById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            log.info("testFindById [1] Создание проекта");
            projectDAO.create(project);
            Integer id = project.getId();

            log.info("testFindById [2] Поиск проекта по ID: " + id);
            Optional<Project> found = projectDAO.findById(id);
            assertTrue("Проект должен быть найден", found.isPresent());
            assertEquals("Название проекта должно совпадать", "Тестовый проект", found.get().getName());
            log.info("testFindById [3] Проект успешно найден");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти проект: " + e.getMessage()), e);
            fail("Не удалось найти проект: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска несуществующего проекта.
     * Тип: Негативный
     */
    @Test
    public void testFindByIdNotFound() {
        String methodName = "testFindByIdNotFound";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testFindByIdNotFound [1] Поиск проекта с ID: 999");
            Optional<Project> found = projectDAO.findById(999);
            assertFalse("Проект не должен быть найден", found.isPresent());
            log.info("testFindByIdNotFound [2] Проект не найден, как ожидалось");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить поиск: " + e.getMessage()), e);
            fail("Не удалось выполнить поиск: " + e.getMessage());
        }
    }

    /**
     * Тестирование получения списка всех проектов.
     * Тип: Позитивный
     */
    @Test
    public void testFindAll() {
        String methodName = "testFindAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project1 = new Project("Проект 1", "Описание 1");
            Project project2 = new Project("Проект 2", "Описание 2");
            log.info("testFindAll [1] Создание двух проектов");
            projectDAO.create(project1);
            projectDAO.create(project2);

            log.info("testFindAll [2] Получение списка всех проектов");
            List<Project> projects = projectDAO.findAll();
            assertEquals("Должно быть найдено 2 проекта", 2, projects.size());
            log.info("testFindAll [3] Найдено проектов: " + projects.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить список проектов: " + e.getMessage()), e);
            fail("Не удалось получить список проектов: " + e.getMessage());
        }
    }

    /**
     * Тестирование обновления проекта.
     * Тип: Позитивный
     */
    @Test
    public void testUpdateProject() {
        String methodName = "testUpdateProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            log.info("testUpdateProject [1] Создание проекта");
            projectDAO.create(project);
            Integer id = project.getId();

            project.setName("Обновленный проект");
            project.setDescription("Новое описание");
            log.info("testUpdateProject [2] Обновление проекта");
            projectDAO.update(project);

            log.info("testUpdateProject [3] Проверка обновленного проекта");
            Optional<Project> updated = projectDAO.findById(id);
            assertTrue("Проект должен быть найден", updated.isPresent());
            assertEquals("Название должно быть обновлено", "Обновленный проект", updated.get().getName());
            assertEquals("Описание должно быть обновлено", "Новое описание", updated.get().getDescription());
            log.info("testUpdateProject [4] Проект успешно обновлен");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить проект: " + e.getMessage()), e);
            fail("Не удалось обновить проект: " + e.getMessage());
        }
    }

    /**
     * Тестирование удаления проекта.
     * Тип: Позитивный
     */
    @Test
    public void testDeleteProject() {
        String methodName = "testDeleteProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            log.info("testDeleteProject [1] Создание проекта");
            projectDAO.create(project);
            Integer id = project.getId();

            log.info("testDeleteProject [2] Удаление проекта с ID: " + id);
            projectDAO.delete(id);

            log.info("testDeleteProject [3] Проверка удаления");
            Optional<Project> deleted = projectDAO.findById(id);
            assertFalse("Проект не должен быть найден", deleted.isPresent());
            log.info("testDeleteProject [4] Проект успешно удален");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить проект: " + e.getMessage()), e);
            fail("Не удалось удалить проект: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска проектов по имени.
     * Тип: Позитивный
     */
    @Test
    public void testFindByName() {
        String methodName = "testFindByName";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project1 = new Project("Тестовый проект 1", "Описание 1");
            Project project2 = new Project("Тестовый проект 2", "Описание 2");
            log.info("testFindByName [1] Создание двух проектов");
            projectDAO.create(project1);
            projectDAO.create(project2);

            log.info("testFindByName [2] Поиск проектов по имени 'Тестовый'");
            List<Project> projects = projectDAO.findByName("Тестовый");
            assertEquals("Должно быть найдено 2 проекта", 2, projects.size());
            log.info("testFindByName [3] Найдено проектов: " + projects.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти проекты: " + e.getMessage()), e);
            fail("Не удалось найти проекты: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска проектов по несуществующему имени.
     * Тип: Негативный
     */
    @Test
    public void testFindByNameNotFound() {
        String methodName = "testFindByNameNotFound";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testFindByNameNotFound [1] Поиск проектов по имени 'Несуществующий'");
            List<Project> projects = projectDAO.findByName("Несуществующий");
            assertTrue("Список проектов должен быть пуст", projects.isEmpty());
            log.info("testFindByNameNotFound [2] Проекты не найдены, как ожидалось");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить поиск: " + e.getMessage()), e);
            fail("Не удалось выполнить поиск: " + e.getMessage());
        }
    }
}