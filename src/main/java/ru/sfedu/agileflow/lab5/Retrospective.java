package ru.sfedu.agileflow.lab5;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import java.util.List;

@Entity
@Table(name = "lab5_retrospectives")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Retrospective {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlAttribute
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    @XmlElement
    private Sprint sprint;

    @XmlElement
    private String summary;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "lab5_retrospective_improvements", joinColumns = @JoinColumn(name = "retrospective_id"))
    @Column(name = "improvement")
    @XmlElementWrapper(name = "improvements")
    @XmlElement(name = "improvement")
    private List<String> improvements;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "lab5_retrospective_positives", joinColumns = @JoinColumn(name = "retrospective_id"))
    @Column(name = "positive")
    @XmlElementWrapper(name = "positives")
    @XmlElement(name = "positive")
    private List<String> positives;

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

    @Override
    public String toString() {
        return "Retrospective{" +
                "id=" + id +
                ", sprintId=" + (sprint != null ? sprint.getId() : null) +
                ", summary='" + summary + '\'' +
                ", improvements=" + improvements +
                ", positives=" + positives +
                '}';
    }
}