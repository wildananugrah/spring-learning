package com.learning.jpa.repository;

import com.learning.jpa.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // ==================== DERIVED QUERY METHODS ====================

    Optional<Course> findByCourseCode(String courseCode);

    List<Course> findByDepartment(String department);

    List<Course> findByLevel(Course.CourseLevel level);

    List<Course> findByInstructor(String instructor);

    List<Course> findByCourseNameContaining(String keyword);

    List<Course> findByCreditsGreaterThanEqual(Integer credits);

    List<Course> findByCurrentEnrollmentLessThan(Integer maxEnrollment);

    boolean existsByCourseCode(String courseCode);


    // ==================== JPQL QUERIES ====================

    @Query("SELECT c FROM Course c WHERE c.department = :department AND c.level = :level")
    List<Course> findByDepartmentAndLevel(@Param("department") String department,
                                          @Param("level") Course.CourseLevel level);

    // JOIN FETCH to solve N+1 problem
    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.enrollments WHERE c.id = :id")
    Optional<Course> findByIdWithEnrollments(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.enrollments e " +
           "LEFT JOIN FETCH e.student WHERE c.courseCode = :courseCode")
    Optional<Course> findByCourseCodeWithStudents(@Param("courseCode") String courseCode);

    // Find available courses (not full)
    @Query("SELECT c FROM Course c WHERE c.currentEnrollment < c.maxEnrollment")
    List<Course> findAvailableCourses();

    // Find courses with minimum enrollment
    @Query("SELECT c FROM Course c WHERE c.currentEnrollment >= :minEnrollment ORDER BY c.currentEnrollment DESC")
    List<Course> findPopularCourses(@Param("minEnrollment") Integer minEnrollment);


    // ==================== NATIVE SQL QUERIES ====================

    @Query(value = "SELECT * FROM courses WHERE credits >= :minCredits ORDER BY credits DESC",
           nativeQuery = true)
    List<Course> findCoursesWithMinCredits(@Param("minCredits") Integer minCredits);

    @Query(value = "SELECT c.*, COUNT(e.id) as enrollment_count " +
                   "FROM courses c " +
                   "LEFT JOIN enrollments e ON c.id = e.course_id " +
                   "GROUP BY c.id " +
                   "ORDER BY enrollment_count DESC " +
                   "LIMIT :limit",
           nativeQuery = true)
    List<Course> findTopEnrolledCourses(@Param("limit") int limit);


    // ==================== UPDATE QUERIES ====================

    @Modifying
    @Query("UPDATE Course c SET c.currentEnrollment = c.currentEnrollment + 1 WHERE c.id = :id")
    int incrementEnrollment(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Course c SET c.currentEnrollment = c.currentEnrollment - 1 " +
           "WHERE c.id = :id AND c.currentEnrollment > 0")
    int decrementEnrollment(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Course c SET c.instructor = :instructor WHERE c.id = :id")
    int updateInstructor(@Param("id") Long id, @Param("instructor") String instructor);

    @Modifying
    @Query("UPDATE Course c SET c.maxEnrollment = :maxEnrollment WHERE c.id = :id")
    int updateMaxEnrollment(@Param("id") Long id, @Param("maxEnrollment") Integer maxEnrollment);
}
