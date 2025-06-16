package ru.sfedu.agileflow.lab3.mappedsuperclass;

import ru.sfedu.agileflow.models.TaskStatus;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для CRUD-операций с задачами в стратегии MappedSuperclass.
 */
public interface TaskMappedSuperclassDAO {
    /**
     * Создает новую задачу в базе данных.
     * @param task Задача для сохранения (BugTask или FeatureTask)
     */
    void create(Task task);

    /**
     * Находит задачу по идентификатору.
     * @param id Идентификатор задачи
     * @param taskClass Класс задачи (BugTask или FeatureTask)
     * @return Optional с задачей, если найдена, иначе пустой Optional
     */
    <T extends Task> Optional<T> findById(Integer id, Class<T> taskClass);

    /**
     * Возвращает список всех задач.
     * @return Список задач (BugTask и FeatureTask)
     */
    List<Task> findAll();

    /**
     * Обновляет данные задачи.
     * @param task Обновленная задача
     */
    void update(Task task);

    /**
     * Удаляет задачу по идентификатору.
     * @param id Идентификатор задачи
     * @param taskClass Класс задачи (BugTask или FeatureTask)
     */
    <T extends Task> void delete(Integer id, Class<T> taskClass);

    /**
     * Находит задачи по статусу.
     * @param status Статус задачи
     * @return Список задач с указанным статусом
     */
    List<Task> findByStatus(TaskStatus status);
}