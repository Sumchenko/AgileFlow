package ru.sfedu.agileflow.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.constants.Constants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Утилитный класс для работы с CSV-файлами.
 */
public class CsvUtil {
    private static final Logger log = Logger.getLogger(CsvUtil.class);
    private static final String CSV_DIR = "src/main/resources/dataCSV";

    // Определение заголовков для каждого CSV-файла
    private static final Map<String, String[]> CSV_HEADERS = new HashMap<>();

    static {
        CSV_HEADERS.put("projects.csv", new String[]{"id", "name", "description"});
        CSV_HEADERS.put("users.csv", new String[]{"id", "name", "email", "bio", "isActive", "lastLogin", "dateJoined"});
        CSV_HEADERS.put("sprints.csv", new String[]{"id", "startDate", "endDate", "projectId"});
        CSV_HEADERS.put("tasks.csv", new String[]{"id", "title", "description", "status", "priority", "sprintId", "assignedUserId"});
        CSV_HEADERS.put("retrospectives.csv", new String[]{"id", "sprintId", "summary"});
        CSV_HEADERS.put("retrospective_improvements.csv", new String[]{"retrospectiveId", "improvement"});
        CSV_HEADERS.put("retrospective_positives.csv", new String[]{"retrospectiveId", "positive"});
        CSV_HEADERS.put("project_users.csv", new String[]{"projectId", "userId"});
    }

    /**
     * Инициализирует CSV-файл, если он не существует.
     * @param fileName Имя файла
     */
    private static void initializeCsvIfNotExists(String fileName) {
        String methodName = "initializeCsvIfNotExists";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        Path filePath = Paths.get(CSV_DIR, fileName);

        try {
            if (!Files.exists(filePath)) {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "CSV-файл не существует, создается: " + fileName));
                Files.createDirectories(filePath.getParent());
                try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
                    String[] header = CSV_HEADERS.getOrDefault(fileName, new String[]{});
                    if (header.length > 0) {
                        writer.writeNext(header);
                        log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Заголовок записан: " + String.join(",", header)));
                    }
                }
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "CSV-файл создан: " + fileName));
            }
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (IOException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать CSV-файл: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось создать CSV-файл", e);
        }
    }

    /**
     * Читает все записи из CSV-файла.
     * @param fileName Имя файла
     * @return Список строк, представляющих записи
     */
    public static List<String[]> readCsv(String fileName) {
        String methodName = "readCsv";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        initializeCsvIfNotExists(fileName);
        List<String[]> records = new ArrayList<>();
        Path filePath = Paths.get(CSV_DIR, fileName);

        try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
            log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
            String[] record;
            while ((record = reader.readNext()) != null) {
                records.add(record);
            }
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Прочитано записей: " + records.size()));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        } catch (IOException | CsvValidationException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось прочитать CSV-файл: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось прочитать CSV-файл", e);
        }
        return records;
    }

    /**
     * Записывает записи в CSV-файл.
     * @param fileName Имя файла
     * @param records Список записей
     */
    public static void writeCsv(String fileName, List<String[]> records) {
        String methodName = "writeCsv";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        initializeCsvIfNotExists(fileName);
        Path filePath = Paths.get(CSV_DIR, fileName);

        try {
            Files.createDirectories(filePath.getParent());
            try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
                log.info(String.format(Constants.LOG_DB_OPERATION, methodName));
                // Записываем заголовок
                String[] header = CSV_HEADERS.getOrDefault(fileName, new String[]{});
                if (header.length > 0 && records.isEmpty()) {
                    writer.writeNext(header);
                } else if (header.length > 0) {
                    writer.writeNext(header);
                    writer.writeAll(records);
                } else {
                    writer.writeAll(records);
                }
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Записано записей: " + records.size()));
                log.info(String.format(Constants.LOG_METHOD_END, methodName));
            }
        } catch (IOException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось записать CSV-файл: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось записать CSV-файл", e);
        }
    }

    /**
     * Генерирует новый идентификатор для сущности.
     * @param fileName Имя файла
     * @return Новый идентификатор
     */
    public static int generateId(String fileName) {
        String methodName = "generateId";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        List<String[]> records = readCsv(fileName);
        int maxId = 0;
        for (String[] record : records) {
            try {
                int id = Integer.parseInt(record[0]);
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException e) {
                // Пропускаем некорректные записи
            }
        }
        int newId = maxId + 1;
        log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Сгенерирован ID: " + newId));
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
        return newId;
    }

    /**
     * Находит запись по идентификатору.
     * @param fileName Имя файла
     * @param id Идентификатор
     * @return Optional с массивом строки, если найдено
     */
    public static Optional<String[]> findById(String fileName, int id) {
        String methodName = "findById";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        List<String[]> records = readCsv(fileName);
        for (String[] record : records) {
            if (record[0].equals(String.valueOf(id))) {
                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Запись найдена для ID: " + id));
                log.info(String.format(Constants.LOG_METHOD_END, methodName));
                return Optional.of(record);
            }
        }
        log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Запись не найдена для ID: " + id));
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
        return Optional.empty();
    }
}