package com.learning.jpa.dto;

import com.learning.jpa.entity.Student;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequest {

    @NotBlank(message = "Student ID is required")
    @Size(max = 50)
    private String studentId;

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100)
    private String email;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Student.Gender gender;

    @Size(max = 255)
    private String address;

    @Size(max = 20)
    private String phoneNumber;

    @NotNull(message = "Status is required")
    private Student.StudentStatus status;
}
