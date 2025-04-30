//package ru.sfedu.agileflow.lab1;
//
//import org.apache.log4j.Logger;
//import org.hibernate.SessionFactory;
//import org.hibernate.boot.MetadataSources;
//import org.hibernate.boot.registry.StandardServiceRegistry;
//import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
//import org.hibernate.cfg.Configuration;
//import ru.sfedu.agileflow.constants.Constants;
//import ru.sfedu.agileflow.models.Project;
//import ru.sfedu.agileflow.models.Retrospective;
//import ru.sfedu.agileflow.models.Sprint;
//import ru.sfedu.agileflow.models.Task;
//import ru.sfedu.agileflow.models.User;
//
//import java.io.File;
//
///**
// * Утилитный класс для инициализации и предоставления Hibernate SessionFactory.
// */
//public class HibernateUtil {
//    private static final Logger log = Logger.getLogger(HibernateUtil.class);
//    private static SessionFactory sessionFactory;
//    private static final String DEFAULT_CONFIG_PATH = "hibernate.cfg.xml";
//    private static final String CONFIG_PROPERTY_KEY = "hibernate.config.path";
//
//    /**
//     * Инициализирует и возвращает SessionFactory.
//     * Использует конфигурационный файл по умолчанию или из системных свойств.
//     * @return SessionFactory для работы с Hibernate
//     * @throws RuntimeException если не удалось инициализировать SessionFactory
//     */
//    public static synchronized SessionFactory getSessionFactory() {
//        String methodName = "getSessionFactory";
//        log.info(String.format(Constants.LOG_METHOD_START, methodName));
//
//        if (sessionFactory == null || sessionFactory.isClosed()) {
//            try {
//                log.info("getSessionFactory [1] Загрузка конфигурации Hibernate");
//                Configuration configuration = new Configuration();
//
//                // Проверяем наличие системного свойства для пути к конфигурации
//                String configPath = System.getProperty(CONFIG_PROPERTY_KEY, DEFAULT_CONFIG_PATH);
//                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Используемый путь конфигурации: " + configPath));
//
//                if (new File(configPath).exists()) {
//                    configuration.configure(new File(configPath));
//                } else {
//                    configuration.configure(configPath);
//                }
//
//                log.info("getSessionFactory [2] Регистрация аннотированных классов");
//                StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
//                        .applySettings(configuration.getProperties())
//                        .build();
//
//                MetadataSources metadataSources = new MetadataSources(serviceRegistry);
//                metadataSources.addAnnotatedClass(User.class);
//                metadataSources.addAnnotatedClass(Project.class);
//                metadataSources.addAnnotatedClass(Sprint.class);
//                metadataSources.addAnnotatedClass(Task.class);
//                metadataSources.addAnnotatedClass(Retrospective.class);
//
//                log.info("getSessionFactory [3] Создание SessionFactory");
//                sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
//                log.info(String.format(Constants.LOG_METHOD_END, methodName));
//            } catch (Exception e) {
//                log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось инициализировать SessionFactory: " + e.getMessage()), e);
//                throw new RuntimeException("Не удалось инициализировать SessionFactory", e);
//            }
//        }
//        return sessionFactory;
//    }
//
//    /**
//     * Закрывает SessionFactory и освобождает ресурсы.
//     */
//    public static void close() {
//        String methodName = "close";
//        log.info(String.format(Constants.LOG_METHOD_START, methodName));
//        try {
//            if (sessionFactory != null && !sessionFactory.isClosed()) {
//                log.info("close [1] Закрытие SessionFactory");
//                sessionFactory.close();
//                sessionFactory = null;
//                log.info("close [2] SessionFactory успешно закрыт");
//            } else {
//                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "SessionFactory уже закрыт или не инициализирован"));
//            }
//            log.info(String.format(Constants.LOG_METHOD_END, methodName));
//        } catch (Exception e) {
//            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось закрыть SessionFactory: " + e.getMessage()), e);
//            throw new RuntimeException("Не удалось закрыть SessionFactory", e);
//        }
//    }
//}