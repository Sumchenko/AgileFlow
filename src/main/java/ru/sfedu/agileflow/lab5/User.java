package ru.sfedu.agileflow.lab5;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "lab5_users")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlAttribute
    private int id;

    @XmlElement
    private String name;

    @XmlElement
    private String email;

    @XmlElement
    private String bio;

    @Column(name = "is_active")
    @XmlElement(name = "isActive")
    private boolean isActive;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login")
    @XmlElement
    private Date lastLogin;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_joined")
    @XmlElement
    private Date dateJoined;

    @ManyToMany(mappedBy = "users")
    @XmlElementWrapper(name = "projects")
    @XmlElement(name = "project")
    private List<Project> projects;

    @OneToMany(mappedBy = "assignedUser", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @XmlElementWrapper(name = "tasks")
    @XmlElement(name = "task")
    private List<Task> tasks;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @XmlElement
    private UserProfile profile;

    public User() {
    }

    public User(String name, String email, String bio, boolean isActive, Date dateJoined) {
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.isActive = isActive;
        this.dateJoined = dateJoined;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Date dateJoined) {
        this.dateJoined = dateJoined;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", bio='" + bio + '\'' +
                ", isActive=" + isActive +
                ", lastLogin=" + lastLogin +
                ", dateJoined=" + dateJoined +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}