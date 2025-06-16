//package ru.sfedu.agileflow.lab2;
//
//import org.apache.log4j.Logger;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.Test;
//import ru.sfedu.agileflow.lab2.Details;
//import ru.sfedu.agileflow.lab2.TestEntity;
//import ru.sfedu.agileflow.lab2.TestEntityService;
//import ru.sfedu.agileflow.lab2.HibernateUtil;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.Assert.*;
//
//public class TestEntityServiceTest {
//    private static final Logger log = Logger.getLogger(TestEntityServiceTest.class);
//    private TestEntityService service;
//    private TestEntity testEntity;
//
//    @Before
//    public void setUp() {
//        log.info("[1] setUp: Инициализация тестового окружения");
//        service = new TestEntityService();
//        testEntity = new TestEntity(
//                "Test_" + UUID.randomUUID(),
//                "Test Description",
//                new Date(),
//                true,
//                new Details("TestCategory", 1)
//        );
//    }
//
//    @AfterClass
//    public static void tearDown() {
//        log.info("[1] tearDown: Очистка ресурсов");
//        HibernateUtil.close();
//    }
//
//    /**
//     * Тестирует создание сущности TestEntity.
//     * Тип: Позитивный
//     */
//    @Test
//    public void testCreatePositive() {
//        log.info("[1] testCreatePositive: Начало теста");
//        TestEntity saved = service.create(testEntity);
//        log.debug("[2] testCreatePositive: Сохранена сущность: " + saved);
//        assertNotNull(saved.getId());
//        log.info("[3] testCreatePositive: Тест успешно завершен");
//    }
//
//    /**
//     * Тестирует создание сущности с некорректными данными.
//     * Тип: Негативный
//     */
//    @Test(expected = IllegalArgumentException.class)
//    public void testCreateNegative() {
//        log.info("[1] testCreateNegative: Начало теста");
//        TestEntity invalidEntity = null;
//        service.create(invalidEntity);
//        log.error("[2] testCreateNegative: Ожидалось исключение");
//    }
//
//    /**
//     * Тестирует чтение сущности по ID.
//     * Тип: Позитивный
//     */
//    @Test
//    public void testReadByIdPositive() {
//        log.info("[1] testReadByIdPositive: Начало теста");
//        TestEntity saved = service.create(testEntity);
//        Optional<TestEntity> found = service.readById(saved.getId());
//        log.debug("[2] testReadByIdPositive: Найдена сущность: " + found);
//        assertTrue(found.isPresent());
//        assertEquals(saved.getId(), found.get().getId());
//        log.info("[3] testReadByIdPositive: Тест успешно завершен");
//    }
//
//    /**
//     * Тестирует чтение сущности с несуществующим ID.
//     * Тип: Негативный
//     */
//    @Test
//    public void testReadByIdNegative() {
//        log.info("[1] testReadByIdNegative: Начало теста");
//        Optional<TestEntity> found = service.readById(-1L);
//        log.debug("[2] testReadByIdNegative: Результат поиска: " + found);
//        assertFalse(found.isPresent());
//        log.info("[3] testReadByIdNegative: Тест успешно завершен");
//    }
//
//    /**
//     * Тестирует обновление сущности.
//     * Тип: Позитивный
//     */
//    @Test
//    public void testUpdatePositive() {
//        log.info("[1] testUpdatePositive: Начало теста");
//        TestEntity saved = service.create(testEntity);
//        saved.setName("Updated Name");
//        TestEntity updated = service.update(saved);
//        log.debug("[2] testUpdatePositive: Обновлена сущность: " + updated);
//        assertEquals("Updated Name", updated.getName());
//        log.info("[3] testUpdatePositive: Тест успешно завершен");
//    }
//
//    /**
//     * Тестирует обновление сущности без ID.
//     * Тип: Негативный
//     */
//    @Test(expected = IllegalArgumentException.class)
//    public void testUpdateNegative() {
//        log.info("[1] testUpdateNegative: Начало теста");
//        TestEntity invalidEntity = new TestEntity();
//        service.update(invalidEntity);
//        log.error("[2] testUpdateNegative: Ожидалось исключение");
//    }
//
//    /**
//     * Тестирует удаление сущности.
//     * Тип: Позитивный
//     */
//    @Test
//    public void testDeletePositive() {
//        log.info("[1] testDeletePositive: Начало теста");
//        TestEntity saved = service.create(testEntity);
//        boolean deleted = service.delete(saved.getId());
//        log.debug("[2] testDeletePositive: Результат удаления: " + deleted);
//        assertTrue(deleted);
//        Optional<TestEntity> found = service.readById(saved.getId());
//        assertFalse(found.isPresent());
//        log.info("[3] testDeletePositive: Тест успешно завершен");
//    }
//
//    /**
//     * Тестирует удаление сущности с несуществующим ID.
//     * Тип: Негативный
//     */
//    @Test
//    public void testDeleteNegative() {
//        log.info("[1] testDeleteNegative: Начало теста");
//        boolean deleted = service.delete(-1L);
//        log.debug("[2] testDeleteNegative: Результат удаления: " + deleted);
//        assertFalse(deleted);
//        log.info("[3] testDeleteNegative: Тест успешно завершен");
//    }
//
//    /**
//     * Тестирует получение всех сущностей.
//     * Тип: Позитивный
//     */
//    @Test
//    public void testReadAllPositive() {
//        log.info("[1] testReadAllPositive: Начало теста");
//        service.create(testEntity);
//        List<TestEntity> entities = service.readAll();
//        log.debug("[2] testReadAllPositive: Получено сущностей: " + entities.size());
//        assertFalse(entities.isEmpty());
//        log.info("[3] testReadAllPositive: Тест успешно завершен");
//    }
//
//    /**
//     * Тестирует поиск сущностей по категории.
//     * Тип: Позитивный
//     */
//    @Test
//    public void testFindByCategoryPositive() {
//        log.info("[1] testFindByCategoryPositive: Начало теста");
//        service.create(testEntity);
//        List<TestEntity> entities = service.findByCategory("TestCategory");
//        log.debug("[2] testFindByCategoryPositive: Найдено сущностей: " + entities.size());
//        assertFalse(entities.isEmpty());
//        assertEquals("TestCategory", entities.get(0).getDetails().getCategory());
//        log.info("[3] testFindByCategoryPositive: Тест успешно завершен");
//    }
//
//    /**
//     * Тестирует поиск сущностей по несуществующей категории.
//     * Тип: Негативный
//     */
//    @Test
//    public void testFindByCategoryNegative() {
//        log.info("[1] testFindByCategoryNegative: Начало теста");
//        List<TestEntity> entities = service.findByCategory("NonExistentCategory");
//        log.debug("[2] testFindByCategoryNegative: Найдено сущностей: " + entities.size());
//        assertTrue(entities.isEmpty());
//        log.info("[3] testFindByCategoryNegative: Тест успешно завершен");
//    }
//}