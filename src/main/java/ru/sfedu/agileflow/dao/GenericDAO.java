package ru.sfedu.agileflow.dao;

import java.util.List;
import java.util.Optional;

/**
 * Общий интерфейс для DAO-классов, предоставляющий CRUD-операции.
 * @param <T> Тип модели
 * @param <ID> Тип идентификатора
 */
public interface GenericDAO<T, ID> {

    /**
     * Создает новую запись в базе данных.
     * @param entity Объект для сохранения
     */
    void create(T entity);

    /**
     * Находит запись по идентификатору.
     * @param id Идентификатор записи
     * @return Optional с объектом типа T, если найден, иначе пустой Optional
     */
    Optional<T> findById(ID id);

    /**
     * Возвращает список всех записей.
     * @return Список объектов типа T
     */
    List<T> findAll();

    /**
     * Обновляет существующую запись.
     * @param entity Обновленный объект
     */
    void update(T entity);

    /**
     * Удаляет запись по идентификатору.
     * @param id Идентификатор записи
     */
    void delete(ID id);
}