package ru.sfedu.agileflow.xml;

import jakarta.xml.bind.JAXBException;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.config.XmlConfig;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.dao.GenericDAO;
import ru.sfedu.agileflow.models.User;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO-класс для управления пользователями в XML хранилище.
 */
public class UserXmlDAO implements GenericDAO<User, Integer> {
    private static final Logger log = Logger.getLogger(UserXmlDAO.class);
    private static final AtomicInteger idGenerator = new AtomicInteger(1);

    /**
     * Создает нового пользователя в XML хранилище.
     * @param user Пользователь для сохранения
     */
    @Override
    public void create(User user) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, user.toString()));

        try {
            XmlDataWrapper wrapper = loadData();
            user.setId(idGenerator.getAndIncrement());
            wrapper.getUsers().add(user);
            saveData(wrapper);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Пользователь сохранен с ID: " + user.getId()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать пользователя: " + e.getMessage()));
            throw new RuntimeException("Не удалось создать пользователя", e);
        }
    }

    /**
     * Находит пользователя по идентификатору.
     * @param id Идентификатор пользователя
     * @return Optional с пользователем, если найден, иначе пустой Optional
     */
    @Override
    public Optional<User> findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            XmlDataWrapper wrapper = loadData();
            User user = wrapper.getUsers().stream()
                    .filter(u -> u.getId() == id)
                    .findFirst()
                    .orElse(null);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, user != null ? "Пользователь найден" : "Пользователь не найден"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.ofNullable(user);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти пользователя: " + e.getMessage()));
            throw new RuntimeException("Не удалось найти пользователя", e);
        }
    }

    /**
     * Возвращает список всех пользователей.
     * @return Список пользователей
     */
    @Override
    public List<User> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try {
            XmlDataWrapper wrapper = loadData();
            List<User> users = wrapper.getUsers();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено пользователей: " + users.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return users;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить пользователей: " + e.getMessage()));
            throw new RuntimeException("Не удалось получить пользователей", e);
        }
    }

    /**
     * Обновляет данные пользователя.
     * @param user Обновленный пользователь
     */
    @Override
    public void update(User user) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, user.toString()));

        try {
            XmlDataWrapper wrapper = loadData();
            wrapper.getUsers().removeIf(u -> u.getId() == user.getId());
            wrapper.getUsers().add(user);
            saveData(wrapper);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Пользователь обновлен"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить пользователя: " + e.getMessage()));
            throw new RuntimeException("Не удалось обновить пользователя", e);
        }
    }

    /**
     * Удаляет пользователя по идентификатору.
     * @param id Идентификатор пользователя
     */
    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            XmlDataWrapper wrapper = loadData();
            boolean removed = wrapper.getUsers().removeIf(u -> u.getId() == id);
            saveData(wrapper);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, removed ? "Пользователь удален" : "Пользователь не найден"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить пользователя: " + e.getMessage()));
            throw new RuntimeException("Не удалось удалить пользователя", e);
        }
    }

    /**
     * Находит пользователя по email.
     * @param email Email пользователя
     * @return Optional с пользователем, если найден, иначе пустой Optional
     */
    public Optional<User> findByEmail(String email) {
        String methodName = "findByEmail";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "email: " + email));

        try {
            XmlDataWrapper wrapper = loadData();
            User user = wrapper.getUsers().stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(email))
                    .findFirst()
                    .orElse(null);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, user != null ? "Пользователь найден" : "Пользователь не найден"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.ofNullable(user);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти пользователя по email: " + e.getMessage()));
            throw new RuntimeException("Не удалось найти пользователя по email", e);
        }
    }

    private XmlDataWrapper loadData() throws JAXBException {
        File file = new File(XmlConfig.getFilePath(User.class));
        if (!file.exists()) {
            return new XmlDataWrapper();
        }
        return (XmlDataWrapper) XmlConfig.getUnmarshaller().unmarshal(file);
    }

    private void saveData(XmlDataWrapper wrapper) throws JAXBException {
        File file = new File(XmlConfig.getFilePath(User.class));
        XmlConfig.getMarshaller().marshal(wrapper, file);
    }
}
