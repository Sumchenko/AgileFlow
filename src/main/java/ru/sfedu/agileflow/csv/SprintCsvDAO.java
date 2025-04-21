package ru.sfedu.agileflow.csv;

import org.apache.log4j.Logger;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.dao.GenericDAO;
import ru.sfedu.agileflow.models.Sprint;
import ru.sfedu.agileflow.config.CsvConfig;
import ru.sfedu.agileflow.config.CsvDateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO-класс для работы со спринтами в CSV.
 */
public class SprintCsvDAO implements GenericDAO<Sprint, Integer> {
    private static final Logger log = Logger.getLogger(SprintCsvDAO.class);
    private static final String FILE_NAME = "sprints.csv";

    @Override
    public void create(Sprint sprint) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        if (sprint == null) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Спринт не может быть null"));
            throw new IllegalArgumentException("Спринт не может быть null");
        }
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, sprint.toString()));

        try {
            List<String[]> records = CsvConfig.readCsv(FILE_NAME);
            sprint.setId(CsvConfig.generateId(FILE_NAME));
            String[] record = new String[]{
                    String.valueOf(sprint.getId()),
                    CsvDateUtil.serializeDate(sprint.getStartDate(), false),
                    CsvDateUtil.serializeDate(sprint.getEndDate(), false),
                    sprint.getProject() != null ? String.valueOf(sprint.getProject().getId()) : ""
            };
            records.add(record);
            CsvConfig.writeCsv(FILE_NAME, records);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Спринт сохранен с ID: " + sprint.getId()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать спринт: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось создать спринт", e);
        }
    }

    @Override
    public Optional<Sprint> findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            Optional<String[]> recordOpt = CsvConfig.findById(FILE_NAME, id);
            if (recordOpt.isEmpty()) {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Спринт не найден"));
                log.info(String.format(Constants.LOG_METHOD_END, methodName));
                return Optional.empty();
            }
            String[] record = recordOpt.get();
            Sprint sprint = new Sprint();
            sprint.setId(Integer.parseInt(record[0]));
            sprint.setStartDate(CsvDateUtil.deserializeDate(record[1], false));
            sprint.setEndDate(CsvDateUtil.deserializeDate(record[2], false));
            // Project будет загружен отдельно, если нужен
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Спринт найден: " + sprint));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.of(sprint);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти спринт: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось найти спринт", e);
        }
    }

    @Override
    public List<Sprint> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try {
            List<String[]> records = CsvConfig.readCsv(FILE_NAME);
            List<Sprint> sprints = new ArrayList<>();
            for (String[] record : records) {
                try {
                    Sprint sprint = new Sprint();
                    sprint.setId(Integer.parseInt(record[0]));
                    sprint.setStartDate(CsvDateUtil.deserializeDate(record[1], false));
                    sprint.setEndDate(CsvDateUtil.deserializeDate(record[2], false));
                    // Project будет загружен отдельно, если нужен
                    sprints.add(sprint);
                } catch (NumberFormatException e) {
                    // Пропускаем некорректные записи
                }
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено спринтов: " + sprints.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return sprints;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить спринты: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось получить спринты", e);
        }
    }

    @Override
    public void update(Sprint sprint) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        if (sprint == null) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Спринт не может быть null"));
            throw new IllegalArgumentException("Спринт не может быть null");
        }
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, sprint.toString()));

        try {
            List<String[]> records = CsvConfig.readCsv(FILE_NAME);
            boolean found = false;
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i)[0].equals(String.valueOf(sprint.getId()))) {
                    records.set(i, new String[]{
                            String.valueOf(sprint.getId()),
                            CsvDateUtil.serializeDate(sprint.getStartDate(), false),
                            CsvDateUtil.serializeDate(sprint.getEndDate(), false),
                            sprint.getProject() != null ? String.valueOf(sprint.getProject().getId()) : ""
                    });
                    found = true;
                    break;
                }
            }
            if (!found) {
                log.error(String.format(Constants.LOG_ERROR, methodName, "Спринт с ID " + sprint.getId() + " не найден"));
                throw new RuntimeException("Спринт не найден");
            }
            CsvConfig.writeCsv(FILE_NAME, records);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Спринт обновлен"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить спринт: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось обновить спринт", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            List<String[]> records = CsvConfig.readCsv(FILE_NAME);
            records.removeIf(record -> record[0].equals(String.valueOf(id)));
            CsvConfig.writeCsv(FILE_NAME, records);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Спринт удален"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить спринт: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось удалить спринт", e);
        }
    }
}