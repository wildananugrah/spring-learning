package com.learning.jpa.repository;

import com.learning.jpa.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // ==================== DERIVED QUERY METHODS ====================

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    List<Enrollment> findByStatus(Enrollment.EnrollmentStatus status);

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByEnrollmentDateBetween(LocalDate startDate, LocalDate endDate);

    List<Enrollment> findByGradeGreaterThanEqual(Double grade);

    long countByStudentId(Long studentId);

    long countByCourseId(Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);


    // ==================== JPQL QUERIES ====================

    // Find enrollments with student and course (solve N+1 problem)
    @Query("SELECT DISTINCT e FROM Enrollment e " +
           "LEFT JOIN FETCH e.student " +
           "LEFT JOIN FETCH e.course " +
           "WHERE e.student.id = :studentId")
    List<Enrollment> findByStudentIdWithDetails(@Param("studentId") Long studentId);

    @Query("SELECT DISTINCT e FROM Enrollment e " +
           "LEFT JOIN FETCH e.student " +
           "LEFT JOIN FETCH e.course " +
           "WHERE e.course.id = :courseId")
    List<Enrollment> findByCourseIdWithDetails(@Param("courseId") Long courseId);

    // Find enrollments by student status
    @Query("SELECT e FROM Enrollment e WHERE e.student.studentId = :studentId AND e.status = :status")
    List<Enrollment> findByStudentIdAndStatus(@Param("studentId") String studentId,
                                              @Param("status") Enrollment.EnrollmentStatus status);

    // Find enrollments by course code
    @Query("SELECT e FROM Enrollment e WHERE e.course.courseCode = :courseCode")
    List<Enrollment> findByCourseCode(@Param("courseCode") String courseCode);

    // Calculate average grade for a student
    @Query("SELECT AVG(e.grade) FROM Enrollment e WHERE e.student.id = :studentId AND e.grade IS NOT NULL")
    Double calculateAverageGradeByStudentId(@Param("studentId") Long studentId);

    // Calculate average grade for a course
    @Query("SELECT AVG(e.grade) FROM Enrollment e WHERE e.course.id = :courseId AND e.grade IS NOT NULL")
    Double calculateAverageGradeByCourseId(@Param("courseId") Long courseId);

    // Find top performers in a course
    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.grade IS NOT NULL " +
           "ORDER BY e.grade DESC")
    List<Enrollment> findTopPerformersByCourse(@Param("courseId") Long courseId);

    // Count enrollments by status and course
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = :status")
    long countByCourseIdAndStatus(@Param("courseId") Long courseId,
                                  @Param("status") Enrollment.EnrollmentStatus status);


    // ==================== NATIVE SQL QUERIES ====================

    // Complex join query
    @Query(value = "SELECT e.* FROM enrollments e " +
                   "JOIN students s ON e.student_id = s.id " +
                   "JOIN courses c ON e.course_id = c.id " +
                   "WHERE s.status = :studentStatus " +
                   "AND c.department = :department",
           nativeQuery = true)
    List<Enrollment> findByStudentStatusAndDepartment(@Param("studentStatus") String studentStatus,
                                                      @Param("department") String department);

    // Get enrollment statistics
    @Query(value = "SELECT c.course_name, COUNT(e.id) as total_enrollments, " +
                   "AVG(e.grade) as avg_grade, MAX(e.grade) as max_grade, MIN(e.grade) as min_grade " +
                   "FROM enrollments e " +
                   "JOIN courses c ON e.course_id = c.id " +
                   "WHERE e.grade IS NOT NULL " +
                   "GROUP BY c.id, c.course_name " +
                   "ORDER BY avg_grade DESC",
           nativeQuery = true)
    List<Object[]> getEnrollmentStatisticsByCourse();

    // Find students enrolled in multiple courses
    @Query(value = "SELECT s.student_id, s.first_name, s.last_name, COUNT(e.id) as course_count " +
                   "FROM students s " +
                   "JOIN enrollments e ON s.id = e.student_id " +
                   "WHERE e.status = 'ENROLLED' " +
                   "GROUP BY s.id, s.student_id, s.first_name, s.last_name " +
                   "HAVING COUNT(e.id) >= :minCourses " +
                   "ORDER BY course_count DESC",
           nativeQuery = true)
    List<Object[]> findStudentsWithMultipleEnrollments(@Param("minCourses") int minCourses);


    // ==================== UPDATE & DELETE QUERIES ====================

    @Modifying
    @Query("UPDATE Enrollment e SET e.status = :newStatus WHERE e.id = :id")
    int updateStatus(@Param("id") Long id, @Param("newStatus") Enrollment.EnrollmentStatus newStatus);

    @Modifying
    @Query("UPDATE Enrollment e SET e.grade = :grade, e.letterGrade = :letterGrade WHERE e.id = :id")
    int updateGrade(@Param("id") Long id,
                   @Param("grade") Double grade,
                   @Param("letterGrade") String letterGrade);

    @Modifying
    @Query("UPDATE Enrollment e SET e.status = :status WHERE e.student.id = :studentId AND e.course.id = :courseId")
    int updateEnrollmentStatus(@Param("studentId") Long studentId,
                              @Param("courseId") Long courseId,
                              @Param("status") Enrollment.EnrollmentStatus status);

    @Modifying
    @Query("DELETE FROM Enrollment e WHERE e.student.id = :studentId AND e.status = :status")
    int deleteByStudentIdAndStatus(@Param("studentId") Long studentId,
                                   @Param("status") Enrollment.EnrollmentStatus status);
}
