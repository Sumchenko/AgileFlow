package ru.sfedu.agileflow.lab5;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "lab5_projects")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlAttribute
    private int id;

    @XmlElement
    private String name;

    @XmlElement
    private String description;

    @ManyToMany
    @JoinTable(
            name = "lab5_project_users",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @XmlElementWrapper(name = "users")
    @XmlElement(name = "user")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @XmlElementWrapper(name = "sprints")
    @XmlElement(name = "sprint")
    private List<Sprint> sprints;

    public Project() {
        this.users = new ArrayList<>();
    }

    public Project(String name, String description) {
        this.name = name;
        this.description = description;
        this.users = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users != null ? users : new ArrayList<>();
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void setSprints(List<Sprint> sprints) {
        this.sprints = sprints;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id == project.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}