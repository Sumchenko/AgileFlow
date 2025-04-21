package ru.sfedu.agileflow.config;

import org.apache.log4j.Logger;
import ru.sfedu.agileflow.constants.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Утилитный класс для работы с датами в CSV.
 */
public class CsvDateUtil {
    private static final Logger log = Logger.getLogger(CsvDateUtil.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Сериализует дату в строку для CSV.
     * @param date Дата
     * @param isTimestamp Флаг, указывающий, использовать ли формат с временем
     * @return Сериализованная строка
     */
    public static String serializeDate(Date date, boolean isTimestamp) {
        if (date == null) {
            return "";
        }
        return isTimestamp ? TIMESTAMP_FORMAT.format(date) : DATE_FORMAT.format(date);
    }

    /**
     * Десериализует строку в дату.
     * @param dateStr Строка даты
     * @param isTimestamp Флаг, указывающий, использовать ли формат с временем
     * @return Десериализованная дата
     */
    public static Date deserializeDate(String dateStr, boolean isTimestamp) {
        String methodName = "deserializeDate";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        if (dateStr == null || dateStr.isEmpty()) {
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Строка даты пустая"));
            return null;
        }
        try {
            Date date = isTimestamp ? TIMESTAMP_FORMAT.parse(dateStr) : DATE_FORMAT.parse(dateStr);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Дата десериализована: " + date));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return date;
        } catch (ParseException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось десериализовать дату: " + e.getMessage()), e);
            throw new RuntimeException("Не удалось десериализовать дату", e);
        }
    }
}
