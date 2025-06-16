//package ru.sfedu.agileflow.lab2;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Embeddable;
//
//import java.io.Serializable;
//
///**
// * Встроенный компонент, содержащий категорию и приоритет сущности.
// */
//@Embeddable
//public class Details implements Serializable {
//    @Column(name = "category", nullable = false)
//    private String category;
//
//    @Column(name = "priority", nullable = false)
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
//    public String toString() {
//        return "Details{category='" + category + "', priority=" + priority + "}";
//    }
//}