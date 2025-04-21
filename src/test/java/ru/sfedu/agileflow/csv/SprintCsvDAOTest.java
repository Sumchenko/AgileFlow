package ru.sfedu.agileflow.csv;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.Sprint;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Тестовый класс для SprintCsvDAO.
 */
public class SprintCsvDAOTest {
    private static final Logger log = Logger.getLogger(SprintCsvDAOTest.class);
    private SprintCsvDAO sprintDAO;
    private ProjectCsvDAO projectDAO;
    private static final String CSV_DIR = "src/main/resources/dataCSV";

    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        sprintDAO = new SprintCsvDAO();
        projectDAO = new ProjectCsvDAO();
        try {
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
            Files.deleteIfExists(Paths.get(CSV_DIR, "sprints.csv"));
            Files.deleteIfExists(Paths.get(CSV_DIR, "projects.csv"));
            log.info("tearDown [1] CSV-файлы удалены");
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить CSV: " + e.getMessage()), e);
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Обрезает временную часть даты, оставляя только год, месяц и день.
     * @param date Дата
     * @return Дата без времени
     */
    private Date truncateTime(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Test
    public void testCreateSprint() {
        String methodName = "testCreateSprint";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(System.currentTimeMillis() + 86400000), project);
            sprintDAO.create(sprint);
            assertNotNull("Идентификатор должен быть установлен", sprint.getId());
            log.info("testCreateSprint [1] Спринт создан с ID: " + sprint.getId());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать спринт: " + e.getMessage()), e);
            fail("Не удалось создать спринт: " + e.getMessage());
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
            Optional<Sprint> found = sprintDAO.findById(sprint.getId());
            assertTrue("Спринт должен быть найден", found.isPresent());
            assertEquals("Дата начала должна совпадать", truncateTime(sprint.getStartDate()), found.get().getStartDate());
            log.info("testFindById [1] Спринт найден");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти спринт: " + e.getMessage()), e);
            fail("Не удалось найти спринт: " + e.getMessage());
        }
    }

    @Test
    public void testFindAll() {
        String methodName = "testFindAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            sprintDAO.create(new Sprint(new Date(), new Date(System.currentTimeMillis() + 86400000), project));
            sprintDAO.create(new Sprint(new Date(), new Date(System.currentTimeMillis() + 2 * 86400000), project));
            List<Sprint> sprints = sprintDAO.findAll();
            assertEquals("Должно быть 2 спринта", 2, sprints.size());
            log.info("testFindAll [1] Найдено спринтов: " + sprints.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить спринты: " + e.getMessage()), e);
            fail("Не удалось получить спринты: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateSprint() {
        String methodName = "testUpdateSprint";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(System.currentTimeMillis() + 86400000), project);
            sprintDAO.create(sprint);
            sprint.setEndDate(new Date(System.currentTimeMillis() + 2 * 86400000));
            sprintDAO.update(sprint);
            Optional<Sprint> updated = sprintDAO.findById(sprint.getId());
            assertTrue("Спринт должен быть найден", updated.isPresent());
            assertEquals("Дата окончания должна быть обновлена", truncateTime(sprint.getEndDate()), updated.get().getEndDate());
            log.info("testUpdateSprint [1] Спринт обновлен");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить спринт: " + e.getMessage()), e);
            fail("Не удалось обновить спринт: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteSprint() {
        String methodName = "testDeleteSprint";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(System.currentTimeMillis() + 86400000), project);
            sprintDAO.create(sprint);
            sprintDAO.delete(sprint.getId());
            Optional<Sprint> deleted = sprintDAO.findById(sprint.getId());
            assertFalse("Спринт не должен быть найден", deleted.isPresent());
            log.info("testDeleteSprint [1] Спринт удален");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить спринт: " + e.getMessage()), e);
            fail("Не удалось удалить спринт: " + e.getMessage());
        }
    }
}