//package ru.sfedu.agileflow.lab2;
//
//import jakarta.persistence.*;
//import java.io.Serializable;
//import java.util.Date;
//import java.util.Objects;
//
///**
// * Сущность для тестирования маппинга Hibernate.
// */
//@Entity
//@Table(name = "test_entities")
//public class TestEntity implements Serializable {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "name", nullable = false)
//    private String name;
//
//    @Column(name = "description")
//    private String description;
//
//    @Column(name = "date_created")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date dateCreated;
//
//    @Column(name = "check_flag")
//    private Boolean check;
//
//    @Embedded
//    @AttributeOverrides({
//            @AttributeOverride(name = "category", column = @Column(name = "details_category")),
//            @AttributeOverride(name = "priority", column = @Column(name = "details_priority"))
//    })
//    private Details details;
//
//    // Конструктор по умолчанию
//    public TestEntity() {
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
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        TestEntity that = (TestEntity) o;
//        return Objects.equals(id, that.id);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id);
//    }
//
//    @Override
//    public String toString() {
//        return "TestEntity{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", description='" + description + '\'' +
//                ", dateCreated=" + dateCreated +
//                ", check=" + check +
//                ", details=" + details +
//                '}';
//    }
//}