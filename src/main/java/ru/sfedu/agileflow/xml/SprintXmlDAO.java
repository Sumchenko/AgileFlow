package ru.sfedu.agileflow.xml;

import jakarta.xml.bind.JAXBException;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.XmlConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.dao.GenericDAO;
import ru.sfedu.agileflow.models.Sprint;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO-класс для управления спринтами в XML хранилище.
 */
public class SprintXmlDAO implements GenericDAO<Sprint, Integer> {
    private static final Logger log = Logger.getLogger(SprintXmlDAO.class);
    private static final AtomicInteger idGenerator = new AtomicInteger(1);

    /**
     * Создает новый спринт в XML хранилище.
     * @param sprint Спринт для сохранения
     */
    @Override
    public void create(Sprint sprint) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, sprint.toString()));

        try {
            XmlDataWrapper wrapper = loadData();
            sprint.setId(idGenerator.getAndIncrement());
            wrapper.getSprints().add(sprint);
            saveData(wrapper);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Спринт сохранен с ID: " + sprint.getId()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать спринт: " + e.getMessage()));
            throw new RuntimeException("Не удалось создать спринт", e);
        }
    }

    /**
     * Находит спринт по идентификатору.
     * @param id Идентификатор спринта
     * @return Optional с спринтом, если найден, иначе пустой Optional
     */
    @Override
    public Optional<Sprint> findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            XmlDataWrapper wrapper = loadData();
            Sprint sprint = wrapper.getSprints().stream()
                    .filter(s -> s.getId() == id)
                    .findFirst()
                    .orElse(null);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, sprint != null ? "Спринт найден" : "Спринт не найден"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.ofNullable(sprint);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти спринт: " + e.getMessage()));
            throw new RuntimeException("Не удалось найти спринт", e);
        }
    }

    /**
     * Возвращает список всех спринтов.
     * @return Список спринтов
     */
    @Override
    public List<Sprint> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try {
            XmlDataWrapper wrapper = loadData();
            List<Sprint> sprints = wrapper.getSprints();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено спринтов: " + sprints.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return sprints;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить спринты: " + e.getMessage()));
            throw new RuntimeException("Не удалось получить спринты", e);
        }
    }

    /**
     * Обновляет данные спринта.
     * @param sprint Обновленный спринт
     */
    @Override
    public void update(Sprint sprint) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, sprint.toString()));

        try {
            XmlDataWrapper wrapper = loadData();
            wrapper.getSprints().removeIf(s -> s.getId() == sprint.getId());
            wrapper.getSprints().add(sprint);
            saveData(wrapper);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Спринт обновлен"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить спринт: " + e.getMessage()));
            throw new RuntimeException("Не удалось обновить спринт", e);
        }
    }

    /**
     * Удаляет спринт по идентификатору.
     * @param id Идентификатор спринта
     */
    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            XmlDataWrapper wrapper = loadData();
            boolean removed = wrapper.getSprints().removeIf(s -> s.getId() == id);
            saveData(wrapper);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, removed ? "Спринт удален" : "Спринт не найден"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить спринт: " + e.getMessage()));
            throw new RuntimeException("Не удалось удалить спринт", e);
        }
    }

    /**
     * Находит спринты по дате начала.
     * @param startDate Дата начала спринта
     * @return Список найденных спринтов
     */
    public List<Sprint> findByStartDate(Date startDate) {
        String methodName = "findByStartDate";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "startDate: " + startDate));

        try {
            XmlDataWrapper wrapper = loadData();
            List<Sprint> sprints = wrapper.getSprints().stream()
                    .filter(s -> s.getStartDate().equals(startDate))
                    .toList();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено спринтов: " + sprints.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return sprints;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти спринты по дате начала: " + e.getMessage()));
            throw new RuntimeException("Не удалось найти спринты по дате начала", e);
        }
    }

    private XmlDataWrapper loadData() throws JAXBException {
        File file = new File(XmlConfig.getFilePath(Sprint.class));
        if (!file.exists()) {
            return new XmlDataWrapper();
        }
        return (XmlDataWrapper) XmlConfig.getUnmarshaller().unmarshal(file);
    }

    private void saveData(XmlDataWrapper wrapper) throws JAXBException {
        File file = new File(XmlConfig.getFilePath(Sprint.class));
        XmlConfig.getMarshaller().marshal(wrapper, file);
    }
}
