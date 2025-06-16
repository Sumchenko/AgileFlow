package ru.sfedu.agileflow.lab3.joinedtable;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.TaskStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Тестовый класс для проверки операций DAO с задачами (Joined Table).
 */
public class TaskJoinedTableDAOTest {
    private static final Logger log = Logger.getLogger(TaskJoinedTableDAOTest.class);
    private TaskJoinedTableDAO taskDAO;

    /**
     * Подготовка перед каждым тестом.
     */
    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format("%s [1] Начало выполнения метода", methodName));
        taskDAO = new TaskJoinedTableDAOImpl();
        log.info("setUp [1] Инициализация TaskDAO завершена");
        log.info(String.format("%s [2] Метод успешно завершен", methodName));
    }

    /**
     * Очистка после каждого теста.
     */
    @After
    public void tearDown() {
        String methodName = "tearDown";
        log.info(String.format("%s [1] Начало выполнения метода", methodName));
        try {
            List<Task> tasks = taskDAO.findAll();
            for (Task task : tasks) {
                taskDAO.delete(task.getId());
            }
            log.info("tearDown [1] Все задачи удалены");
            log.info(String.format("%s [2] Метод успешно завершен", methodName));
        } catch (Exception e) {
            log.error(String.format("%s [3] Произошла ошибка: %s", methodName, e.getMessage()), e);
        }
    }

    /**
     * Тестирование создания задачи типа Bug.
     * Тип: Позитивный
     */
    @Test
    public void testCreateBugTask() {
        String methodName = "testCreateBugTask";
        log.info(String.format("%s [1] Начало выполнения метода", methodName));
        try {
            BugTask task = new BugTask("Fix Bug", "Bug Description", TaskStatus.TO_DO, 1, null, null, "High");
            log.info("testCreateBugTask [1] Создание задачи типа Bug");
            taskDAO.create(task);
            assertNotNull("Идентификатор задачи должен быть установлен", task.getId());
            log.info("testCreateBugTask [2] Задача успешно создана с ID: " + task.getId());
            log.info(String.format("%s [2] Метод успешно завершен", methodName));
        } catch (Exception e) {
            log.error(String.format("%s [3] Произошла ошибка: %s", methodName, e.getMessage()), e);
            fail("Не удалось создать задачу: " + e.getMessage());
        }
    }

    /**
     * Тестирование создания задачи с невалидными данными.
     * Тип: Негативный
     */
    @Test
    public void testCreateTaskInvalid() {
        String methodName = "testCreateTaskInvalid";
        log.info(String.format("%s [1] Начало выполнения метода", methodName));
        try {
            BugTask task = new BugTask(null, "Bug Description", TaskStatus.TO_DO, 1, null, null, "High");
            log.info("testCreateTaskInvalid [1] Попытка создания задачи с null заголовком");
            taskDAO.create(task);
            fail("Должно быть выброшено исключение из-за невалидных данных");
        } catch (Exception e) {
            log.info("testCreateTaskInvalid [2] Исключение поймано, как ожидалось: " + e.getMessage());
            log.info(String.format("%s [2] Метод успешно завершен", methodName));
        }
    }

    /**
     * Тестирование поиска задачи по идентификатору.
     * Тип: Позитивный
     */
    @Test
    public void testFindById() {
        String methodName = "testFindById";
        log.info(String.format("%s [1] Начало выполнения метода", methodName));
        try {
            FeatureTask task = new FeatureTask("New Feature", "Feature Description", TaskStatus.TO_DO, 2, null, null, "Must work");
            log.info("testFindById [1] Создание задачи типа Feature");
            taskDAO.create(task);
            Integer id = task.getId();

            log.info("testFindById [2] Поиск задачи по ID: " + id);
            Optional<Task> found = taskDAO.findById(id);
            assertTrue("Задача должна быть найдена", found.isPresent());
            assertEquals("Заголовок задачи должен совпадать", "New Feature", found.get().getTitle());
            log.info("testFindById [3] Задача успешно найдена");
            log.info(String.format("%s [2] Метод успешно завершен", methodName));
        } catch (Exception e) {
            log.error(String.format("%s [3] Произошла ошибка: %s", methodName, e.getMessage()), e);
            fail("Не удалось найти задачу: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска несуществующей задачи.
     * Тип: Негативный
     */
    @Test
    public void testFindByIdNotFound() {
        String methodName = "testFindByIdNotFound";
        log.info(String.format("%s [1] Начало выполнения метода", methodName));
        try {
            log.info("testFindByIdNotFound [1] Поиск задачи с ID: 999");
            Optional<Task> found = taskDAO.findById(999);
            assertFalse("Задача не должна быть найдена", found.isPresent());
            log.info("testFindByIdNotFound [2] Задача не найдена, как ожидалось");
            log.info(String.format("%s [2] Метод успешно завершен", methodName));
        } catch (Exception e) {
            log.error(String.format("%s [3] Произошла ошибка: %s", methodName, e.getMessage()), e);
            fail("Не удалось выполнить поиск: " + e.getMessage());
        }
    }

    /**
     * Тестирование получения списка всех задач.
     * Тип: Позитивный
     */
    @Test
    public void testFindAll() {
        String methodName = "testFindAll";
        log.info(String.format("%s [1] Начало выполнения метода", methodName));
        try {
            BugTask task1 = new BugTask("Bug 1", "Description 1", TaskStatus.TO_DO, 1, null, null, "High");
            FeatureTask task2 = new FeatureTask("Feature 1", "Description 2", TaskStatus.IN_PROGRESS, 2, null, null, "Must work");
            log.info("testFindAll [1] Создание двух задач");
            taskDAO.create(task1);
            taskDAO.create(task2);

            log.info("testFindAll [2] Получение списка всех задач");
            List<Task> tasks = taskDAO.findAll();
            assertEquals("Должно быть найдено 2 задачи", 2, tasks.size());
            log.info("testFindAll [3] Найдено задач: " + tasks.size());
            log.info(String.format("%s [2] Метод успешно завершен", methodName));
        } catch (Exception e) {
            log.error(String.format("%s [3] Произошла ошибка: %s", methodName, e.getMessage()), e);
            fail("Не удалось получить список задач: " + e.getMessage());
        }
    }

    /**
     * Тестирование обновления задачи.
     * Тип: Позитивный
     */
    @Test
    public void testUpdateTask() {
        String methodName = "testUpdateTask";
        log.info(String.format("%s [1] Начало выполнения метода", methodName));
        try {
            BugTask task = new BugTask("Bug Task", "Description", TaskStatus.TO_DO, 1, null, null, "High");
            log.info("testUpdateTask [1] Создание задачи");
            taskDAO.create(task);
            Integer id = task.getId();

            task.setTitle("Updated Bug Task");
            task.setStatus(TaskStatus.IN_PROGRESS);
            log.info("testUpdateTask [2] Обновление задачи");
            taskDAO.update(task);

            log.info("testUpdateTask [3] Проверка обновленной задачи");
            Optional<Task> updated = taskDAO.findById(id);
            assertTrue("Задача должна быть найдена", updated.isPresent());
            assertEquals("Заголовок должен быть обновлен", "Updated Bug Task", updated.get().getTitle());
            assertEquals("Статус должен быть обновлен", TaskStatus.IN_PROGRESS, updated.get().getStatus());
            log.info("testUpdateTask [4] Задача успешно обновлена");
            log.info(String.format("%s [2] Метод успешно завершен", methodName));
        } catch (Exception e) {
            log.error(String.format("%s [3] Произошла ошибка: %s", methodName, e.getMessage()), e);
            fail("Не удалось обновить задачу: " + e.getMessage());
        }
    }

    /**
     * Тестирование удаления задачи.
     * Тип: Позитивный
     */
    @Test
    public void testDeleteTask() {
        String methodName = "testDeleteTask";
        log.info(String.format("%s [1] Начало выполнения метода", methodName));
        try {
            FeatureTask task = new FeatureTask("Feature Task", "Description", TaskStatus.TO_DO, 2, null, null, "Must work");
            log.info("testDeleteTask [1] Создание задачи");
            taskDAO.create(task);
            Integer id = task.getId();

            log.info("testDeleteTask [2] Удаление задачи с ID: " + id);
            taskDAO.delete(id);

            log.info("testDeleteTask [3] Проверка удаления");
            Optional<Task> deleted = taskDAO.findById(id);
            assertFalse("Задача не должна быть найдена", deleted.isPresent());
            log.info("testDeleteTask [4] Задача успешно удалена");
            log.info(String.format("%s [2] Метод успешно завершен", methodName));
        } catch (Exception e) {
            log.error(String.format("%s [3] Произошла ошибка: %s", methodName, e.getMessage()), e);
            fail("Не удалось удалить задачу: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска задач по статусу.
     * Тип: Позитивный
     */
    @Test
    public void testFindByStatusPositive() {
        String methodName = "testFindByStatusPositive";
        log.info(String.format("%s [1] Начало выполнения метода", methodName));
        try {
            BugTask task1 = new BugTask("Bug 1", "Description 1", TaskStatus.TO_DO, 1, null, null, "High");
            FeatureTask task2 = new FeatureTask("Feature 1", "Description 2", TaskStatus.TO_DO, 2, null, null, "Must work");
            log.info("testFindByStatusPositive [1] Создание двух задач с статусом TO_DO");
            taskDAO.create(task1);
            taskDAO.create(task2);

            log.info("testFindByStatusPositive [2] Поиск задач по статусу TO_DO");
            List<Task> tasks = taskDAO.findByStatus(TaskStatus.TO_DO);
            assertEquals("Должно быть найдено 2 задачи", 2, tasks.size());
            log.info("testFindByStatusPositive [3] Найдено задач: " + tasks.size());
            log.info(String.format("%s [2] Метод успешно завершен", methodName));
        } catch (Exception e) {
            log.error(String.format("%s [3] Произошла ошибка: %s", methodName, e.getMessage()), e);
            fail("Не удалось найти задачи по статусу: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска задач по несуществующему статусу.
     * Тип: Негативный
     */
    @Test
    public void testFindByStatusNegative() {
        String methodName = "testFindByStatusNegative";
        log.info(String.format("%s [1] Начало выполнения метода", methodName));
        try {
            log.info("testFindByStatusNegative [1] Поиск задач по статусу DONE");
            List<Task> tasks = taskDAO.findByStatus(TaskStatus.DONE);
            assertEquals("Не должно быть найдено задач", 0, tasks.size());
            log.info("testFindByStatusNegative [2] Задачи не найдены, как ожидалось");
            log.info(String.format("%s [2] Метод успешно завершен", methodName));
        } catch (Exception e) {
            log.error(String.format("%s [3] Произошла ошибка: %s", methodName, e.getMessage()), e);
            fail("Не удалось выполнить поиск: " + e.getMessage());
        }
    }
}