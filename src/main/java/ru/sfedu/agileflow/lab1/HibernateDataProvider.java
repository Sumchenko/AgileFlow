//package ru.sfedu.agileflow.lab1;
//
//import org.apache.log4j.Logger;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.query.NativeQuery;
//import ru.sfedu.agileflow.constants.Constants;
//
//import java.util.List;
//import java.util.Optional;
//
///**
// * Класс для получения служебной информации о базе данных через Hibernate Native SQL.
// */
//public class HibernateDataProvider {
//    private static final Logger log = Logger.getLogger(HibernateDataProvider.class);
//    private final SessionFactory sessionFactory;
//
//    /**
//     * Конструктор для инициализации SessionFactory.
//     */
//    public HibernateDataProvider() {
//        this.sessionFactory = HibernateUtil.getSessionFactory();
//    }
//
//    /**
//     * Получает имя базы данных.
//     * @return Optional с именем базы данных
//     * @throws RuntimeException если не удалось выполнить запрос
//     */
//    public Optional<String> getDatabaseName() {
//        String methodName = "getDatabaseName";
//        log.info(String.format(Constants.LOG_METHOD_START, methodName));
//
//        try (Session session = sessionFactory.openSession()) {
//            log.info("getDatabaseName [1] Выполнение запроса для получения имени базы данных");
//            NativeQuery<String> query = session.createNativeQuery("SELECT current_database()", String.class);
//            String result = query.getSingleResult();
//            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Имя базы данных: " + result));
//            log.info(String.format(Constants.LOG_METHOD_END, methodName));
//            return Optional.ofNullable(result);
//        } catch (Exception e) {
//            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить имя базы данных: " + e.getMessage()), e);
//            throw new RuntimeException("Не удалось получить имя базы данных", e);
//        }
//    }
//
//    /**
//     * Получает размер базы данных в мегабайтах.
//     * @return Optional с размером базы данных
//     * @throws RuntimeException если не удалось выполнить запрос
//     */
//    public Optional<Double> getDatabaseSize() {
//        String methodName = "getDatabaseSize";
//        log.info(String.format(Constants.LOG_METHOD_START, methodName));
//
//        try (Session session = sessionFactory.openSession()) {
//            log.info("getDatabaseSize [1] Выполнение запроса для получения размера базы данных");
//            NativeQuery<Double> query = session.createNativeQuery(
//                    "SELECT pg_database_size(current_database()) / 1024.0 / 1024.0", Double.class);
//            Double result = query.getSingleResult();
//            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Размер базы данных: " + result + " MB"));
//            log.info(String.format(Constants.LOG_METHOD_END, methodName));
//            return Optional.ofNullable(result);
//        } catch (Exception e) {
//            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить размер базы данных: " + e.getMessage()), e);
//            throw new RuntimeException("Не удалось получить размер базы данных", e);
//        }
//    }
//
//    /**
//     * Получает список таблиц в базе данных.
//     * @return Список имен таблиц
//     * @throws RuntimeException если не удалось выполнить запрос
//     */
//    public List<String> getTableNames() {
//        String methodName = "getTableNames";
//        log.info(String.format(Constants.LOG_METHOD_START, methodName));
//
//        try (Session session = sessionFactory.openSession()) {
//            log.info("getTableNames [1] Выполнение запроса для получения списка таблиц");
//            NativeQuery<String> query = session.createNativeQuery(
//                    "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'", String.class);
//            List<String> result = query.getResultList();
//            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Список таблиц: " + result));
//            log.info(String.format(Constants.LOG_METHOD_END, methodName));
//            return result;
//        } catch (Exception e) {
//            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить список таблиц: " + e.getMessage()), e);
//            throw new RuntimeException("Не удалось получить список таблиц", e);
//        }
//    }
//
//    /**
//     * Получает версию сервера PostgreSQL.
//     * @return Optional с версией сервера
//     * @throws RuntimeException если не удалось выполнить запрос
//     */
//    public Optional<String> getServerVersion() {
//        String methodName = "getServerVersion";
//        log.info(String.format(Constants.LOG_METHOD_START, methodName));
//
//        try (Session session = sessionFactory.openSession()) {
//            log.info("getServerVersion [1] Выполнение запроса для получения версии сервера");
//            NativeQuery<String> query = session.createNativeQuery("SELECT version()", String.class);
//            String result = query.getSingleResult();
//            log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Версия сервера: " + result));
//            log.info(String.format(Constants.LOG_METHOD_END, methodName));
//            return Optional.ofNullable(result);
//        } catch (Exception e) {
//            log.error(String.format(Constants.LOG_ERROR, methodName, "Не удалось получить версию сервера: " + e.getMessage()), e);
//            throw new RuntimeException("Не удалось получить версию сервера", e);
//        }
//    }
//}