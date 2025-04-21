package ru.sfedu.agileflow.config;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.xml.XmlDataWrapper;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Класс для конфигурации и управления JAXB контекстом для работы с XML.
 */
public class XmlConfig {
    private static final Logger log = Logger.getLogger(XmlConfig.class);
    private static final String XML_STORAGE_PATH = "data/xml/";
    private static JAXBContext jaxbContext;

    static {
        String methodName = "static_initializer";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            log.info("static_initializer [1] Инициализация JAXB контекста");
            jaxbContext = JAXBContext.newInstance(
                    ru.sfedu.agileflow.models.Project.class,
                    ru.sfedu.agileflow.models.User.class,
                    ru.sfedu.agileflow.models.Sprint.class,
                    ru.sfedu.agileflow.models.Task.class,
                    ru.sfedu.agileflow.models.Retrospective.class,
                    XmlDataWrapper.class
            );
            log.info("static_initializer [2] JAXB контекст успешно инициализирован");
            Files.createDirectories(Path.of(XML_STORAGE_PATH));
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Директория для XML создана: " + XML_STORAGE_PATH));
        } catch (JAXBException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось инициализировать JAXB контекст: " + e.getMessage()));
            throw new RuntimeException("Не удалось инициализировать JAXB контекст", e);
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать директорию для XML: " + e.getMessage()));
            throw new RuntimeException("Не удалось создать директорию для XML", e);
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Получает Marshaller для сериализации объектов в XML.
     * @return Marshaller
     * @throws RuntimeException если не удалось создать Marshaller
     */
    public static Marshaller getMarshaller() {
        String methodName = "getMarshaller";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Marshaller успешно создан"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return marshaller;
        } catch (JAXBException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать Marshaller: " + e.getMessage()));
            throw new RuntimeException("Не удалось создать Marshaller", e);
        }
    }

    /**
     * Получает Unmarshaller для десериализации XML в объекты.
     * @return Unmarshaller
     * @throws RuntimeException если не удалось создать Unmarshaller
     */
    public static Unmarshaller getUnmarshaller() {
        String methodName = "getUnmarshaller";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Unmarshaller успешно создан"));
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return unmarshaller;
        } catch (JAXBException e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось создать Unmarshaller: " + e.getMessage()));
            throw new RuntimeException("Не удалось создать Unmarshaller", e);
        }
    }

    /**
     * Возвращает путь к файлу XML для указанного класса.
     * @param clazz Класс сущности
     * @return Путь к файлу
     */
    public static String getFilePath(Class<?> clazz) {
        return XML_STORAGE_PATH + clazz.getSimpleName().toLowerCase() + "s.xml";
    }
}