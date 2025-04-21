package ru.sfedu.agileflow.xml;

import jakarta.xml.bind.JAXBException;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.XmlConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.dao.GenericDAO;
import ru.sfedu.agileflow.models.Project;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO-класс для управления проектами в XML хранилище.
 */
public class ProjectXmlDAO implements GenericDAO<Project, Integer> {
    private static final Logger log = Logger.getLogger(ProjectXmlDAO.class);
    private static final AtomicInteger idGenerator = new AtomicInteger(1);

    /**
     * Создает новый проект в XML хранилище.
     * @param project Проект для сохранения
     */
    @Override
    public void create(Project project) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, project.toString()));

        try {
            XmlDataWrapper wrapper = loadData();
            project.setId(idGenerator.getAndIncrement());
            wrapper.getProjects().add(project);
            saveData(wrapper);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Проект сохранен с ID: " + project.getId()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать проект: " + e.getMessage()));
            throw new RuntimeException("Не удалось создать проект", e);
        }
    }

    /**
     * Находит проект по идентификатору.
     * @param id Идентификатор проекта
     * @return Optional с проектом, если найден, иначе пустой Optional
     */
    @Override
    public Optional<Project> findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            XmlDataWrapper wrapper = loadData();
            Project project = wrapper.getProjects().stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElse(null);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName,
                    project != null ? "Проект найден" : "Проект не найден"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.ofNullable(project);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName,
                    "Не удалось найти проект: " + e.getMessage()));
            throw new RuntimeException("Не удалось найти проект", e);
        }
    }

    /**
     * Возвращает список всех проектов.
     * @return Список проектов
     */
    @Override
    public List<Project> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try {
            XmlDataWrapper wrapper = loadData();
            List<Project> projects = wrapper.getProjects();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName,
                    "Найдено проектов: " + projects.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return projects;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName,
                    "Не удалось получить проекты: " + e.getMessage()));
            throw new RuntimeException("Не удалось получить проекты", e);
        }
    }

    /**
     * Обновляет данные проекта.
     * @param project Обновленный проект
     */
    @Override
    public void update(Project project) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, project.toString()));

        try {
            XmlDataWrapper wrapper = loadData();
            wrapper.getProjects().removeIf(p -> p.getId() == project.getId());
            wrapper.getProjects().add(project);
            saveData(wrapper);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Проект обновлен"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName,
                    "Не удалось обновить проект: " + e.getMessage()));
            throw new RuntimeException("Не удалось обновить проект", e);
        }
    }

    /**
     * Удаляет проект по идентификатору.
     * @param id Идентификатор проекта
     */
    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            XmlDataWrapper wrapper = loadData();
            boolean removed = wrapper.getProjects().removeIf(p -> p.getId() == id);
            saveData(wrapper);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName,
                    removed ? "Проект удален" : "Проект не найден"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName,
                    "Не удалось удалить проект: " + e.getMessage()));
            throw new RuntimeException("Не удалось удалить проект", e);
        }
    }

    /**
     * Находит проекты по имени (частичное совпадение).
     * @param name Имя проекта для поиска
     * @return Список найденных проектов
     */
    public List<Project> findByName(String name) {
        String methodName = "findByName";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "name: " + name));

        try {
            XmlDataWrapper wrapper = loadData();
            List<Project> projects = wrapper.getProjects().stream()
                    .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                    .toList();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName,
                    "Найдено проектов: " + projects.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return projects;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName,
                    "Не удалось найти проекты по имени: " + e.getMessage()));
            throw new RuntimeException("Не удалось найти проекты по имени", e);
        }
    }

    private XmlDataWrapper loadData() throws JAXBException {
        File file = new File(XmlConfig.getFilePath(Project.class));
        if (!file.exists()) {
            return new XmlDataWrapper();
        }
        return (XmlDataWrapper) XmlConfig.getUnmarshaller().unmarshal(file);
    }

    private void saveData(XmlDataWrapper wrapper) throws JAXBException {
        File file = new File(XmlConfig.getFilePath(Project.class));
        XmlConfig.getMarshaller().marshal(wrapper, file);
    }
}