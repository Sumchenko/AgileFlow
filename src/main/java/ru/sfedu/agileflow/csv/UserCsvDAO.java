package ru.sfedu.agileflow.csv;

import org.apache.log4j.Logger;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.dao.GenericDAO;
import ru.sfedu.agileflow.models.User;
import ru.sfedu.agileflow.utils.CsvUtil;
import ru.sfedu.agileflow.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO-класс для работы с пользователями в CSV.
 */
public class UserCsvDAO implements GenericDAO<User, Integer> {
    private static final Logger log = Logger.getLogger(UserCsvDAO.class);
    private static final String FILE_NAME = "users.csv";

    @Override
    public void create(User user) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        if (user == null) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Пользователь не может быть null"));
            throw new IllegalArgumentException("Пользователь не может быть null");
        }
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, user.toString()));

        try {
            List<String[]> records = CsvUtil.readCsv(FILE_NAME);
            user.setId(CsvUtil.generateId(FILE_NAME));
            String[] record = new String[]{
                    String.valueOf(user.getId()),
                    user.getName(),
                    user.getEmail(),
                    user.getBio() != null ? user.getBio() : "",
                    String.valueOf(user.isActive()),
                    DateUtil.serializeDate(user.getLastLogin(), true),
                    DateUtil.serializeDate(user.getDateJoined(), true)
            };
            records.add(record);
            CsvUtil.writeCsv(FILE_NAME, records);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Пользователь сохранен с ID: " + user.getId()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать пользователя: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось создать пользователя", e);
        }
    }

    @Override
    public Optional<User> findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            Optional<String[]> recordOpt = CsvUtil.findById(FILE_NAME, id);
            if (recordOpt.isEmpty()) {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Пользователь не найден"));
                log.info(String.format(Constants.LOG_METHOD_END, methodName));
                return Optional.empty();
            }
            String[] record = recordOpt.get();
            User user = new User();
            user.setId(Integer.parseInt(record[0]));
            user.setName(record[1]);
            user.setEmail(record[2]);
            user.setBio(record[3]);
            user.setActive(Boolean.parseBoolean(record[4]));
            user.setLastLogin(DateUtil.deserializeDate(record[5], true));
            user.setDateJoined(DateUtil.deserializeDate(record[6], true));
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Пользователь найден: " + user));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.of(user);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти пользователя: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось найти пользователя", e);
        }
    }

    @Override
    public List<User> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try {
            List<String[]> records = CsvUtil.readCsv(FILE_NAME);
            List<User> users = new ArrayList<>();
            for (String[] record : records) {
                try {
                    User user = new User();
                    user.setId(Integer.parseInt(record[0]));
                    user.setName(record[1]);
                    user.setEmail(record[2]);
                    user.setBio(record[3]);
                    user.setActive(Boolean.parseBoolean(record[4]));
                    user.setLastLogin(DateUtil.deserializeDate(record[5], true));
                    user.setDateJoined(DateUtil.deserializeDate(record[6], true));
                    users.add(user);
                } catch (NumberFormatException e) {
                    // Пропускаем некорректные записи
                }
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено пользователей: " + users.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return users;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить пользователей: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось получить пользователей", e);
        }
    }

    @Override
    public void update(User user) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        if (user == null) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Пользователь не может быть null"));
            throw new IllegalArgumentException("Пользователь не может быть null");
        }
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, user.toString()));

        try {
            List<String[]> records = CsvUtil.readCsv(FILE_NAME);
            boolean found = false;
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i)[0].equals(String.valueOf(user.getId()))) {
                    records.set(i, new String[]{
                            String.valueOf(user.getId()),
                            user.getName(),
                            user.getEmail(),
                            user.getBio() != null ? user.getBio() : "",
                            String.valueOf(user.isActive()),
                            DateUtil.serializeDate(user.getLastLogin(), true),
                            DateUtil.serializeDate(user.getDateJoined(), true)
                    });
                    found = true;
                    break;
                }
            }
            if (!found) {
                log.error(String.format(Constants.LOG_ERROR, methodName, "Пользователь с ID " + user.getId() + " не найден"));
                throw new RuntimeException("Пользователь не найден");
            }
            CsvUtil.writeCsv(FILE_NAME, records);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Пользователь обновлен"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить пользователя: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось обновить пользователя", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            List<String[]> records = CsvUtil.readCsv(FILE_NAME);
            records.removeIf(record -> record[0].equals(String.valueOf(id)));
            CsvUtil.writeCsv(FILE_NAME, records);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Пользователь удален"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить пользователя: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось удалить пользователя", e);
        }
    }

    /**
     * Находит пользователя по email.
     * @param email Email пользователя
     * @return Optional с пользователем, если найден
     */
    public Optional<User> findByEmail(String email) {
        String methodName = "findByEmail";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "email: " + email));

        try {
            List<User> users = findAll();
            Optional<User> userOpt = users.stream()
                    .filter(user -> user.getEmail().equals(email))
                    .findFirst();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, userOpt.isPresent() ? "Пользователь найден" : "Пользователь не найден"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return userOpt;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти пользователя по email: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось найти пользователя по email", e);
        }
    }
}