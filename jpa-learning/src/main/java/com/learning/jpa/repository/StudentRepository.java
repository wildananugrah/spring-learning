package com.learning.jpa.repository;

import com.learning.jpa.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // ==================== DERIVED QUERY METHODS ====================
    // Spring Data JPA automatically implements these based on method names

    Optional<Student> findByStudentId(String studentId);

    Optional<Student> findByEmail(String email);

    List<Student> findByFirstNameAndLastName(String firstName, String lastName);

    List<Student> findByStatus(Student.StudentStatus status);

    List<Student> findByGender(Student.Gender gender);

    // Find students by first name (case-insensitive)
    List<Student> findByFirstNameIgnoreCase(String firstName);

    // Find students by first name containing (partial match)
    List<Student> findByFirstNameContaining(String firstName);

    // Find students by first name starting with
    List<Student> findByFirstNameStartingWith(String prefix);

    // Find students born after a specific date
    List<Student> findByDateOfBirthAfter(LocalDate date);

    // Find students born between dates
    List<Student> findByDateOfBirthBetween(LocalDate startDate, LocalDate endDate);

    // Find by multiple statuses
    List<Student> findByStatusIn(List<Student.StudentStatus> statuses);

    // Check existence
    boolean existsByEmail(String email);

    boolean existsByStudentId(String studentId);

    // Count queries
    long countByStatus(Student.StudentStatus status);

    // Delete queries
    void deleteByStudentId(String studentId);


    // ==================== JPQL QUERIES ====================
    // Using @Query with JPQL (Java Persistence Query Language)

    @Query("SELECT s FROM Student s WHERE s.firstName = :firstName AND s.lastName = :lastName")
    List<Student> findStudentsByFullName(@Param("firstName") String firstName,
                                         @Param("lastName") String lastName);

    @Query("SELECT s FROM Student s WHERE LOWER(s.email) LIKE LOWER(CONCAT('%', :domain, '%'))")
    List<Student> findByEmailDomain(@Param("domain") String domain);

    @Query("SELECT s FROM Student s WHERE s.status = :status ORDER BY s.firstName ASC")
    List<Student> findActiveStudentsSorted(@Param("status") Student.StudentStatus status);

    // JPQL with JOIN FETCH to solve N+1 problem
    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.enrollments WHERE s.id = :id")
    Optional<Student> findByIdWithEnrollments(@Param("id") Long id);

    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.enrollments e " +
           "LEFT JOIN FETCH e.course WHERE s.status = :status")
    List<Student> findByStatusWithEnrollmentsAndCourses(@Param("status") Student.StudentStatus status);

    // JPQL with aggregation
    @Query("SELECT COUNT(s) FROM Student s WHERE s.gender = :gender AND s.status = :status")
    long countByGenderAndStatus(@Param("gender") Student.Gender gender,
                                 @Param("status") Student.StudentStatus status);

    // JPQL with pagination
    @Query("SELECT s FROM Student s WHERE s.status = :status")
    Page<Student> findStudentsByStatus(@Param("status") Student.StudentStatus status, Pageable pageable);


    // ==================== NATIVE SQL QUERIES ====================
    // Using native SQL for complex queries or database-specific features

    @Query(value = "SELECT * FROM students WHERE date_of_birth > :date ORDER BY date_of_birth DESC",
           nativeQuery = true)
    List<Student> findStudentsYoungerThan(@Param("date") LocalDate date);

    @Query(value = "SELECT s.* FROM students s " +
                   "JOIN enrollments e ON s.id = e.student_id " +
                   "WHERE e.course_id = :courseId",
           nativeQuery = true)
    List<Student> findStudentsByCourseNative(@Param("courseId") Long courseId);

    @Query(value = "SELECT DISTINCT s.* FROM students s " +
                   "JOIN enrollments e ON s.id = e.student_id " +
                   "WHERE e.grade >= :minGrade",
           nativeQuery = true)
    List<Student> findStudentsWithMinGrade(@Param("minGrade") Double minGrade);

    // Native query with aggregation
    @Query(value = "SELECT s.gender, COUNT(*) as count " +
                   "FROM students s " +
                   "WHERE s.status = :status " +
                   "GROUP BY s.gender",
           nativeQuery = true)
    List<Object[]> countStudentsByGenderAndStatus(@Param("status") String status);

    // Complex native query with subquery
    @Query(value = "SELECT * FROM students WHERE id IN " +
                   "(SELECT DISTINCT student_id FROM enrollments " +
                   "WHERE status = 'COMPLETED' " +
                   "GROUP BY student_id " +
                   "HAVING COUNT(*) >= :minCourses)",
           nativeQuery = true)
    List<Student> findStudentsWithMinCompletedCourses(@Param("minCourses") int minCourses);


    // ==================== UPDATE & DELETE QUERIES ====================
    // Using @Modifying for UPDATE and DELETE operations

    @Modifying
    @Query("UPDATE Student s SET s.status = :newStatus WHERE s.status = :oldStatus")
    int updateStudentStatus(@Param("oldStatus") Student.StudentStatus oldStatus,
                           @Param("newStatus") Student.StudentStatus newStatus);

    @Modifying
    @Query("UPDATE Student s SET s.phoneNumber = :phoneNumber WHERE s.id = :id")
    int updatePhoneNumber(@Param("id") Long id, @Param("phoneNumber") String phoneNumber);

    @Modifying
    @Query("UPDATE Student s SET s.address = :address, s.phoneNumber = :phoneNumber WHERE s.id = :id")
    int updateContactInfo(@Param("id") Long id,
                         @Param("address") String address,
                         @Param("phoneNumber") String phoneNumber);

    @Modifying
    @Query(value = "UPDATE students SET status = 'GRADUATED' " +
                   "WHERE id IN (SELECT DISTINCT student_id FROM enrollments " +
                   "WHERE status = 'COMPLETED' " +
                   "GROUP BY student_id " +
                   "HAVING COUNT(*) >= :requiredCourses)",
           nativeQuery = true)
    int markStudentsAsGraduated(@Param("requiredCourses") int requiredCourses);

    @Modifying
    @Query("DELETE FROM Student s WHERE s.status = :status AND s.createdAt < :date")
    int deleteInactiveStudents(@Param("status") Student.StudentStatus status,
                              @Param("date") java.time.LocalDateTime date);
}
