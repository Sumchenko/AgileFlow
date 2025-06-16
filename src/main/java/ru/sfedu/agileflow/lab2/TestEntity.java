//package ru.sfedu.agileflow.lab2;
//
//import jakarta.persistence.*;
//import java.io.Serializable;
//import java.util.Date;
//
///**
// * Тестовая сущность для лабораторной работы.
// */
//@Entity
//@Table(name = "test_entity")
//public class TestEntity implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Long id;
//
//    @Column(name = "name", nullable = false)
//    private String name;
//
//    @Column(name = "description")
//    private String description;
//
//    @Column(name = "date_created", nullable = false)
//    @Temporal(TemporalType.DATE)
//    private Date dateCreated;
//
//    @Column(name = "check_flag", nullable = false)
//    private Boolean check;
//
//    @Embedded
//    @AttributeOverrides({
//            @AttributeOverride(name = "category", column = @Column(name = "entity_category", nullable = false)),
//            @AttributeOverride(name = "priority", column = @Column(name = "entity_priority", nullable = false))
//    })
//    private Details details;
//
//    // Конструктор по умолчанию
//    public TestEntity() {
//    }
//
//    public TestEntity(String name, String description, Date dateCreated, Boolean check, Details details) {
//        this.name = name;
//        this.description = description;
//        this.dateCreated = dateCreated;
//        this.check = check;
//        this.details = details;
//    }
//
//    // Геттеры и сеттеры
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public Date getDateCreated() {
//        return dateCreated;
//    }
//
//    public void setDateCreated(Date dateCreated) {
//        this.dateCreated = dateCreated;
//    }
//
//    public Boolean getCheck() {
//        return check;
//    }
//
//    public void setCheck(Boolean check) {
//        this.check = check;
//    }
//
//    public Details getDetails() {
//        return details;
//    }
//
//    public void setDetails(Details details) {
//        this.details = details;
//    }
//
//    @Override
//    public String toString() {
//        return "TestEntity{id=" + id +
//                ", name='" + name + '\'' +
//                ", description='" + description + '\'' +
//                ", dateCreated=" + dateCreated +
//                ", check=" + check +
//                ", details=" + details +
//                '}';
//    }
//}