//package ru.sfedu.agileflow.lab2;
//
//import org.apache.log4j.Logger;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import ru.sfedu.agileflow.lab2.HibernateUtil;
//
//import java.util.List;
//import java.util.Optional;
//
///**
// * Репозиторий для работы с сущностью TestEntity.
// */
//public class TestEntityRepository {
//
//    private static final Logger log = Logger.getLogger(TestEntityRepository.class);
//    private final SessionFactory sessionFactory;
//
//    public TestEntityRepository() {
//        // Устанавливаем тестовую конфигурацию
//        System.setProperty("hibernate.config.path", "lab2/hibernate-test.cfg.xml");
//        this.sessionFactory = HibernateUtil.getSessionFactory();
//    }
//
//    /**
//     * Создает новую сущность TestEntity в базе данных.
//     *
//     * @param entity Сущность для сохранения
//     * @return Optional с сохраненной сущностью
//     */
//    public Optional<TestEntity> create(TestEntity entity) {
//        String methodName = "create";
//        log.info(String.format("create [1] Начало создания сущности: %s", entity));
//        try (Session session = sessionFactory.openSession()) {
//            session.beginTransaction();
//            log.debug(String.format("create [2] Сохранение сущности: %s", entity));
//            session.persist(entity);
//            session.getTransaction().commit();
//            log.info("create [3] Сущность успешно создана");
//            return Optional.of(entity);
//        } catch (Exception e) {
//            log.error(String.format("create [4] Ошибка создания сущности: %s", e.getMessage()), e);
//            return Optional.empty();
//        }
//    }
//
//    /**
//     * Получает сущность TestEntity по идентификатору.
//     *
//     * @param id Идентификатор сущности
//     * @return Optional с найденной сущностью или пустой Optional
//     */
//    public Optional<TestEntity> readById(Long id) {
//        String methodName = "readById";
//        log.info(String.format("readById [1] Начало получения сущности с id: %s", id));
//        try (Session session = sessionFactory.openSession()) {
//            log.debug(String.format("readById [2] Запрос сущности с id: %s", id));
//            TestEntity entity = session.get(TestEntity.class, id);
//            log.info("readById [3] Получение сущности завершено");
//            return Optional.ofNullable(entity);
//        } catch (Exception e) {
//            log.error(String.format("readById [4] Ошибка получения сущности: %s", e.getMessage()), e);
//            return Optional.empty();
//        }
//    }
//
//    /**
//     * Обновляет сущность TestEntity в базе данных.
//     *
//     * @param entity Сущность для обновления
//     * @return Optional с обновленной сущностью или пустой Optional, если сущность не найдена
//     */
//    public Optional<TestEntity> update(TestEntity entity) {
//        String methodName = "update";
//        log.info(String.format("update [1] Начало обновления сущности: %s", entity));
//        try (Session session = sessionFactory.openSession()) {
//            session.beginTransaction();
//            log.debug(String.format("update [2] Проверка существования сущности с id: %s", entity.getId()));
//            // Проверяем, существует ли сущность
//            TestEntity existingEntity = session.get(TestEntity.class, entity.getId());
//            if (existingEntity == null) {
//                log.warn("update [3] Сущность с id: " + entity.getId() + " не найдена");
//                session.getTransaction().rollback();
//                return Optional.empty();
//            }
//            log.debug(String.format("update [4] Обновление сущности: %s", entity));
//            TestEntity updatedEntity = (TestEntity) session.merge(entity);
//            session.getTransaction().commit();
//            log.info("update [5] Сущность успешно обновлена");
//            return Optional.of(updatedEntity);
//        } catch (Exception e) {
//            log.error(String.format("update [6] Ошибка обновления сущности: %s", e.getMessage()), e);
//            return Optional.empty();
//        }
//    }
//
//    /**
//     * Удаляет сущность TestEntity по идентификатору.
//     *
//     * @param id Идентификатор сущности
//     * @return true, если удаление успешно, иначе false
//     */
//    public boolean delete(Long id) {
//        String methodName = "delete";
//        log.info(String.format("delete [1] Начало удаления сущности с id: %s", id));
//        try (Session session = sessionFactory.openSession()) {
//            session.beginTransaction();
//            log.debug(String.format("delete [2] Запрос сущности с id: %s", id));
//            TestEntity entity = session.get(TestEntity.class, id);
//            if (entity != null) {
//                session.remove(entity);
//                session.getTransaction().commit();
//                log.info("delete [3] Сущность успешно удалена");
//                return true;
//            }
//            log.warn("delete [3] Сущность не найдена");
//            return false;
//        } catch (Exception e) {
//            log.error(String.format("delete [4] Ошибка удаления сущности: %s", e.getMessage()), e);
//            return false;
//        }
//    }
//
//    /**
//     * Получает все сущности TestEntity из базы данных.
//     *
//     * @return Список сущностей
//     */
//    public List<TestEntity> findAll() {
//        String methodName = "findAll";
//        log.info("findAll [1] Начало получения всех сущностей");
//        try (Session session = sessionFactory.openSession()) {
//            log.debug("findAll [2] Выполнение запроса для получения всех сущностей");
//            List<TestEntity> entities = session.createQuery("from TestEntity", TestEntity.class).list();
//            log.info("findAll [3] Получение сущностей завершено");
//            return entities;
//        } catch (Exception e) {
//            log.error(String.format("findAll [4] Ошибка получения сущностей: %s", e.getMessage()), e);
//            return List.of();
//        }
//    }
//}