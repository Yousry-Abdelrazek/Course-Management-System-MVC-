package com.codewithyousry.coursemanagementsystemmvc.mapper;

import com.codewithyousry.coursemanagementsystemmvc.dto.response.EnrollmentResponse;
import com.codewithyousry.coursemanagementsystemmvc.entity.Enrollment;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentMapper {

    public EnrollmentResponse toResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent().getId())
                .studentName(enrollment.getStudent().getName())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .enrolledOn(enrollment.getEnrolledOn())
                .grade(enrollment.getGrade())
                .progress(enrollment.getProgress())
                .status(enrollment.getStatus())
                .build();
    }
}
