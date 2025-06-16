//package ru.sfedu.agileflow.lab2;
//
//import org.apache.log4j.Logger;
//import org.hibernate.SessionFactory;
//import org.hibernate.boot.MetadataSources;
//import org.hibernate.boot.registry.StandardServiceRegistry;
//import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
//import org.hibernate.cfg.Configuration;
//import ru.sfedu.agileflow.lab2.TestEntity;
//
//import java.io.File;
//
///**
// * Утилитный класс для инициализации Hibernate SessionFactory для лабораторной работы.
// */
//public class HibernateUtil {
//    private static final Logger log = Logger.getLogger(HibernateUtil.class);
//    private static SessionFactory sessionFactory;
//    private static final String DEFAULT_CONFIG_PATH = "lab2/hibernate-test.cfg.xml";
//    private static final String CONFIG_PROPERTY_KEY = "hibernate.lab2.config.path";
//
//    /**
//     * Инициализирует и возвращает SessionFactory.
//     *
//     * @return SessionFactory для работы с Hibernate
//     * @throws RuntimeException если не удалось инициализировать SessionFactory
//     */
//    public static synchronized SessionFactory getSessionFactory() {
//        String methodName = "getSessionFactory";
//        log.info(String.format("[1] %s: Начало выполнения метода", methodName));
//
//        if (sessionFactory == null || sessionFactory.isClosed()) {
//            try {
//                log.info("[1] getSessionFactory: Загрузка конфигурации Hibernate");
//                Configuration configuration = new Configuration();
//
//                String configPath = System.getProperty(CONFIG_PROPERTY_KEY, DEFAULT_CONFIG_PATH);
//                log.debug(String.format("[1] getSessionFactory: Используемый путь конфигурации: %s", configPath));
//
//                if (new File(configPath).exists()) {
//                    configuration.configure(new File(configPath));
//                } else {
//                    configuration.configure(configPath);
//                }
//
//                log.info("[2] getSessionFactory: Регистрация аннотированных классов");
//                StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
//                        .applySettings(configuration.getProperties())
//                        .build();
//
//                MetadataSources metadataSources = new MetadataSources(serviceRegistry);
//                metadataSources.addAnnotatedClass(TestEntity.class);
//
//                log.info("[3] getSessionFactory: Создание SessionFactory");
//                sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
//                log.info(String.format("[3] %s: Метод успешно завершен", methodName));
//            } catch (Exception e) {
//                log.error(String.format("[3] %s: Не удалось инициализировать SessionFactory: %s", methodName, e.getMessage()), e);
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
//        log.info(String.format("[1] %s: Начало выполнения метода", methodName));
//        try {
//            if (sessionFactory != null && !sessionFactory.isClosed()) {
//                log.info("[1] close: Закрытие SessionFactory");
//                sessionFactory.close();
//                sessionFactory = null;
//                log.info("[2] close: SessionFactory успешно закрыт");
//            } else {
//                log.debug(String.format("[1] close: SessionFactory уже закрыт или не инициализирован"));
//            }
//            log.info(String.format("[2] %s: Метод успешно завершен", methodName));
//        } catch (Exception e) {
//            log.error(String.format("[2] %s: Не удалось закрыть SessionFactory: %s", methodName, e.getMessage()), e);
//            throw new RuntimeException("Не удалось закрыть SessionFactory", e);
//        }
//    }
//}