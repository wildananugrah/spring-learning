package com.learning.jpa.service;

import com.learning.jpa.entity.Student;
import com.learning.jpa.repository.StudentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // ==================== CRITERIA API EXAMPLES ====================

    /**
     * Criteria API: Simple query
     * Find students by status using Criteria API
     */
    public List<Student> findByStatusCriteria(Student.StudentStatus status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> query = cb.createQuery(Student.class);
        Root<Student> student = query.from(Student.class);

        query.select(student)
             .where(cb.equal(student.get("status"), status));

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Criteria API: Multiple conditions (AND)
     * Find students by gender and status
     */
    public List<Student> findByGenderAndStatusCriteria(Student.Gender gender,
                                                       Student.StudentStatus status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> query = cb.createQuery(Student.class);
        Root<Student> student = query.from(Student.class);

        Predicate genderPredicate = cb.equal(student.get("gender"), gender);
        Predicate statusPredicate = cb.equal(student.get("status"), status);

        query.select(student)
             .where(cb.and(genderPredicate, statusPredicate));

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Criteria API: OR conditions
     * Find students by multiple statuses
     */
    public List<Student> findByMultipleStatusesCriteria(List<Student.StudentStatus> statuses) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> query = cb.createQuery(Student.class);
        Root<Student> student = query.from(Student.class);

        query.select(student)
             .where(student.get("status").in(statuses));

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Criteria API: LIKE query
     * Find students by name pattern
     */
    public List<Student> findByNamePatternCriteria(String namePattern) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> query = cb.createQuery(Student.class);
        Root<Student> student = query.from(Student.class);

        Predicate firstNameLike = cb.like(
            cb.lower(student.get("firstName")),
            "%" + namePattern.toLowerCase() + "%"
        );
        Predicate lastNameLike = cb.like(
            cb.lower(student.get("lastName")),
            "%" + namePattern.toLowerCase() + "%"
        );

        query.select(student)
             .where(cb.or(firstNameLike, lastNameLike));

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Criteria API: Date range query
     * Find students born within a date range
     */
    public List<Student> findByDateOfBirthRangeCriteria(LocalDate startDate, LocalDate endDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> query = cb.createQuery(Student.class);
        Root<Student> student = query.from(Student.class);

        query.select(student)
             .where(cb.between(student.get("dateOfBirth"), startDate, endDate));

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Criteria API: Dynamic query with optional parameters
     * Search students with multiple optional filters
     */
    public List<Student> searchStudentsCriteria(String firstName,
                                               String lastName,
                                               Student.Gender gender,
                                               Student.StudentStatus status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> query = cb.createQuery(Student.class);
        Root<Student> student = query.from(Student.class);

        List<Predicate> predicates = new ArrayList<>();

        if (firstName != null && !firstName.isEmpty()) {
            predicates.add(cb.like(
                cb.lower(student.get("firstName")),
                "%" + firstName.toLowerCase() + "%"
            ));
        }

        if (lastName != null && !lastName.isEmpty()) {
            predicates.add(cb.like(
                cb.lower(student.get("lastName")),
                "%" + lastName.toLowerCase() + "%"
            ));
        }

        if (gender != null) {
            predicates.add(cb.equal(student.get("gender"), gender));
        }

        if (status != null) {
            predicates.add(cb.equal(student.get("status"), status));
        }

        query.select(student)
             .where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Criteria API: ORDER BY
     * Find students ordered by name
     */
    public List<Student> findAllOrderedByNameCriteria() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> query = cb.createQuery(Student.class);
        Root<Student> student = query.from(Student.class);

        query.select(student)
             .orderBy(
                 cb.asc(student.get("firstName")),
                 cb.asc(student.get("lastName"))
             );

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Criteria API: JOIN query
     * Find students enrolled in a specific course
     */
    public List<Student> findStudentsEnrolledInCourseCriteria(Long courseId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> query = cb.createQuery(Student.class);
        Root<Student> student = query.from(Student.class);
        Join<Object, Object> enrollments = student.join("enrollments");
        Join<Object, Object> course = enrollments.join("course");

        query.select(student)
             .where(cb.equal(course.get("id"), courseId))
             .distinct(true);

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Criteria API: Aggregation (COUNT)
     * Count students by status
     */
    public Long countByStatusCriteria(Student.StudentStatus status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Student> student = query.from(Student.class);

        query.select(cb.count(student))
             .where(cb.equal(student.get("status"), status));

        return entityManager.createQuery(query).getSingleResult();
    }


    // ==================== NATIVE QUERY EXAMPLES ====================

    /**
     * Native SQL: Custom complex query
     * Find students with their enrollment count
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findStudentsWithEnrollmentCount() {
        String sql = "SELECT s.id, s.student_id, s.first_name, s.last_name, " +
                     "COUNT(e.id) as enrollment_count " +
                     "FROM students s " +
                     "LEFT JOIN enrollments e ON s.id = e.student_id " +
                     "GROUP BY s.id, s.student_id, s.first_name, s.last_name " +
                     "ORDER BY enrollment_count DESC";

        return entityManager.createNativeQuery(sql).getResultList();
    }

    /**
     * Native SQL: Database-specific function
     * Find students by age (using PostgreSQL age function)
     */
    @SuppressWarnings("unchecked")
    public List<Student> findStudentsByAge(int minAge, int maxAge) {
        String sql = "SELECT * FROM students " +
                     "WHERE EXTRACT(YEAR FROM AGE(CURRENT_DATE, date_of_birth)) " +
                     "BETWEEN :minAge AND :maxAge";

        return entityManager.createNativeQuery(sql, Student.class)
                           .setParameter("minAge", minAge)
                           .setParameter("maxAge", maxAge)
                           .getResultList();
    }


    // ==================== CRUD OPERATIONS ====================

    @Transactional
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional
    public Student updateStudent(Long id, Student updatedStudent) {
        Student existingStudent = getStudentById(id);

        existingStudent.setFirstName(updatedStudent.getFirstName());
        existingStudent.setLastName(updatedStudent.getLastName());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setDateOfBirth(updatedStudent.getDateOfBirth());
        existingStudent.setGender(updatedStudent.getGender());
        existingStudent.setAddress(updatedStudent.getAddress());
        existingStudent.setPhoneNumber(updatedStudent.getPhoneNumber());
        existingStudent.setStatus(updatedStudent.getStatus());

        return studentRepository.save(existingStudent);
    }

    @Transactional
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    @Transactional
    public int bulkUpdateStatus(Student.StudentStatus oldStatus, Student.StudentStatus newStatus) {
        return studentRepository.updateStudentStatus(oldStatus, newStatus);
    }
}
