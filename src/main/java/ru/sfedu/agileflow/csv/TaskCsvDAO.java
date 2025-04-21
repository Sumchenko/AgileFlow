package ru.sfedu.agileflow.csv;

import org.apache.log4j.Logger;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.dao.GenericDAO;
import ru.sfedu.agileflow.models.Task;
import ru.sfedu.agileflow.models.TaskStatus;
import ru.sfedu.agileflow.utils.CsvUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO-класс для работы с задачами в CSV.
 */
public class TaskCsvDAO implements GenericDAO<Task, Integer> {
    private static final Logger log = Logger.getLogger(TaskCsvDAO.class);
    private static final String FILE_NAME = "tasks.csv";

    @Override
    public void create(Task task) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        if (task == null) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Задача не может быть null"));
            throw new IllegalArgumentException("Задача не может быть null");
        }
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, task.toString()));

        try {
            List<String[]> records = CsvUtil.readCsv(FILE_NAME);
            task.setId(CsvUtil.generateId(FILE_NAME));
            String[] record = new String[]{
                    String.valueOf(task.getId()),
                    task.getTitle(),
                    task.getDescription() != null ? task.getDescription() : "",
                    task.getStatus().name(),
                    String.valueOf(task.getPriority()),
                    task.getSprint() != null ? String.valueOf(task.getSprint().getId()) : "",
                    task.getAssignedUser() != null ? String.valueOf(task.getAssignedUser().getId()) : ""
            };
            records.add(record);
            CsvUtil.writeCsv(FILE_NAME, records);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Задача сохранена с ID: " + task.getId()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать задачу: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось создать задачу", e);
        }
    }

    @Override
    public Optional<Task> findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            Optional<String[]> recordOpt = CsvUtil.findById(FILE_NAME, id);
            if (recordOpt.isEmpty()) {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Задача не найдена"));
                log.info(String.format(Constants.LOG_METHOD_END, methodName));
                return Optional.empty();
            }
            String[] record = recordOpt.get();
            Task task = new Task();
            task.setId(Integer.parseInt(record[0]));
            task.setTitle(record[1]);
            task.setDescription(record[2]);
            task.setStatus(TaskStatus.valueOf(record[3]));
            task.setPriority(Integer.parseInt(record[4]));
            // Sprint и User будут загружены отдельно, если нужны
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Задача найдена: " + task));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.of(task);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти задачу: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось найти задачу", e);
        }
    }

    @Override
    public List<Task> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try {
            List<String[]> records = CsvUtil.readCsv(FILE_NAME);
            List<Task> tasks = new ArrayList<>();
            for (String[] record : records) {
                try {
                    Task task = new Task();
                    task.setId(Integer.parseInt(record[0]));
                    task.setTitle(record[1]);
                    task.setDescription(record[2]);
                    task.setStatus(TaskStatus.valueOf(record[3]));
                    task.setPriority(Integer.parseInt(record[4]));
                    // Sprint и User будут загружены отдельно, если нужны
                    tasks.add(task);
                } catch (IllegalArgumentException e) {
                    // Пропускаем некорректные записи
                }
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено задач: " + tasks.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return tasks;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить задачи: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось получить задачи", e);
        }
    }

    @Override
    public void update(Task task) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        if (task == null) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Задача не может быть null"));
            throw new IllegalArgumentException("Задача не может быть null");
        }
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, task.toString()));

        try {
            List<String[]> records = CsvUtil.readCsv(FILE_NAME);
            boolean found = false;
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i)[0].equals(String.valueOf(task.getId()))) {
                    records.set(i, new String[]{
                            String.valueOf(task.getId()),
                            task.getTitle(),
                            task.getDescription() != null ? task.getDescription() : "",
                            task.getStatus().name(),
                            String.valueOf(task.getPriority()),
                            task.getSprint() != null ? String.valueOf(task.getSprint().getId()) : "",
                            task.getAssignedUser() != null ? String.valueOf(task.getAssignedUser().getId()) : ""
                    });
                    found = true;
                    break;
                }
            }
            if (!found) {
                log.error(String.format(Constants.LOG_ERROR, methodName, "Задача с ID " + task.getId() + " не найдена"));
                throw new RuntimeException("Задача не найдена");
            }
            CsvUtil.writeCsv(FILE_NAME, records);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Задача обновлена"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить задачу: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось обновить задачу", e);
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
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Задача удалена"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить задачу: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось удалить задачу", e);
        }
    }
}