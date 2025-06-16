package ru.sfedu.agileflow.lab4.mapcollection;

import jakarta.persistence.*;
import ru.sfedu.agileflow.models.TaskStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Сущность задачи с коллекцией комментариев типа Map (автор -> комментарий).
 */
@Entity(name = "MapCollectionTask")
@Table(name = "tasks_map")
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
    @CollectionTable(name = "task_comments_map", joinColumns = @JoinColumn(name = "task_id"))
    @MapKeyColumn(name = "author")
    @Column(name = "comment")
    private Map<String, String> comments = new HashMap<>();

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

    public Map<String, String> getComments() {
        return comments;
    }

    public void setComments(Map<String, String> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", comments=" + comments +
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