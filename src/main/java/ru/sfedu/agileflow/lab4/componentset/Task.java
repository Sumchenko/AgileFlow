package ru.sfedu.agileflow.lab4.componentset;

import jakarta.persistence.*;
import ru.sfedu.agileflow.models.TaskStatus;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Сущность задачи с коллекцией вложений типа Set<TaskAttachment>.
 */
@Entity(name = "ComponentSetTask")
@Table(name = "tasks_component_set")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(nullable = false)
    private int priority;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_attachments_set", joinColumns = @JoinColumn(name = "task_id"))
    private Set<TaskAttachment> attachments = new HashSet<>();

    public Task() {
    }

    public Task(String title, String description, TaskStatus status, int priority) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Set<TaskAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<TaskAttachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", attachments=" + attachments +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}