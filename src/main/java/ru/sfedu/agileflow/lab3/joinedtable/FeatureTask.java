package ru.sfedu.agileflow.lab3.joinedtable;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import ru.sfedu.agileflow.models.TaskStatus;

/**
 * Класс для задач типа "Новая функциональность" (Feature).
 */
@Entity
@Table(name = "feature_tasks_joined_table")
@PrimaryKeyJoinColumn(name = "task_id")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FeatureTask extends Task {
    @XmlElement
    private String acceptanceCriteria;

    public FeatureTask() {
    }

    /**
     * Конструктор для создания задачи типа Feature.
     * @param title Заголовок задачи
     * @param description Описание задачи
     * @param status Статус задачи
     * @param priority Приоритет задачи
     * @param sprintId Идентификатор спринта
     * @param assignedUserId Идентификатор назначенного пользователя
     * @param acceptanceCriteria Критерии приемки
     */
    public FeatureTask(String title, String description, TaskStatus status, int priority, Integer sprintId, Integer assignedUserId, String acceptanceCriteria) {
        super(title, description, status, priority, sprintId, assignedUserId);
        this.acceptanceCriteria = acceptanceCriteria;
    }

    public String getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public void setAcceptanceCriteria(String acceptanceCriteria) {
        this.acceptanceCriteria = acceptanceCriteria;
    }

    @Override
    public String toString() {
        return "FeatureTask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", priority=" + getPriority() +
                ", sprintId=" + getSprintId() +
                ", assignedUserId=" + getAssignedUserId() +
                ", acceptanceCriteria='" + acceptanceCriteria + '\'' +
                '}';
    }
}