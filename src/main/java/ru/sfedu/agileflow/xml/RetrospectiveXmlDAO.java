package ru.sfedu.agileflow.xml;

import jakarta.xml.bind.JAXBException;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.XmlConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.dao.GenericDAO;
import ru.sfedu.agileflow.models.Retrospective;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO-класс для управления ретроспективами в XML хранилище.
 */
public class RetrospectiveXmlDAO implements GenericDAO<Retrospective, Integer> {
    private static final Logger log = Logger.getLogger(RetrospectiveXmlDAO.class);
    private static final AtomicInteger idGenerator = new AtomicInteger(1);

    /**
     * Создает новую ретроспективу в XML хранилище.
     * @param retrospective Ретроспектива для сохранения
     */
    @Override
    public void create(Retrospective retrospective) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, retrospective.toString()));

        try {
            XmlDataWrapper wrapper = loadData();
            retrospective.setId(idGenerator.getAndIncrement());
            wrapper.getRetrospectives().add(retrospective);
            saveData(wrapper);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Ретроспектива сохранена с ID: " + retrospective.getId()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать ретроспективу: " + e.getMessage()));
            throw new RuntimeException("Не удалось создать ретроспективу", e);
        }
    }

    /**
     * Находит ретроспективу по идентификатору.
     * @param id Идентификатор ретроспективы
     * @return Optional с ретроспективой, если найдена, иначе пустой Optional
     */
    @Override
    public Optional<Retrospective> findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            XmlDataWrapper wrapper = loadData();
            Retrospective retrospective = wrapper.getRetrospectives().stream()
                    .filter(r -> r.getId() == id)
                    .findFirst()
                    .orElse(null);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, retrospective != null ? "Ретроспектива найдена" : "Ретроспектива не найдена"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.ofNullable(retrospective);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти ретроспективу: " + e.getMessage()));
            throw new RuntimeException("Не удалось найти ретроспективу", e);
        }
    }

    /**
     * Возвращает список всех ретроспектив.
     * @return Список ретроспектив
     */
    @Override
    public List<Retrospective> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try {
            XmlDataWrapper wrapper = loadData();
            List<Retrospective> retrospectives = wrapper.getRetrospectives();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено ретроспектив: " + retrospectives.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return retrospectives;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить ретроспективы: " + e.getMessage()));
            throw new RuntimeException("Не удалось получить ретроспективы", e);
        }
    }

    /**
     * Обновляет данные ретроспективы.
     * @param retrospective Обновленная ретроспектива
     */
    @Override
    public void update(Retrospective retrospective) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, retrospective.toString()));

        try {
            XmlDataWrapper wrapper = loadData();
            wrapper.getRetrospectives().removeIf(r -> r.getId() == retrospective.getId());
            wrapper.getRetrospectives().add(retrospective);
            saveData(wrapper);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Ретроспектива обновлена"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить ретроспективу: " + e.getMessage()));
            throw new RuntimeException("Не удалось обновить ретроспективу", e);
        }
    }

    /**
     * Удаляет ретроспективу по идентификатору.
     * @param id Идентификатор ретроспективы
     */
    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            XmlDataWrapper wrapper = loadData();
            boolean removed = wrapper.getRetrospectives().removeIf(r -> r.getId() == id);
            saveData(wrapper);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, removed ? "Ретроспектива удалена" : "Ретроспектива не найдена"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить ретроспективу: " + e.getMessage()));
            throw new RuntimeException("Не удалось удалить ретроспективу", e);
        }
    }

    /**
     * Находит ретроспективы по идентификатору спринта.
     * @param sprintId Идентификатор спринта
     * @return Optional с ретроспективой, если найдена, иначе пустой Optional
     */
    public Optional<Retrospective> findBySprintId(int sprintId) {
        String methodName = "findBySprintId";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "sprintId: " + sprintId));

        try {
            XmlDataWrapper wrapper = loadData();
            Retrospective retrospective = wrapper.getRetrospectives().stream()
                    .filter(r -> r.getSprint() != null && r.getSprint().getId() == sprintId)
                    .findFirst()
                    .orElse(null);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, retrospective != null ? "Ретроспектива найдена" : "Ретроспектива не найдена"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.ofNullable(retrospective);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти ретроспективу по спринту: " + e.getMessage()));
            throw new RuntimeException("Не удалось найти ретроспективу по спринту", e);
        }
    }

    private XmlDataWrapper loadData() throws JAXBException {
        File file = new File(XmlConfig.getFilePath(Retrospective.class));
        if (!file.exists()) {
            return new XmlDataWrapper();
        }
        return (XmlDataWrapper) XmlConfig.getUnmarshaller().unmarshal(file);
    }

    private void saveData(XmlDataWrapper wrapper) throws JAXBException {
        File file = new File(XmlConfig.getFilePath(Retrospective.class));
        XmlConfig.getMarshaller().marshal(wrapper, file);
    }
}
