package ru.sfedu.agileflow.xml;

import jakarta.xml.bind.JAXBException;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.XmlConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.dao.GenericDAO;
import ru.sfedu.agileflow.models.Task;
import ru.sfedu.agileflow.models.TaskStatus;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO-класс для управления задачами в XML хранилище.
 */
public class TaskXmlDAO implements GenericDAO<Task, Integer> {
    private static final Logger log = Logger.getLogger(TaskXmlDAO.class);
    private static final AtomicInteger idGenerator = new AtomicInteger(1);

    /**
     * Создает новую задачу в XML хранилище.
     * @param task Задача для сохранения
     */
    @Override
    public void create(Task task) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, task.toString()));

        try {
            XmlDataWrapper wrapper = loadData();
            task.setId(idGenerator.getAndIncrement());
            wrapper.getTasks().add(task);
            saveData(wrapper);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Задача сохранена с ID: " + task.getId()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать задачу: " + e.getMessage()));
            throw new RuntimeException("Не удалось создать задачу", e);
        }
    }

    /**
     * Находит задачу по идентификатору.
     * @param id Идентификатор задачи
     * @return Optional с задачей, если найдена, иначе пустой Optional
     */
    @Override
    public Optional<Task> findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            XmlDataWrapper wrapper = loadData();
            Task task = wrapper.getTasks().stream()
                    .filter(t -> t.getId() == id)
                    .findFirst()
                    .orElse(null);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, task != null ? "Задача найдена" : "Задача не найдена"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.ofNullable(task);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти задачу: " + e.getMessage()));
            throw new RuntimeException("Не удалось найти задачу", e);
        }
    }

    /**
     * Возвращает список всех задач.
     * @return Список задач
     */
    @Override
    public List<Task> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try {
            XmlDataWrapper wrapper = loadData();
            List<Task> tasks = wrapper.getTasks();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено задач: " + tasks.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return tasks;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить задачи: " + e.getMessage()));
            throw new RuntimeException("Не удалось получить задачи", e);
        }
    }

    /**
     * Обновляет данные задачи.
     * @param task Обновленная задача
     */
    @Override
    public void update(Task task) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, task.toString()));

        try {
            XmlDataWrapper wrapper = loadData();
            wrapper.getTasks().removeIf(t -> t.getId() == task.getId());
            wrapper.getTasks().add(task);
            saveData(wrapper);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Задача обновлена"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить задачу: " + e.getMessage()));
            throw new RuntimeException("Не удалось обновить задачу", e);
        }
    }

    /**
     * Удаляет задачу по идентификатору.
     * @param id Идентификатор задачи
     */
    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            XmlDataWrapper wrapper = loadData();
            boolean removed = wrapper.getTasks().removeIf(t -> t.getId() == id);
            saveData(wrapper);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, removed ? "Задача удалена" : "Задача не найдена"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить задачу: " + e.getMessage()));
            throw new RuntimeException("Не удалось удалить задачу", e);
        }
    }

    /**
     * Находит задачи по статусу.
     * @param status Статус задачи
     * @return Список найденных задач
     */
    public List<Task> findByStatus(TaskStatus status) {
        String methodName = "findByStatus";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "status: " + status));

        try {
            XmlDataWrapper wrapper = loadData();
            List<Task> tasks = wrapper.getTasks().stream()
                    .filter(t -> t.getStatus() == status)
                    .toList();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено задач: " + tasks.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return tasks;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти задачи по статусу: " + e.getMessage()));
            throw new RuntimeException("Не удалось найти задачи по статусу", e);
        }
    }

    private XmlDataWrapper loadData() throws JAXBException {
        File file = new File(XmlConfig.getFilePath(Task.class));
        if (!file.exists()) {
            return new XmlDataWrapper();
        }
        return (XmlDataWrapper) XmlConfig.getUnmarshaller().unmarshal(file);
    }

    private void saveData(XmlDataWrapper wrapper) throws JAXBException {
        File file = new File(XmlConfig.getFilePath(Task.class));
        XmlConfig.getMarshaller().marshal(wrapper, file);
    }
}
