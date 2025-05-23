package ru.sfedu.agileflow.dao;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.Retrospective;
import ru.sfedu.agileflow.models.Sprint;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Тестовый класс для проверки операций DAO с ретроспективами.
 */
public class RetrospectiveDAOTest {
    private static final Logger log = Logger.getLogger(RetrospectiveDAOTest.class);
    private RetrospectiveDAO retrospectiveDAO;
    private SprintDAO sprintDAO;
    private ProjectDAO projectDAO;

    /**
     * Подготовка перед каждым тестом.
     */
    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        retrospectiveDAO = new RetrospectiveDAO();
        sprintDAO = new SprintDAO();
        projectDAO = new ProjectDAO();
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
            List<Retrospective> retrospectives = retrospectiveDAO.findAll();
            for (Retrospective retrospective : retrospectives) {
                retrospectiveDAO.delete(retrospective.getId());
            }
            List<Sprint> sprints = sprintDAO.findAll();
            for (Sprint sprint : sprints) {
                sprintDAO.delete(sprint.getId());
            }
            List<Project> projects = projectDAO.findAll();
            for (Project project : projects) {
                projectDAO.delete(project.getId());
            }
            log.info("tearDown [1] Все ретроспективы, спринты и проекты удалены");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось очистить данные: " + e.getMessage()), e);
        }
    }

    /**
     * Тестирование создания ретроспективы.
     * Тип: Позитивный
     */
    @Test
    public void testCreateRetrospective() {
        String methodName = "testCreateRetrospective";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(), project);
            sprintDAO.create(sprint);
            Retrospective retrospective = new Retrospective(sprint, "Итоги спринта",
                    Arrays.asList("Улучшить планирование"), Arrays.asList("Хорошая командная работа"));
            log.info("testCreateRetrospective [1] Создание ретроспективы");
            retrospectiveDAO.create(retrospective);
            assertNotNull("Идентификатор ретроспективы должен быть установлен", retrospective.getId());
            log.info("testCreateRetrospective [2] Ретроспектива успешно создана с ID: " + retrospective.getId());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать ретроспективу: " + e.getMessage()), e);
            fail("Не удалось создать ретроспективу: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска ретроспективы по идентификатору.
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
            sprintDAO.create(sprint);
            Retrospective retrospective = new Retrospective(sprint, "Итоги спринта",
                    Arrays.asList("Улучшить планирование"), Arrays.asList("Хорошая командная работа"));
            log.info("testFindById [1] Создание ретроспективы");
            retrospectiveDAO.create(retrospective);
            Integer id = retrospective.getId();

            log.info("testFindById [2] Поиск ретроспективы по ID: " + id);
            Optional<Retrospective> found = retrospectiveDAO.findById(id);
            assertTrue("Ретроспектива должна быть найдена", found.isPresent());
            assertEquals("Итоги должны совпадать", "Итоги спринта", found.get().getSummary());
            log.info("testFindById [3] Ретроспектива успешно найдена");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти ретроспективу: " + e.getMessage()), e);
            fail("Не удалось найти ретроспективу: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска несуществующей ретроспективы.
     * Тип: Негативный
     */
    @Test
    public void testFindByIdNotFound() {
        String methodName = "testFindByIdNotFound";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testFindByIdNotFound [1] Поиск ретроспективы с ID: 999");
            Optional<Retrospective> found = retrospectiveDAO.findById(999);
            assertFalse("Ретроспектива не должна быть найдена", found.isPresent());
            log.info("testFindByIdNotFound [2] Ретроспектива не найдена, как ожидалось");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить поиск: " + e.getMessage()), e);
            fail("Не удалось выполнить поиск: " + e.getMessage());
        }
    }

    /**
     * Тестирование получения списка всех ретроспектив.
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
            sprintDAO.create(sprint1);
            sprintDAO.create(sprint2);
            Retrospective retrospective1 = new Retrospective(sprint1, "Итоги 1",
                    Arrays.asList("Улучшение 1"), Arrays.asList("Позитив 1"));
            Retrospective retrospective2 = new Retrospective(sprint2, "Итоги 2",
                    Arrays.asList("Улучшение 2"), Arrays.asList("Позитив 2"));
            log.info("testFindAll [1] Создание двух ретроспектив");
            retrospectiveDAO.create(retrospective1);
            retrospectiveDAO.create(retrospective2);

            log.info("testFindAll [2] Получение списка всех ретроспектив");
            List<Retrospective> retrospectives = retrospectiveDAO.findAll();
            assertEquals("Должно быть найдено 2 ретроспективы", 2, retrospectives.size());
            log.info("testFindAll [3] Найдено ретроспектив: " + retrospectives.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить список ретроспектив: " + e.getMessage()), e);
            fail("Не удалось получить список ретроспектив: " + e.getMessage());
        }
    }

    /**
     * Тестирование обновления ретроспективы.
     * Тип: Позитивный
     */
    @Test
    public void testUpdateRetrospective() {
        String methodName = "testUpdateRetrospective";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(), project);
            sprintDAO.create(sprint);
            Retrospective retrospective = new Retrospective(sprint, "Итоги спринта",
                    Arrays.asList("Улучшить планирование"), Arrays.asList("Хорошая работа"));
            log.info("testUpdateRetrospective [1] Создание ретроспективы");
            retrospectiveDAO.create(retrospective);
            Integer id = retrospective.getId();

            retrospective.setSummary("Обновленные итоги");
            retrospective.setImprovements(Arrays.asList("Новое улучшение"));
            log.info("testUpdateRetrospective [2] Обновление ретроспективы");
            retrospectiveDAO.update(retrospective);

            log.info("testUpdateRetrospective [3] Проверка обновленной ретроспективы");
            try (jakarta.persistence.EntityManager em = ru.sfedu.agileflow.config.DatabaseConfig.getEntityManager()) {
                Retrospective updated = em.find(Retrospective.class, id);
                assertNotNull("Ретроспектива должна быть найдена", updated);
                // Инициализируем коллекцию improvements в пределах сессии
                updated.getImprovements().size(); // Заставляем Hibernate загрузить коллекцию
                assertEquals("Итоги должны быть обновлены", "Обновленные итоги", updated.getSummary());
                assertEquals("Улучшения должны быть обновлены", Arrays.asList("Новое улучшение"), updated.getImprovements());
                log.info("testUpdateRetrospective [4] Ретроспектива успешно обновлена");
            }
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить ретроспективу: " + e.getMessage()), e);
            fail("Не удалось обновить ретроспективу: " + e.getMessage());
        }
    }

    /**
     * Тестирование удаления ретроспективы.
     * Тип: Позитивный
     */
    @Test
    public void testDeleteRetrospective() {
        String methodName = "testDeleteRetrospective";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(), project);
            sprintDAO.create(sprint);
            Retrospective retrospective = new Retrospective(sprint, "Итоги спринта",
                    Arrays.asList("Улучшить планирование"), Arrays.asList("Хорошая работа"));
            log.info("testDeleteRetrospective [1] Создание ретроспективы");
            retrospectiveDAO.create(retrospective);
            Integer id = retrospective.getId();

            log.info("testDeleteRetrospective [2] Удаление ретроспективы с ID: " + id);
            retrospectiveDAO.delete(id);

            log.info("testDeleteRetrospective [3] Проверка удаления");
            Optional<Retrospective> deleted = retrospectiveDAO.findById(id);
            assertFalse("Ретроспектива не должна быть найдена", deleted.isPresent());
            log.info("testDeleteRetrospective [4] Ретроспектива успешно удалена");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить ретроспективу: " + e.getMessage()), e);
            fail("Не удалось удалить ретроспективу: " + e.getMessage());
        }
    }
}