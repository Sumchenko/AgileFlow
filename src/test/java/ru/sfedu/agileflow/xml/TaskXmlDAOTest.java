package ru.sfedu.agileflow.xml;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.agileflow.config.XmlConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.Sprint;
import ru.sfedu.agileflow.models.Task;
import ru.sfedu.agileflow.models.TaskStatus;
import ru.sfedu.agileflow.models.User;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Тестовый класс для проверки операций XML DAO с задачами.
 */
public class TaskXmlDAOTest {
    private static final Logger log = Logger.getLogger(TaskXmlDAOTest.class);
    private TaskXmlDAO taskDAO;
    private SprintXmlDAO sprintDAO;
    private ProjectXmlDAO projectDAO;
    private UserXmlDAO userDAO;

    /**
     * Подготовка перед каждым тестом.
     */
    @Before
    public void setUp() {
        String methodName = "setUp";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        taskDAO = new TaskXmlDAO();
        sprintDAO = new SprintXmlDAO();
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
            List<Task> tasks = taskDAO.findAll();
            for (Task task : tasks) {
                taskDAO.delete(task.getId());
            }
            List<Sprint> sprints = sprintDAO.findAll();
            for (Sprint sprint : sprints) {
                sprintDAO.delete(sprint.getId());
            }
            List<Project> projects = projectDAO.findAll();
            for (Project project : projects) {
                projectDAO.delete(project.getId());
            }
            List<User> users = userDAO.findAll();
            for (User user : users) {
                userDAO.delete(user.getId());
            }
            File taskFile = new File(XmlConfig.getFilePath(Task.class));
            File sprintFile = new File(XmlConfig.getFilePath(Sprint.class));
            File projectFile = new File(XmlConfig.getFilePath(Project.class));
            File userFile = new File(XmlConfig.getFilePath(User.class));
            if (taskFile.exists()) {
                taskFile.delete();
            }
            if (sprintFile.exists()) {
                sprintFile.delete();
            }
            if (projectFile.exists()) {
                projectFile.delete();
            }
            if (userFile.exists()) {
                userFile.delete();
            }
            log.info("tearDown [1] Все задачи, спринты, проекты и пользователи удалены, XML файлы очищены");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось очистить данные: " + e.getMessage()), e);
        }
    }

    /**
     * Тестирование создания задачи.
     * Тип: Позитивный
     */
    @Test
    public void testCreateTask() {
        String methodName = "testCreateTask";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(), project);
            sprintDAO.create(sprint);
            User user = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            userDAO.create(user);
            Task task = new Task("Тестовая задача", "Описание задачи", TaskStatus.TO_DO, 1, sprint, user);
            log.info("testCreateTask [1] Создание задачи");
            taskDAO.create(task);
            assertNotNull("Идентификатор задачи должен быть установлен", task.getId());
            log.info("testCreateTask [2] Задача успешно создана с ID: " + task.getId());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать задачу: " + e.getMessage()), e);
            fail("Не удалось создать задачу: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска задачи по идентификатору.
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
            User user = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            userDAO.create(user);
            Task task = new Task("Тестовая задача", "Описание задачи", TaskStatus.TO_DO, 1, sprint, user);
            log.info("testFindById [1] Создание задачи");
            taskDAO.create(task);
            Integer id = task.getId();

            log.info("testFindById [2] Поиск задачи по ID: " + id);
            Optional<Task> found = taskDAO.findById(id);
            assertTrue("Задача должна быть найдена", found.isPresent());
            assertEquals("Название задачи должно совпадать", "Тестовая задача", found.get().getTitle());
            log.info("testFindById [3] Задача успешно найдена");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти задачу: " + e.getMessage()), e);
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
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testFindByIdNotFound [1] Поиск задачи с ID: 999");
            Optional<Task> found = taskDAO.findById(999);
            assertFalse("Задача не должна быть найдена", found.isPresent());
            log.info("testFindByIdNotFound [2] Задача не найдена, как ожидалось");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить поиск: " + e.getMessage()), e);
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
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(), project);
            sprintDAO.create(sprint);
            User user = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            userDAO.create(user);
            Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.TO_DO, 1, sprint, user);
            Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.TO_DO, 2, sprint, user);
            log.info("testFindAll [1] Создание двух задач");
            taskDAO.create(task1);
            taskDAO.create(task2);

            log.info("testFindAll [2] Получение списка всех задач");
            List<Task> tasks = taskDAO.findAll();
            assertEquals("Должно быть найдено 2 задачи", 2, tasks.size());
            log.info("testFindAll [3] Найдено задач: " + tasks.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить список задач: " + e.getMessage()), e);
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
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(), project);
            sprintDAO.create(sprint);
            User user = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            userDAO.create(user);
            Task task = new Task("Тестовая задача", "Описание задачи", TaskStatus.TO_DO, 1, sprint, user);
            log.info("testUpdateTask [1] Создание задачи");
            taskDAO.create(task);
            Integer id = task.getId();

            task.setTitle("Обновленная задача");
            task.setStatus(TaskStatus.IN_PROGRESS);
            log.info("testUpdateTask [2] Обновление задачи");
            taskDAO.update(task);

            log.info("testUpdateTask [3] Проверка обновленной задачи");
            Optional<Task> updated = taskDAO.findById(id);
            assertTrue("Задача должна быть найдена", updated.isPresent());
            assertEquals("Название должно быть обновлено", "Обновленная задача", updated.get().getTitle());
            assertEquals("Статус должен быть обновлен", TaskStatus.IN_PROGRESS, updated.get().getStatus());
            log.info("testUpdateTask [4] Задача успешно обновлена");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить задачу: " + e.getMessage()), e);
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
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(), project);
            sprintDAO.create(sprint);
            User user = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            userDAO.create(user);
            Task task = new Task("Тестовая задача", "Описание задачи", TaskStatus.TO_DO, 1, sprint, user);
            log.info("testDeleteTask [1] Создание задачи");
            taskDAO.create(task);
            Integer id = task.getId();

            log.info("testDeleteTask [2] Удаление задачи с ID: " + id);
            taskDAO.delete(id);

            log.info("testDeleteTask [3] Проверка удаления");
            Optional<Task> deleted = taskDAO.findById(id);
            assertFalse("Задача не должна быть найдена", deleted.isPresent());
            log.info("testDeleteTask [4] Задача успешно удалена");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить задачу: " + e.getMessage()), e);
            fail("Не удалось удалить задачу: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска задач по статусу.
     * Тип: Позитивный
     */
    @Test
    public void testFindByStatus() {
        String methodName = "testFindByStatus";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Project project = new Project("Тестовый проект", "Описание");
            projectDAO.create(project);
            Sprint sprint = new Sprint(new Date(), new Date(), project);
            sprintDAO.create(sprint);
            User user = new User("Тестовый пользователь", "test@example.com", "Биография", true, new Date());
            userDAO.create(user);
            Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.TO_DO, 1, sprint, user);
            Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.TO_DO, 2, sprint, user);
            log.info("testFindByStatus [1] Создание двух задач");
            taskDAO.create(task1);
            taskDAO.create(task2);

            log.info("testFindByStatus [2] Поиск задач по статусу TODO");
            List<Task> tasks = taskDAO.findByStatus(TaskStatus.TO_DO);
            assertEquals("Должно быть найдено 2 задачи", 2, tasks.size());
            log.info("testFindByStatus [3] Найдено задач: " + tasks.size());
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти задачи: " + e.getMessage()), e);
            fail("Не удалось найти задачи: " + e.getMessage());
        }
    }

    /**
     * Тестирование поиска задач по несуществующему статусу.
     * Тип: Негативный
     */
    @Test
    public void testFindByStatusNotFound() {
        String methodName = "testFindByStatusNotFound";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("testFindByStatusNotFound [1] Поиск задач по статусу DONE");
            List<Task> tasks = taskDAO.findByStatus(TaskStatus.DONE);
            assertTrue("Список задач должен быть пуст", tasks.isEmpty());
            log.info("testFindByStatusNotFound [2] Задачи не найдены, как ожидалось");
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось выполнить поиск: " + e.getMessage()), e);
            fail("Не удалось выполнить поиск: " + e.getMessage());
        }
    }
}
