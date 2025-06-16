package ru.sfedu.agileflow.lab3.singletable;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import ru.sfedu.agileflow.models.TaskStatus;

/**
 * Класс для задач типа "Новая функциональность" (Feature).
 */
@Entity
@DiscriminatorValue("Feature")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FeatureTask extends Task {
    @XmlElement
    private String acceptanceCriteria;

    public FeatureTask() {
    }

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