package ru.sfedu.agileflow.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "retrospectives")
public class Retrospective {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    private String summary;

    @ElementCollection
    private List<String> improvements;

    @ElementCollection
    private List<String> positives;

    // Конструкторы
    public Retrospective() {}

    public Retrospective(Sprint sprint, String summary, List<String> improvements, List<String> positives) {
        this.sprint = sprint;
        this.summary = summary;
        this.improvements = improvements;
        this.positives = positives;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getImprovements() {
        return improvements;
    }

    public void setImprovements(List<String> improvements) {
        this.improvements = improvements;
    }

    public List<String> getPositives() {
        return positives;
    }

    public void setPositives(List<String> positives) {
        this.positives = positives;
    }
}
