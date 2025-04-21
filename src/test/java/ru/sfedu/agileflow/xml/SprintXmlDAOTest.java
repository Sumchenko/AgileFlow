package ru.sfedu.agileflow.xml;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.config.XmlConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.Sprint;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Тестовый класс для проверки операций XML DAO со спринтами.
 */
public class SprintXmlDAOTest {
    private static final Logger log = Logger.getLogger(SprintXmlDAOTest.class);
    private SprintXmlDAO sprintDAO;
    private ProjectXmlDAO projectDAO;

    /**
     * Подготовка перед каждым тестом.
     */
    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        sprintDAO = new SprintXmlDAO();
        projectDAO = new ProjectXmlDAO();
        log.info("setUp [1] Инициализация SprintXmlDAO и ProjectXmlDAO завершена");
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
            List<Sprint> sprints = sprintDAO.findAll();
            for (Sprint sprint : sprints) {
                sprintDAO.delete(sprint.getId());
            }
            List<Project> projects = projectDAO.findAll();
            for (Project project : projects) {
                projectDAO.delete(project.getId());
            }
            File sprintFile = new File(XmlConfig.getFilePath(Sprint.class));
            File projectFile = new File(XmlConfig.getFilePath(Project.class));
            if (sprintFile.exists()) {
                sprintFile.delete();
            }
            if (projectFile.exists()) {
                projectFile.delete();
            }
            log.info("tearDown [1] Все спринты и проекты удалены, XML файлы очищены");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось очистить данные: " + e.getMessage()), e);
        }
    }

    /**
     * Тестирование создания спринта.
     * Тип: Позитивный
     */
    @Test
    public void testCreateSprint() {
        String methodName = "testCreateSprint";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(), project);
            log.info("testCreateSprint [1] Создание спринта");
            sprintDAO.create(sprint);
            assertNotNull("Идентификатор спринта должен быть установлен", sprint.getId());
            log.info("testCreateSprint [2] Спринт успешно создан с ID: " + sprint.getId());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать спринт: " + e.getMessage()), e);
            fail("Не удалось создать спринт: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска спринта по идентификатору.
     * Тип: Позитивный
     */
    @Test
    public void testFindById() {
        String methodName = "testFindById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(), project);
            log.info("testFindById [1] Создание спринта");
            sprintDAO.create(sprint);
            Integer id = sprint.getId();

            log.info("testFindById [2] Поиск спринта по ID: " + id);
            Optional<Sprint> found = sprintDAO.findById(id);
            assertTrue("Спринт должен быть найден", found.isPresent());
            assertEquals("Проект спринта должен совпадать", project.getId(), found.get().getProject().getId());
            log.info("testFindById [3] Спринт успешно найден");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти спринт: " + e.getMessage()), e);
            fail("Не удалось найти спринт: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска несуществующего спринта.
     * Тип: Негативный
     */
    @Test
    public void testFindByIdNotFound() {
        String methodName = "testFindByIdNotFound";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testFindByIdNotFound [1] Поиск спринта с ID: 999");
            Optional<Sprint> found = sprintDAO.findById(999);
            assertFalse("Спринт не должен быть найден", found.isPresent());
            log.info("testFindByIdNotFound [2] Спринт не найден, как ожидалось");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить поиск: " + e.getMessage()), e);
            fail("Не удалось выполнить поиск: " + e.getMessage());
        }
    }

    /**
     * Тестирование получения списка всех спринтов.
     * Тип: Позитивный
     */
    @Test
    public void testFindAll() {
        String methodName = "testFindAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint1 = new Sprint(new Date(), new Date(), project);
            Sprint sprint2 = new Sprint(new Date(), new Date(), project);
            log.info("testFindAll [1] Создание двух спринтов");
            sprintDAO.create(sprint1);
            sprintDAO.create(sprint2);

            log.info("testFindAll [2] Получение списка всех спринтов");
            List<Sprint> sprints = sprintDAO.findAll();
            assertEquals("Должно быть найдено 2 спринта", 2, sprints.size());
            log.info("testFindAll [3] Найдено спринтов: " + sprints.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить список спринтов: " + e.getMessage()), e);
            fail("Не удалось получить список спринтов: " + e.getMessage());
        }
    }

    /**
     * Тестирование обновления спринта.
     * Тип: Позитивный
     */
    @Test
    public void testUpdateSprint() {
        String methodName = "testUpdateSprint";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(), project);
            log.info("testUpdateSprint [1] Создание спринта");
            sprintDAO.create(sprint);
            Integer id = sprint.getId();

            sprint.setEndDate(new Date(System.currentTimeMillis() + 86400000));
            log.info("testUpdateSprint [2] Обновление спринта");
            sprintDAO.update(sprint);

            log.info("testUpdateSprint [3] Проверка обновленного спринта");
            Optional<Sprint> updated = sprintDAO.findById(id);
            assertTrue("Спринт должен быть найден", updated.isPresent());
            assertEquals("Дата окончания должна быть обновлена", sprint.getEndDate(), updated.get().getEndDate());
            log.info("testUpdateSprint [4] Спринт успешно обновлен");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить спринт: " + e.getMessage()), e);
            fail("Не удалось обновить спринт: " + e.getMessage());
        }
    }

    /**
     * Тестирование удаления спринта.
     * Тип: Позитивный
     */
    @Test
    public void testDeleteSprint() {
        String methodName = "testDeleteSprint";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(), project);
            log.info("testDeleteSprint [1] Создание спринта");
            sprintDAO.create(sprint);
            Integer id = sprint.getId();

            log.info("testDeleteSprint [2] Удаление спринта с ID: " + id);
            sprintDAO.delete(id);

            log.info("testDeleteSprint [3] Проверка удаления");
            Optional<Sprint> deleted = sprintDAO.findById(id);
            assertFalse("Спринт не должен быть найден", deleted.isPresent());
            log.info("testDeleteSprint [4] Спринт успешно удален");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить спринт: " + e.getMessage()), e);
            fail("Не удалось удалить спринт: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска спринтов по дате начала.
     * Тип: Позитивный
     */
    @Test
    public void testFindByStartDate() {
        String methodName = "testFindByStartDate";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Date startDate = new Date();
            Sprint sprint1 = new Sprint(startDate, new Date(), project);
            Sprint sprint2 = new Sprint(startDate, new Date(), project);
            log.info("testFindByStartDate [1] Создание двух спринтов");
            sprintDAO.create(sprint1);
            sprintDAO.create(sprint2);

            log.info("testFindByStartDate [2] Поиск спринтов по дате начала");
            List<Sprint> sprints = sprintDAO.findByStartDate(startDate);
            assertEquals("Должно быть найдено 2 спринта", 2, sprints.size());
            log.info("testFindByStartDate [3] Найдено спринтов: " + sprints.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти спринты: " + e.getMessage()), e);
            fail("Не удалось найти спринты: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска спринтов по несуществующей дате.
     * Тип: Негативный
     */
    @Test
    public void testFindByStartDateNotFound() {
        String methodName = "testFindByStartDateNotFound";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testFindByStartDateNotFound [1] Поиск спринтов по несуществующей дате");
            List<Sprint> sprints = sprintDAO.findByStartDate(new Date(0));
            assertTrue("Список спринтов должен быть пуст", sprints.isEmpty());
            log.info("testFindByStartDateNotFound [2] Спринты не найдены, как ожидалось");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить поиск: " + e.getMessage()), e);
            fail("Не удалось выполнить поиск: " + e.getMessage());
        }
    }
}
