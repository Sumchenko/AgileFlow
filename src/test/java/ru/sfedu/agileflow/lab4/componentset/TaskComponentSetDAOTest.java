package ru.sfedu.agileflow.lab4.componentset;

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
 * Тестовый класс для проверки операций DAO с задачами (Set коллекция компонентов).
 */
public class TaskComponentSetDAOTest {
    private static final Logger log = Logger.getLogger(TaskComponentSetDAOTest.class);
    private TaskComponentSetDAO taskDAO;

    /**
     * Подготовка перед каждым тестом.
     */
    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format("%s [1] Начало выполнения метода", methodName));
        taskDAO = new TaskComponentSetDAOImpl();
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
     * Тестирование создания задачи.
     * Тип: Позитивный
     */
    @Test
    public void testCreateTask() {
        String methodName = "testCreateTask";
        log.info(String.format("%s [1] Начало выполнения метода", methodName));
        try {
            Task task = new Task("Test Task", "Description", TaskStatus.TO_DO, 1);
            task.getAttachments().add(new TaskAttachment("file1.pdf", 1024));
            task.getAttachments().add(new TaskAttachment("file2.png", 2048));
            log.info("testCreateTask [1] Создание задачи");
            taskDAO.create(task);
            assertNotNull("Идентификатор задачи должен быть установлен", task.getId());
            log.info("testCreateTask [2] Задача успешно создана с ID: " + task.getId());
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
            Task task = new Task(null, "Description", TaskStatus.TO_DO, 1);
            task.getAttachments().add(new TaskAttachment("file1.pdf", 1024));
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
            Task task = new Task("Test Task", "Description", TaskStatus.TO_DO, 1);
            TaskAttachment attachment = new TaskAttachment("file1.pdf", 1024);
            task.getAttachments().add(attachment);
            log.info("testFindById [1] Создание задачи");
            taskDAO.create(task);
            Integer id = task.getId();

            log.info("testFindById [2] Поиск задачи по ID: " + id);
            Optional<Task> found = taskDAO.findById(id);
            assertTrue("Задача должна быть найдена", found.isPresent());
            assertEquals("Название задачи должно совпадать", "Test Task", found.get().getTitle());
            assertTrue("Вложение должно присутствовать", found.get().getAttachments().contains(attachment));
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
            Task task1 = new Task("Task 1", "Desc 1", TaskStatus.TO_DO, 1);
            task1.getAttachments().add(new TaskAttachment("file1.pdf", 1024));
            Task task2 = new Task("Task 2", "Desc 2", TaskStatus.IN_PROGRESS, 2);
            task2.getAttachments().add(new TaskAttachment("file2.png", 2048));
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
            Task task = new Task("Test Task", "Description", TaskStatus.TO_DO, 1);
            task.getAttachments().add(new TaskAttachment("file1.pdf", 1024));
            log.info("testUpdateTask [1] Создание задачи");
            taskDAO.create(task);
            Integer id = task.getId();

            task.setTitle("Updated Task");
            task.getAttachments().add(new TaskAttachment("file2.png", 2048));
            task.getAttachments().remove(new TaskAttachment("file1.pdf", 1024));
            log.info("testUpdateTask [2] Обновление задачи");
            taskDAO.update(task);

            log.info("testUpdateTask [3] Проверка обновленной задачи");
            Optional<Task> updated = taskDAO.findById(id);
            assertTrue("Задача должна быть найдена", updated.isPresent());
            assertEquals("Название должно быть обновлено", "Updated Task", updated.get().getTitle());
            assertTrue("Новое вложение должно присутствовать", updated.get().getAttachments().contains(new TaskAttachment("file2.png", 2048)));
            assertFalse("Старое вложение должно быть удалено", updated.get().getAttachments().contains(new TaskAttachment("file1.pdf", 1024)));
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
            Task task = new Task("Test Task", "Description", TaskStatus.TO_DO, 1);
            task.getAttachments().add(new TaskAttachment("file1.pdf", 1024));
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
     * Тестирование удаления несуществующей задачи.
     * Тип: Негативный
     */
    @Test
    public void testDeleteTaskNotFound() {
        String methodName = "testDeleteTaskNotFound";
        log.info(String.format("%s [1] Начало выполнения метода", methodName));
        try {
            log.info("testDeleteTaskNotFound [1] Попытка удаления задачи с ID: 999");
            taskDAO.delete(999);
            log.info("testDeleteTaskNotFound [2] Удаление несуществующей задачи выполнено без ошибок");
            log.info(String.format("%s [2] Метод успешно завершен", methodName));
        } catch (Exception e) {
            log.error(String.format("%s [3] Произошла ошибка: %s", methodName, e.getMessage()), e);
            fail("Не удалось выполнить удаление: " + e.getMessage());
        }
    }
}