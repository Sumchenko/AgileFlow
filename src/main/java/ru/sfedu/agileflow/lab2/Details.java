//package ru.sfedu.agileflow.lab2;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Embeddable;
//import java.io.Serializable;
//import java.util.Objects;
//
///**
// * Встроенный компонент для TestEntity.
// */
//@Embeddable
//public class Details implements Serializable {
//
//    @Column(name = "category")
//    private String category;
//
//    @Column(name = "priority")
//    private Integer priority;
//
//    // Конструктор по умолчанию
//    public Details() {
//    }
//
//    public Details(String category, Integer priority) {
//        this.category = category;
//        this.priority = priority;
//    }
//
//    // Геттеры и сеттеры
//    public String getCategory() {
//        return category;
//    }
//
//    public void setCategory(String category) {
//        this.category = category;
//    }
//
//    public Integer getPriority() {
//        return priority;
//    }
//
//    public void setPriority(Integer priority) {
//        this.priority = priority;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Details details = (Details) o;
//        return Objects.equals(category, details.category) &&
//                Objects.equals(priority, details.priority);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(category, priority);
//    }
//
//    @Override
//    public String toString() {
//        return "Details{" +
//                "category='" + category + '\'' +
//                ", priority=" + priority +
//                '}';
//    }
//}