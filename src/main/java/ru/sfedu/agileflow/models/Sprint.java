package ru.sfedu.agileflow.models;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sprints")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlAttribute
    private int id;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    @XmlElement
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    @XmlElement
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @XmlElement
    private Project project;

    @OneToMany(mappedBy = "sprint")
    @XmlElementWrapper(name = "tasks")
    @XmlElement(name = "task")
    private List<Task> tasks;

    @OneToOne(mappedBy = "sprint")
    @XmlElement
    private Retrospective retrospective;

    public Sprint() {
    }

    public Sprint(Date startDate, Date endDate, Project project) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.project = project;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Retrospective getRetrospective() {
        return retrospective;
    }

    public void setRetrospective(Retrospective retrospective) {
        this.retrospective = retrospective;
    }

    @Override
    public String toString() {
        return "Sprint{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", projectId=" + (project != null ? project.getId() : null) +
                '}';
    }
}