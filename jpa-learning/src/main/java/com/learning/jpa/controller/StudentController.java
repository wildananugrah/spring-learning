package com.learning.jpa.controller;

import com.learning.jpa.dto.ApiResponse;
import com.learning.jpa.dto.StudentRequest;
import com.learning.jpa.entity.Student;
import com.learning.jpa.repository.StudentRepository;
import com.learning.jpa.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentRepository studentRepository;

    // ==================== CRUD OPERATIONS ====================

    @PostMapping
    public ResponseEntity<ApiResponse<Student>> createStudent(@Valid @RequestBody StudentRequest request) {
        Student student = Student.builder()
                .studentId(request.getStudentId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .status(request.getStatus())
                .build();

        Student created = studentService.createStudent(student);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student created successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> getStudent(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success("Student retrieved", student));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Student>>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(ApiResponse.success("Students retrieved", students));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequest request) {

        Student student = Student.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .status(request.getStatus())
                .build();

        Student updated = studentService.updateStudent(id, student);
        return ResponseEntity.ok(ApiResponse.success("Student updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted", null));
    }


    // ==================== DERIVED QUERY METHODS ====================

    @GetMapping("/by-student-id/{studentId}")
    public ResponseEntity<ApiResponse<Student>> getByStudentId(@PathVariable String studentId) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return ResponseEntity.ok(ApiResponse.success("Student found", student));
    }

    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse<Student>> getByEmail(@RequestParam String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return ResponseEntity.ok(ApiResponse.success("Student found", student));
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<ApiResponse<List<Student>>> getByStatus(@PathVariable Student.StudentStatus status) {
        List<Student> students = studentRepository.findByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Students found", students));
    }

    @GetMapping("/by-gender/{gender}")
    public ResponseEntity<ApiResponse<List<Student>>> getByGender(@PathVariable Student.Gender gender) {
        List<Student> students = studentRepository.findByGender(gender);
        return ResponseEntity.ok(ApiResponse.success("Students found", students));
    }

    @GetMapping("/search-by-name")
    public ResponseEntity<ApiResponse<List<Student>>> searchByName(@RequestParam String firstName) {
        List<Student> students = studentRepository.findByFirstNameContaining(firstName);
        return ResponseEntity.ok(ApiResponse.success("Students found", students));
    }


    // ==================== JPQL QUERIES ====================

    @GetMapping("/by-full-name")
    public ResponseEntity<ApiResponse<List<Student>>> getByFullName(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        List<Student> students = studentRepository.findStudentsByFullName(firstName, lastName);
        return ResponseEntity.ok(ApiResponse.success("Students found", students));
    }

    @GetMapping("/by-email-domain")
    public ResponseEntity<ApiResponse<List<Student>>> getByEmailDomain(@RequestParam String domain) {
        List<Student> students = studentRepository.findByEmailDomain(domain);
        return ResponseEntity.ok(ApiResponse.success("Students found", students));
    }

    @GetMapping("/{id}/with-enrollments")
    public ResponseEntity<ApiResponse<Student>> getWithEnrollments(@PathVariable Long id) {
        Student student = studentRepository.findByIdWithEnrollments(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return ResponseEntity.ok(ApiResponse.success("Student with enrollments retrieved", student));
    }

    @GetMapping("/count-by-gender-status")
    public ResponseEntity<ApiResponse<Long>> countByGenderAndStatus(
            @RequestParam Student.Gender gender,
            @RequestParam Student.StudentStatus status) {
        long count = studentRepository.countByGenderAndStatus(gender, status);
        return ResponseEntity.ok(ApiResponse.success("Count retrieved", count));
    }


    // ==================== NATIVE QUERIES ====================

    @GetMapping("/younger-than")
    public ResponseEntity<ApiResponse<List<Student>>> getYoungerThan(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<Student> students = studentRepository.findStudentsYoungerThan(localDate);
        return ResponseEntity.ok(ApiResponse.success("Students found", students));
    }

    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<ApiResponse<List<Student>>> getByCourse(@PathVariable Long courseId) {
        List<Student> students = studentRepository.findStudentsByCourseNative(courseId);
        return ResponseEntity.ok(ApiResponse.success("Students found", students));
    }

    @GetMapping("/with-min-grade")
    public ResponseEntity<ApiResponse<List<Student>>> getWithMinGrade(@RequestParam Double minGrade) {
        List<Student> students = studentRepository.findStudentsWithMinGrade(minGrade);
        return ResponseEntity.ok(ApiResponse.success("Students found", students));
    }


    // ==================== CRITERIA API QUERIES ====================

    @GetMapping("/criteria/by-status/{status}")
    public ResponseEntity<ApiResponse<List<Student>>> getByStatusCriteria(
            @PathVariable Student.StudentStatus status) {
        List<Student> students = studentService.findByStatusCriteria(status);
        return ResponseEntity.ok(ApiResponse.success("Students found using Criteria API", students));
    }

    @GetMapping("/criteria/by-gender-status")
    public ResponseEntity<ApiResponse<List<Student>>> getByGenderAndStatusCriteria(
            @RequestParam Student.Gender gender,
            @RequestParam Student.StudentStatus status) {
        List<Student> students = studentService.findByGenderAndStatusCriteria(gender, status);
        return ResponseEntity.ok(ApiResponse.success("Students found using Criteria API", students));
    }

    @GetMapping("/criteria/search-by-name")
    public ResponseEntity<ApiResponse<List<Student>>> searchByNameCriteria(@RequestParam String pattern) {
        List<Student> students = studentService.findByNamePatternCriteria(pattern);
        return ResponseEntity.ok(ApiResponse.success("Students found using Criteria API", students));
    }

    @GetMapping("/criteria/date-range")
    public ResponseEntity<ApiResponse<List<Student>>> getByDateRangeCriteria(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<Student> students = studentService.findByDateOfBirthRangeCriteria(start, end);
        return ResponseEntity.ok(ApiResponse.success("Students found using Criteria API", students));
    }

    @GetMapping("/criteria/search")
    public ResponseEntity<ApiResponse<List<Student>>> searchCriteria(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Student.Gender gender,
            @RequestParam(required = false) Student.StudentStatus status) {
        List<Student> students = studentService.searchStudentsCriteria(firstName, lastName, gender, status);
        return ResponseEntity.ok(ApiResponse.success("Students found using dynamic Criteria API", students));
    }

    @GetMapping("/criteria/enrolled-in-course/{courseId}")
    public ResponseEntity<ApiResponse<List<Student>>> getEnrolledInCourseCriteria(@PathVariable Long courseId) {
        List<Student> students = studentService.findStudentsEnrolledInCourseCriteria(courseId);
        return ResponseEntity.ok(ApiResponse.success("Students found using Criteria API with JOIN", students));
    }


    // ==================== UPDATE OPERATIONS ====================

    @PatchMapping("/{id}/phone")
    public ResponseEntity<ApiResponse<Integer>> updatePhone(
            @PathVariable Long id,
            @RequestParam String phoneNumber) {
        int updated = studentRepository.updatePhoneNumber(id, phoneNumber);
        return ResponseEntity.ok(ApiResponse.success("Phone number updated", updated));
    }

    @PatchMapping("/bulk-update-status")
    public ResponseEntity<ApiResponse<Integer>> bulkUpdateStatus(
            @RequestParam Student.StudentStatus oldStatus,
            @RequestParam Student.StudentStatus newStatus) {
        int updated = studentService.bulkUpdateStatus(oldStatus, newStatus);
        return ResponseEntity.ok(ApiResponse.success(updated + " students updated", updated));
    }


    // ==================== STATISTICS ====================

    @GetMapping("/statistics/enrollment-count")
    public ResponseEntity<ApiResponse<List<Object[]>>> getEnrollmentStatistics() {
        List<Object[]> stats = studentService.findStudentsWithEnrollmentCount();
        return ResponseEntity.ok(ApiResponse.success("Statistics retrieved", stats));
    }

    @GetMapping("/statistics/by-age")
    public ResponseEntity<ApiResponse<List<Student>>> getByAge(
            @RequestParam int minAge,
            @RequestParam int maxAge) {
        List<Student> students = studentService.findStudentsByAge(minAge, maxAge);
        return ResponseEntity.ok(ApiResponse.success("Students found by age", students));
    }
}
