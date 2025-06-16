package ru.sfedu.agileflow.lab3.singletable;

import ru.sfedu.agileflow.models.TaskStatus;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для CRUD-операций с задачами в стратегии Single Table.
 */
public interface TaskSingleTableDAO {
    /**
     * Создает новую задачу в базе данных.
     * @param task Задача для сохранения
     */
    void create(Task task);

    /**
     * Находит задачу по идентификатору.
     * @param id Идентификатор задачи
     * @return Optional с задачей, если найдена, иначе пустой Optional
     */
    Optional<Task> findById(Integer id);

    /**
     * Возвращает список всех задач.
     * @return Список задач
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
     */
    void delete(Integer id);

    /**
     * Находит задачи по статусу.
     * @param status Статус задачи
     * @return Список задач с указанным статусом
     */
    List<Task> findByStatus(TaskStatus status);
}