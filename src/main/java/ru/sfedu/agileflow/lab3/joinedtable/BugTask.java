package ru.sfedu.agileflow.lab3.joinedtable;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import ru.sfedu.agileflow.models.TaskStatus;

/**
 * Класс для задач типа "Ошибка" (Bug).
 */
@Entity
@Table(name = "bug_tasks_joined_table")
@PrimaryKeyJoinColumn(name = "task_id")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BugTask extends Task {
    @XmlElement
    private String severity;

    public BugTask() {
    }

    /**
     * Конструктор для создания задачи типа Bug.
     * @param title Заголовок задачи
     * @param description Описание задачи
     * @param status Статус задачи
     * @param priority Приоритет задачи
     * @param sprintId Идентификатор спринта
     * @param assignedUserId Идентификатор назначенного пользователя
     * @param severity Уровень серьезности ошибки
     */
    public BugTask(String title, String description, TaskStatus status, int priority, Integer sprintId, Integer assignedUserId, String severity) {
        super(title, description, status, priority, sprintId, assignedUserId);
        this.severity = severity;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    @Override
    public String toString() {
        return "BugTask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", priority=" + getPriority() +
                ", sprintId=" + getSprintId() +
                ", assignedUserId=" + getAssignedUserId() +
                ", severity='" + severity + '\'' +
                '}';
    }
}