package ru.sfedu.agileflow.csv;;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.Retrospective;
import ru.sfedu.agileflow.models.Sprint;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Тестовый класс для RetrospectiveCsvDAO.
 */
public class RetrospectiveCsvDAOTest {
    private static final Logger log = Logger.getLogger(RetrospectiveCsvDAOTest.class);
    private RetrospectiveCsvDAO retrospectiveDAO;
    private SprintCsvDAO sprintDAO;
    private ProjectCsvDAO projectDAO;
    private static final String CSV_DIR = "src/main/resources/dataCSV";

    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        retrospectiveDAO = new RetrospectiveCsvDAO();
        sprintDAO = new SprintCsvDAO();
        projectDAO = new ProjectCsvDAO();
        try {
            Files.deleteIfExists(Paths.get(CSV_DIR, "retrospectives.csv"));
            Files.deleteIfExists(Paths.get(CSV_DIR, "retrospective_improvements.csv"));
            Files.deleteIfExists(Paths.get(CSV_DIR, "retrospective_positives.csv"));
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
            Files.deleteIfExists(Paths.get(CSV_DIR, "retrospectives.csv"));
            Files.deleteIfExists(Paths.get(CSV_DIR, "retrospective_improvements.csv"));
            Files.deleteIfExists(Paths.get(CSV_DIR, "retrospective_positives.csv"));
            Files.deleteIfExists(Paths.get(CSV_DIR, "sprints.csv"));
            Files.deleteIfExists(Paths.get(CSV_DIR, "projects.csv"));
            log.info("tearDown [1] CSV-файлы удалены");
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить CSV: " + e.getMessage()), e);
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    @Test
    public void testCreateRetrospective() {
        String methodName = "testCreateRetrospective";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(System.currentTimeMillis() + 86400000), project);
            sprintDAO.create(sprint);
            Retrospective retrospective = new Retrospective(sprint, Constants.TEST_SPRINT_SUMMARY,
                    Arrays.asList("Улучшение 1"), Arrays.asList("Позитив 1"));
            retrospectiveDAO.create(retrospective);
            assertNotNull("Идентификатор должен быть установлен", retrospective.getId());
            log.info("testCreateRetrospective [1] Ретроспектива создана с ID: " + retrospective.getId());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать ретроспективу: " + e.getMessage()), e);
            fail("Не удалось создать ретроспективу: " + e.getMessage());
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
            Retrospective retrospective = new Retrospective(sprint, Constants.TEST_SPRINT_SUMMARY,
                    Arrays.asList("Улучшение 1"), Arrays.asList("Позитив 1"));
            retrospectiveDAO.create(retrospective);
            Optional<Retrospective> found = retrospectiveDAO.findById(retrospective.getId());
            assertTrue("Ретроспектива должна быть найдена", found.isPresent());
            assertEquals("Резюме должно совпадать", Constants.TEST_SPRINT_SUMMARY, found.get().getSummary());
            assertEquals("Должно быть 1 улучшение", 1, found.get().getImprovements().size());
            assertEquals("Должно быть 1 позитив", 1, found.get().getPositives().size());
            log.info("testFindById [1] Ретроспектива найдена");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти ретроспективу: " + e.getMessage()), e);
            fail("Не удалось найти ретроспективу: " + e.getMessage());
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
            retrospectiveDAO.create(new Retrospective(sprint, "Резюме 1", Arrays.asList("Улучшение 1"), Arrays.asList("Позитив 1")));
            retrospectiveDAO.create(new Retrospective(sprint, "Резюме 2", Arrays.asList("Улучшение 2"), Arrays.asList("Позитив 2")));
            List<Retrospective> retrospectives = retrospectiveDAO.findAll();
            assertEquals("Должно быть 2 ретроспективы", 2, retrospectives.size());
            log.info("testFindAll [1] Найдено ретроспектив: " + retrospectives.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить ретроспективы: " + e.getMessage()), e);
            fail("Не удалось получить ретроспективы: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateRetrospective() {
        String methodName = "testUpdateRetrospective";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(System.currentTimeMillis() + 86400000), project);
            sprintDAO.create(sprint);
            Retrospective retrospective = new Retrospective(sprint, Constants.TEST_SPRINT_SUMMARY,
                    Arrays.asList("Улучшение 1"), Arrays.asList("Позитив 1"));
            retrospectiveDAO.create(retrospective);
            retrospective.setSummary("Обновленное резюме");
            retrospective.setImprovements(Arrays.asList("Новое улучшение"));
            retrospective.setPositives(Arrays.asList("Новый позитив"));
            retrospectiveDAO.update(retrospective);
            Optional<Retrospective> updated = retrospectiveDAO.findById(retrospective.getId());
            assertTrue("Ретроспектива должна быть найдена", updated.isPresent());
            assertEquals("Резюме должно быть обновлено", "Обновленное резюме", updated.get().getSummary());
            assertEquals("Должно быть 1 новое улучшение", 1, updated.get().getImprovements().size());
            assertEquals("Должно быть 1 новый позитив", 1, updated.get().getPositives().size());
            log.info("testUpdateRetrospective [1] Ретроспектива обновлена");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить ретроспективу: " + e.getMessage()), e);
            fail("Не удалось обновить ретроспективу: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteRetrospective() {
        String methodName = "testDeleteRetrospective";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(System.currentTimeMillis() + 86400000), project);
            sprintDAO.create(sprint);
            Retrospective retrospective = new Retrospective(sprint, Constants.TEST_SPRINT_SUMMARY,
                    Arrays.asList("Улучшение 1"), Arrays.asList("Позитив 1"));
            retrospectiveDAO.create(retrospective);
            retrospectiveDAO.delete(retrospective.getId());
            Optional<Retrospective> deleted = retrospectiveDAO.findById(retrospective.getId());
            assertFalse("Ретроспектива не должна быть найдена", deleted.isPresent());
            log.info("testDeleteRetrospective [1] Ретроспектива удалена");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить ретроспективу: " + e.getMessage()), e);
            fail("Не удалось удалить ретроспективу: " + e.getMessage());
        }
    }
}
