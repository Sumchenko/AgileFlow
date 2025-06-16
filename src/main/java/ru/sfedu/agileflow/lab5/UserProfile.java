package ru.sfedu.agileflow.lab5;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import java.util.Objects;

@Entity
@Table(name = "lab5_user_profiles")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserProfile {
    @Id
    @XmlAttribute
    private int id;

    @OneToOne
    @PrimaryKeyJoinColumn
    @XmlElement
    private User user;

    @XmlElement
    private String phoneNumber;

    @XmlElement
    private String address;

    public UserProfile() {
    }

    public UserProfile(User user, String phoneNumber, String address) {
        this.user = user;
        this.id = user.getId();
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}