package com.codewithyousry.coursemanagementsystemmvc.dto.response;

import com.codewithyousry.coursemanagementsystemmvc.entity.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseTitle;
    private LocalDate enrolledOn;
    private String grade;
    private Integer progress;
    private EnrollmentStatus status;
}
