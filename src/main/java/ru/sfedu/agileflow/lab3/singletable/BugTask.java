package ru.sfedu.agileflow.lab3.singletable;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import ru.sfedu.agileflow.models.TaskStatus;

/**
 * Класс для задач типа "Ошибка" (Bug).
 */
@Entity
@DiscriminatorValue("Bug")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BugTask extends Task {
    @XmlElement
    private String severity;

    public BugTask() {
    }

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