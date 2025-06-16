//package ru.sfedu.agileflow.lab2;
//
//import org.apache.log4j.Logger;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import ru.sfedu.agileflow.lab2.TestEntity;
//import ru.sfedu.agileflow.lab2.HibernateUtil;
//
//import java.util.List;
//import java.util.Optional;
//
///**
// * Сервис для работы с сущностью TestEntity.
// */
//public class TestEntityService {
//    private static final Logger log = Logger.getLogger(TestEntityService.class);
//    private final SessionFactory sessionFactory;
//
//    public TestEntityService() {
//        this.sessionFactory = HibernateUtil.getSessionFactory();
//    }
//
//    /**
//     * Создаёт новую сущность TestEntity в базе данных.
//     *
//     * @param entity Сущность для сохранения
//     * @return Сохранённая сущность
//     * @throws IllegalArgumentException если сущность null или уже имеет ID
//     */
//    public TestEntity create(TestEntity entity) {
//        String methodName = "create";
//        log.info(String.format("[1] %s: Начало выполнения метода", methodName));
//        if (entity == null || entity.getId() != null) {
//            log.error("[1] create: Сущность null или уже имеет ID");
//            throw new IllegalArgumentException("Сущность не должна быть null и не должна иметь ID");
//        }
//        try (Session session = sessionFactory.openSession()) {
//            log.info("[2] create: Начало транзакции");
//            session.beginTransaction();
//            session.persist(entity);
//            session.getTransaction().commit();
//            log.debug(String.format("[2] create: Сохранена сущность: %s", entity));
//            log.info(String.format("[3] %s: Метод успешно завершен", methodName));
//            return entity;
//        } catch (Exception e) {
//            log.error(String.format("[3] %s: Ошибка при сохранении сущности: %s", methodName, e.getMessage()), e);
//            throw new RuntimeException("Не удалось сохранить сущность", e);
//        }
//    }
//
//    /**
//     * Получает сущность TestEntity по ID.
//     *
//     * @param id Идентификатор сущности
//     * @return Optional с сущностью или пустой, если не найдено
//     */
//    public Optional<TestEntity> readById(Long id) {
//        String methodName = "readById";
//        log.info(String.format("[1] %s: Начало выполнения метода", methodName));
//        if (id == null || id <= 0) {
//            log.error("[1] readById: Неверный ID");
//            return Optional.empty();
//        }
//        try (Session session = sessionFactory.openSession()) {
//            log.info("[2] readById: Поиск сущности по ID: " + id);
//            TestEntity entity = session.get(TestEntity.class, id);
//            log.debug(String.format("[2] readById: Найдена сущность: %s", entity));
//            log.info(String.format("[3] %s: Метод успешно завершен", methodName));
//            return Optional.ofNullable(entity);
//        } catch (Exception e) {
//            log.error(String.format("[3] %s: Ошибка при чтении сущности: %s", methodName, e.getMessage()), e);
//            return Optional.empty();
//        }
//    }
//
//    /**
//     * Обновляет существующую сущность TestEntity.
//     *
//     * @param entity Сущность для обновления
//     * @return Обновлённая сущность
//     * @throws IllegalArgumentException если сущность null или не имеет ID
//     */
//    public TestEntity update(TestEntity entity) {
//        String methodName = "update";
//        log.info(String.format("[1] %s: Начало выполнения метода", methodName));
//        if (entity == null || entity.getId() == null) {
//            log.error("[1] update: Сущность null или не имеет ID");
//            throw new IllegalArgumentException("Сущность не должна быть null и должна иметь ID");
//        }
//        try (Session session = sessionFactory.openSession()) {
//            log.info("[2] update: Начало транзакции");
//            session.beginTransaction();
//            TestEntity updated = (TestEntity) session.merge(entity);
//            session.getTransaction().commit();
//            log.debug(String.format("[2] update: Обновлена сущность: %s", updated));
//            log.info(String.format("[3] %s: Метод успешно завершен", methodName));
//            return updated;
//        } catch (Exception e) {
//            log.error(String.format("[3] %s: Ошибка при обновлении сущности: %s", methodName, e.getMessage()), e);
//            throw new RuntimeException("Не удалось обновить сущность", e);
//        }
//    }
//
//    /**
//     * Удаляет сущность TestEntity по ID.
//     *
//     * @param id Идентификатор сущности
//     * @return true, если удаление успешно, false, если сущность не найдена
//     */
//    public boolean delete(Long id) {
//        String methodName = "delete";
//        log.info(String.format("[1] %s: Начало выполнения метода", methodName));
//        if (id == null || id <= 0) {
//            log.error("[1] delete: Неверный ID");
//            return false;
//        }
//        try (Session session = sessionFactory.openSession()) {
//            log.info("[2] delete: Начало транзакции");
//            session.beginTransaction();
//            TestEntity entity = session.get(TestEntity.class, id);
//            if (entity != null) {
//                session.delete(entity);
//                session.getTransaction().commit();
//                log.debug(String.format("[2] delete: Удалена сущность с ID: %s", id));
//                log.info(String.format("[3] %s: Метод успешно завершен", methodName));
//                return true;
//            }
//            log.info("[2] delete: Сущность с ID " + id + " не найдена");
//            return false;
//        } catch (Exception e) {
//            log.error(String.format("[3] %s: Ошибка при удалении сущности: %s", methodName, e.getMessage()), e);
//            return false;
//        }
//    }
//
//    /**
//     * Получает список всех сущностей TestEntity.
//     *
//     * @return Список сущностей
//     */
//    public List<TestEntity> readAll() {
//        String methodName = "readAll";
//        log.info(String.format("[1] %s: Начало выполнения метода", methodName));
//        try (Session session = sessionFactory.openSession()) {
//            log.info("[2] readAll: Выполнение запроса на получение всех сущностей");
//            List<TestEntity> entities = session.createQuery("FROM TestEntity", TestEntity.class).getResultList();
//            log.debug(String.format("[2] readAll: Получено %d сущностей", entities.size()));
//            log.info(String.format("[3] %s: Метод успешно завершен", methodName));
//            return entities;
//        } catch (Exception e) {
//            log.error(String.format("[3] %s: Ошибка при чтении всех сущностей: %s", methodName, e.getMessage()), e);
//            return List.of();
//        }
//    }
//
//    /**
//     * Ищет сущности TestEntity по категории из встроенного компонента Details.
//     *
//     * @param category Категория для поиска
//     * @return Список сущностей с указанной категорией
//     */
//    public List<TestEntity> findByCategory(String category) {
//        String methodName = "findByCategory";
//        log.info(String.format("[1] %s: Начало выполнения метода", methodName));
//        if (category == null || category.isEmpty()) {
//            log.error("[1] findByCategory: Категория не указана");
//            return List.of();
//        }
//        try (Session session = sessionFactory.openSession()) {
//            log.info("[2] findByCategory: Выполнение запроса для категории: " + category);
//            List<TestEntity> entities = session.createQuery("FROM TestEntity WHERE details.category = :category", TestEntity.class)
//                    .setParameter("category", category)
//                    .getResultList();
//            log.debug(String.format("[2] findByCategory: Найдено %d сущностей с категорией: %s", entities.size(), category));
//            log.info(String.format("[3] %s: Метод успешно завершен", methodName));
//            return entities;
//        } catch (Exception e) {
//            log.error(String.format("[3] %s: Ошибка при поиске по категории: %s", methodName, e.getMessage()), e);
//            return List.of();
//        }
//    }
//}