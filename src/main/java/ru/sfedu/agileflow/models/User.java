package ru.sfedu.agileflow.models;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String email;
    private String bio;

    private boolean isActive;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateJoined;

    @ManyToMany(mappedBy = "users")
    private List<Project> projects;

    @OneToMany(mappedBy = "assignedUser")
    private List<Task> tasks;

    // Конструкторы
    public User() {
    }

    public User(String name, String email, String bio, boolean isActive, Date dateJoined) {
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.isActive = isActive;
        this.dateJoined = dateJoined;
    }

    // Новый конструктор с параметром lastLogin
    public User(String name, String email, String bio, boolean isActive, Date lastLogin, Date dateJoined) {
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.isActive = isActive;
        this.lastLogin = lastLogin;
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
}