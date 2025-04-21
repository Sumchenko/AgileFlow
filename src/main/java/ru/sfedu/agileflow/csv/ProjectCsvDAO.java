package ru.sfedu.agileflow.csv;

import org.apache.log4j.Logger;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.dao.GenericDAO;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.utils.CsvUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO-класс для работы с проектами в CSV.
 */
public class ProjectCsvDAO implements GenericDAO<Project, Integer> {
    private static final Logger log = Logger.getLogger(ProjectCsvDAO.class);
    private static final String FILE_NAME = "projects.csv";

    @Override
    public void create(Project project) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        if (project == null) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Проект не может быть null"));
            throw new IllegalArgumentException("Проект не может быть null");
        }
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, project.toString()));

        try {
            List<String[]> records = CsvUtil.readCsv(FILE_NAME);
            project.setId(CsvUtil.generateId(FILE_NAME));
            String[] record = new String[]{
                    String.valueOf(project.getId()),
                    project.getName(),
                    project.getDescription()
            };
            records.add(record);
            CsvUtil.writeCsv(FILE_NAME, records);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Проект сохранен с ID: " + project.getId()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать проект: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось создать проект", e);
        }
    }

    @Override
    public Optional<Project> findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            Optional<String[]> recordOpt = CsvUtil.findById(FILE_NAME, id);
            if (recordOpt.isEmpty()) {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Проект не найден"));
                log.info(String.format(Constants.LOG_METHOD_END, methodName));
                return Optional.empty();
            }
            String[] record = recordOpt.get();
            Project project = new Project();
            project.setId(Integer.parseInt(record[0]));
            project.setName(record[1]);
            project.setDescription(record[2]);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Проект найден: " + project));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.of(project);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти проект: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось найти проект", e);
        }
    }

    @Override
    public List<Project> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try {
            List<String[]> records = CsvUtil.readCsv(FILE_NAME);
            List<Project> projects = new ArrayList<>();
            for (String[] record : records) {
                try {
                    Project project = new Project();
                    project.setId(Integer.parseInt(record[0]));
                    project.setName(record[1]);
                    project.setDescription(record[2]);
                    projects.add(project);
                } catch (NumberFormatException e) {
                    // Пропускаем некорректные записи
                }
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено проектов: " + projects.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return projects;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить проекты: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось получить проекты", e);
        }
    }

    @Override
    public void update(Project project) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        if (project == null) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Проект не может быть null"));
            throw new IllegalArgumentException("Проект не может быть null");
        }
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, project.toString()));

        try {
            List<String[]> records = CsvUtil.readCsv(FILE_NAME);
            boolean found = false;
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i)[0].equals(String.valueOf(project.getId()))) {
                    records.set(i, new String[]{
                            String.valueOf(project.getId()),
                            project.getName(),
                            project.getDescription()
                    });
                    found = true;
                    break;
                }
            }
            if (!found) {
                log.error(String.format(Constants.LOG_ERROR, methodName, "Проект с ID " + project.getId() + " не найден"));
                throw new RuntimeException("Проект не найден");
            }
            CsvUtil.writeCsv(FILE_NAME, records);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Проект обновлен"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить проект: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось обновить проект", e);
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
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Проект удален"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить проект: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось удалить проект", e);
        }
    }
}