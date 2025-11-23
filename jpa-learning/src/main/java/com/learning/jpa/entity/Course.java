package com.learning.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "courses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String courseCode;

    @Column(nullable = false, length = 200)
    private String courseName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer credits;

    @Column(length = 100)
    private String instructor;

    @Column(length = 100)
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CourseLevel level;

    @Column(nullable = false)
    private Integer maxEnrollment;

    @Column(nullable = false)
    private Integer currentEnrollment = 0;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum CourseLevel {
        BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    }
}
