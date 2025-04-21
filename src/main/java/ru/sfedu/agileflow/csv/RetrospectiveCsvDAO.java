package ru.sfedu.agileflow.csv;

import org.apache.log4j.Logger;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.dao.GenericDAO;
import ru.sfedu.agileflow.models.Retrospective;
import ru.sfedu.agileflow.config.CsvConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DAO-класс для работы с ретроспективами в CSV.
 */
public class RetrospectiveCsvDAO implements GenericDAO<Retrospective, Integer> {
    private static final Logger log = Logger.getLogger(RetrospectiveCsvDAO.class);
    private static final String FILE_NAME = "retrospectives.csv";
    private static final String IMPROVEMENTS_FILE = "retrospective_improvements.csv";
    private static final String POSITIVES_FILE = "retrospective_positives.csv";

    @Override
    public void create(Retrospective retrospective) {
        String methodName = "create";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        if (retrospective == null) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Ретроспектива не может быть null"));
            throw new IllegalArgumentException("Ретроспектива не может быть null");
        }
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, retrospective.toString()));

        try {
            // Сохранение ретроспективы
            List<String[]> records = CsvConfig.readCsv(FILE_NAME);
            retrospective.setId(CsvConfig.generateId(FILE_NAME));
            String[] record = new String[]{
                    String.valueOf(retrospective.getId()),
                    retrospective.getSprint() != null ? String.valueOf(retrospective.getSprint().getId()) : "",
                    retrospective.getSummary()
            };
            records.add(record);
            CsvConfig.writeCsv(FILE_NAME, records);

            // Сохранение improvements
            if (retrospective.getImprovements() != null && !retrospective.getImprovements().isEmpty()) {
                List<String[]> improvementRecords = CsvConfig.readCsv(IMPROVEMENTS_FILE);
                for (String improvement : retrospective.getImprovements()) {
                    improvementRecords.add(new String[]{
                            String.valueOf(retrospective.getId()),
                            improvement
                    });
                }
                CsvConfig.writeCsv(IMPROVEMENTS_FILE, improvementRecords);
            }

            // Сохранение positives
            if (retrospective.getPositives() != null && !retrospective.getPositives().isEmpty()) {
                List<String[]> positiveRecords = CsvConfig.readCsv(POSITIVES_FILE);
                for (String positive : retrospective.getPositives()) {
                    positiveRecords.add(new String[]{
                            String.valueOf(retrospective.getId()),
                            positive
                    });
                }
                CsvConfig.writeCsv(POSITIVES_FILE, positiveRecords);
            }

            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Ретроспектива сохранена с ID: " + retrospective.getId()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать ретроспективу: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось создать ретроспективу", e);
        }
    }

    @Override
    public Optional<Retrospective> findById(Integer id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            Optional<String[]> recordOpt = CsvConfig.findById(FILE_NAME, id);
            if (recordOpt.isEmpty()) {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Ретроспектива не найдена"));
                log.info(String.format(Constants.LOG_METHOD_END, methodName));
                return Optional.empty();
            }
            String[] record = recordOpt.get();
            Retrospective retrospective = new Retrospective();
            retrospective.setId(Integer.parseInt(record[0]));
            retrospective.setSummary(record[2]);

            // Загрузка improvements
            List<String[]> improvementRecords = CsvConfig.readCsv(IMPROVEMENTS_FILE);
            List<String> improvements = improvementRecords.stream()
                    .filter(r -> r[0].equals(String.valueOf(id)))
                    .map(r -> r[1])
                    .collect(Collectors.toList());
            retrospective.setImprovements(improvements);

            // Загрузка positives
            List<String[]> positiveRecords = CsvConfig.readCsv(POSITIVES_FILE);
            List<String> positives = positiveRecords.stream()
                    .filter(r -> r[0].equals(String.valueOf(id)))
                    .map(r -> r[1])
                    .collect(Collectors.toList());
            retrospective.setPositives(positives);

            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Ретроспектива найдена: " + retrospective));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return Optional.of(retrospective);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось найти ретроспективу: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось найти ретроспективу", e);
        }
    }

    @Override
    public List<Retrospective> findAll() {
        String methodName = "findAll";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));

        try {
            List<String[]> records = CsvConfig.readCsv(FILE_NAME);
            List<Retrospective> retrospectives = new ArrayList<>();
            for (String[] record : records) {
                try {
                    int id = Integer.parseInt(record[0]);
                    Retrospective retrospective = new Retrospective();
                    retrospective.setId(id);
                    retrospective.setSummary(record[2]);

                    // Загрузка improvements
                    List<String[]> improvementRecords = CsvConfig.readCsv(IMPROVEMENTS_FILE);
                    List<String> improvements = improvementRecords.stream()
                            .filter(r -> r[0].equals(String.valueOf(id)))
                            .map(r -> r[1])
                            .collect(Collectors.toList());
                    retrospective.setImprovements(improvements);

                    // Загрузка positives
                    List<String[]> positiveRecords = CsvConfig.readCsv(POSITIVES_FILE);
                    List<String> positives = positiveRecords.stream()
                            .filter(r -> r[0].equals(String.valueOf(id)))
                            .map(r -> r[1])
                            .collect(Collectors.toList());
                    retrospective.setPositives(positives);

                    retrospectives.add(retrospective);
                } catch (NumberFormatException e) {
                    // Пропускаем некорректные записи
                }
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено ретроспектив: " + retrospectives.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return retrospectives;
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить ретроспективы: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось получить ретроспективы", e);
        }
    }

    @Override
    public void update(Retrospective retrospective) {
        String methodName = "update";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        if (retrospective == null) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Ретроспектива не может быть null"));
            throw new IllegalArgumentException("Ретроспектива не может быть null");
        }
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, retrospective.toString()));

        try {
            // Обновление ретроспективы
            List<String[]> records = CsvConfig.readCsv(FILE_NAME);
            boolean found = false;
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i)[0].equals(String.valueOf(retrospective.getId()))) {
                    records.set(i, new String[]{
                            String.valueOf(retrospective.getId()),
                            retrospective.getSprint() != null ? String.valueOf(retrospective.getSprint().getId()) : "",
                            retrospective.getSummary()
                    });
                    found = true;
                    break;
                }
            }
            if (!found) {
                log.error(String.format(Constants.LOG_ERROR, methodName, "Ретроспектива с ID " + retrospective.getId() + " не найдена"));
                throw new RuntimeException("Ретроспектива не найдена");
            }
            CsvConfig.writeCsv(FILE_NAME, records);

            // Обновление improvements
            List<String[]> improvementRecords = CsvConfig.readCsv(IMPROVEMENTS_FILE);
            improvementRecords.removeIf(r -> r[0].equals(String.valueOf(retrospective.getId())));
            if (retrospective.getImprovements() != null) {
                for (String improvement : retrospective.getImprovements()) {
                    improvementRecords.add(new String[]{
                            String.valueOf(retrospective.getId()),
                            improvement
                    });
                }
            }
            CsvConfig.writeCsv(IMPROVEMENTS_FILE, improvementRecords);

            // Обновление positives
            List<String[]> positiveRecords = CsvConfig.readCsv(POSITIVES_FILE);
            positiveRecords.removeIf(r -> r[0].equals(String.valueOf(retrospective.getId())));
            if (retrospective.getPositives() != null) {
                for (String positive : retrospective.getPositives()) {
                    positiveRecords.add(new String[]{
                            String.valueOf(retrospective.getId()),
                            positive
                    });
                }
            }
            CsvConfig.writeCsv(POSITIVES_FILE, positiveRecords);

            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Ретроспектива обновлена"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось обновить ретроспективу: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось обновить ретроспективу", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String methodName = "delete";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "id: " + id));

        try {
            // Удаление ретроспективы
            List<String[]> records = CsvConfig.readCsv(FILE_NAME);
            records.removeIf(record -> record[0].equals(String.valueOf(id)));
            CsvConfig.writeCsv(FILE_NAME, records);

            // Удаление improvements
            List<String[]> improvementRecords = CsvConfig.readCsv(IMPROVEMENTS_FILE);
            improvementRecords.removeIf(r -> r[0].equals(String.valueOf(id)));
            CsvConfig.writeCsv(IMPROVEMENTS_FILE, improvementRecords);

            // Удаление positives
            List<String[]> positiveRecords = CsvConfig.readCsv(POSITIVES_FILE);
            positiveRecords.removeIf(r -> r[0].equals(String.valueOf(id)));
            CsvConfig.writeCsv(POSITIVES_FILE, positiveRecords);

            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Ретроспектива удалена"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось удалить ретроспективу: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось удалить ретроспективу", e);
        }
    }
}