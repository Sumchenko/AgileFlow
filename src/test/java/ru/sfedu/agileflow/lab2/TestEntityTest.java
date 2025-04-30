//package ru.sfedu.agileflow.lab2;
//
//import org.apache.log4j.Logger;
//import org.hibernate.Session;
//import org.junit.Before;
//import org.junit.Test;
//import ru.sfedu.agileflow.lab2.HibernateUtil;
//
//import java.util.Date;
//import java.util.Optional;
//
//import static org.junit.Assert.*;
//
///**
// * Тестовый класс для TestEntityRepository.
// */
//public class TestEntityTest {
//
//    private static final Logger log = Logger.getLogger(TestEntityTest.class);
//    private TestEntityRepository repository;
//
//    @Before
//    public void setUp() {
//        // Устанавливаем тестовую конфигурацию
//        System.setProperty("hibernate.config.path", "lab2/hibernate-test.cfg.xml");
//        repository = new TestEntityRepository();
//        // Очистка данных перед каждым тестом
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            session.beginTransaction();
//            session.createQuery("delete from TestEntity").executeUpdate();
//            session.getTransaction().commit();
//        }
//    }
//
//    /**
//     * Тестирование создания сущности.
//     * Тип: Позитивный
//     */
//    @Test
//    public void testCreatePositive() {
//        log.info("testCreatePositive [1] Начало позитивного теста создания");
//        TestEntity entity = createTestEntity("ТестовоеИмя", "ТестовоеОписание");
//        Optional<TestEntity> result = repository.create(entity);
//        assertTrue(result.isPresent());
//        assertNotNull(result.get().getId());
//        log.info("testCreatePositive [2] Позитивный тест создания завершен");
//    }
//
//    /**
//     * Тестирование создания сущности с null именем.
//     * Тип: Негативный
//     */
//    @Test
//    public void testCreateNegative() {
//        log.info("testCreateNegative [1] Начало негативного теста создания");
//        TestEntity entity = createTestEntity(null, "ТестовоеОписание");
//        Optional<TestEntity> result = repository.create(entity);
//        assertFalse(result.isPresent());
//        log.info("testCreateNegative [2] Негативный тест создания завершен");
//    }
//
//    /**
//     * Тестирование получения сущности по ID.
//     * Тип: Позитивный
//     */
//    @Test
//    public void testReadByIdPositive() {
//        log.info("testReadByIdPositive [1] Начало позитивного теста получения по ID");
//        TestEntity entity = createTestEntity("ТестовоеИмя", "ТестовоеОписание");
//        repository.create(entity);
//        Optional<TestEntity> result = repository.readById(entity.getId());
//        assertTrue(result.isPresent());
//        assertEquals(entity.getName(), result.get().getName());
//        log.info("testReadByIdPositive [2] Позитивный тест получения по ID завершен");
//    }
//
//    /**
//     * Тестирование получения несуществующей сущности.
//     * Тип: Негативный
//     */
//    @Test
//    public void testReadByIdNegative() {
//        log.info("testReadByIdNegative [1] Начало негативного теста получения по ID");
//        Optional<TestEntity> result = repository.readById(-1L);
//        assertFalse(result.isPresent());
//        log.info("testReadByIdNegative [2] Негативный тест получения по ID завершен");
//    }
//
//    /**
//     * Тестирование обновления сущности.
//     * Тип: Позитивный
//     */
//    @Test
//    public void testUpdatePositive() {
//        log.info("testUpdatePositive [1] Начало позитивного теста обновления");
//        TestEntity entity = createTestEntity("ТестовоеИмя", "ТестовоеОписание");
//        repository.create(entity);
//        entity.setName("ОбновленноеИмя");
//        Optional<TestEntity> result = repository.update(entity);
//        assertTrue(result.isPresent());
//        assertEquals("ОбновленноеИмя", result.get().getName());
//        log.info("testUpdatePositive [2] Позитивный тест обновления завершен");
//    }
//
//    /**
//     * Тестирование обновления несуществующей сущности.
//     * Тип: Негативный
//     */
//    @Test
//    public void testUpdateNegative() {
//        log.info("testUpdateNegative [1] Начало негативного теста обновления");
//        TestEntity entity = createTestEntity("ТестовоеИмя", "ТестовоеОписание");
//        entity.setId(-1L);
//        Optional<TestEntity> result = repository.update(entity);
//        assertFalse(result.isPresent());
//        log.info("testUpdateNegative [2] Негативный тест обновления завершен");
//    }
//
//    /**
//     * Тестирование удаления сущности.
//     * Тип: Позитивный
//     */
//    @Test
//    public void testDeletePositive() {
//        log.info("testDeletePositive [1] Начало позитивного теста удаления");
//        TestEntity entity = createTestEntity("ТестовоеИмя", "ТестовоеОписание");
//        repository.create(entity);
//        boolean result = repository.delete(entity.getId());
//        assertTrue(result);
//        log.info("testDeletePositive [2] Позитивный тест удаления завершен");
//    }
//
//    /**
//     * Тестирование удаления несуществующей сущности.
//     * Тип: Негативный
//     */
//    @Test
//    public void testDeleteNegative() {
//        log.info("testDeleteNegative [1] Начало негативного теста удаления");
//        boolean result = repository.delete(-1L);
//        assertFalse(result);
//        log.info("testDeleteNegative [2] Негативный тест удаления завершен");
//    }
//
//    /**
//     * Вспомогательный метод для создания тестового объекта TestEntity.
//     */
//    private TestEntity createTestEntity(String name, String description) {
//        TestEntity entity = new TestEntity();
//        entity.setName(name);
//        entity.setDescription(description);
//        entity.setDateCreated(new Date());
//        entity.setCheck(true);
//        entity.setDetails(new Details("Категория1", 1));
//        return entity;
//    }
//}