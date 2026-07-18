package com.codewithyousry.coursemanagementsystemmvc.mapper;

import com.codewithyousry.coursemanagementsystemmvc.dto.request.CourseRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.response.CourseResponse;
import com.codewithyousry.coursemanagementsystemmvc.entity.Course;
import com.codewithyousry.coursemanagementsystemmvc.entity.Instructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CourseMapper {

    public Course toEntity(CourseRequest request, Instructor instructor) {
        return Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .registrationStartTime(request.getRegistrationStartTime())
                .registrationEndTime(request.getRegistrationEndTime())
                .instructor(instructor)
                .deleted(false)
                .build();
    }

    public void updateEntity(Course course, CourseRequest request, Instructor instructor) {
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice());
        course.setStartDate(request.getStartDate());
        course.setEndDate(request.getEndDate());
        course.setRegistrationStartTime(request.getRegistrationStartTime());
        course.setRegistrationEndTime(request.getRegistrationEndTime());
        course.setInstructor(instructor);
    }

    public CourseResponse toResponse(Course course, long enrollmentCount) {
        Instructor instructor = course.getInstructor();
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .registrationStartTime(course.getRegistrationStartTime())
                .registrationEndTime(course.getRegistrationEndTime())
                .deleted(course.isDeleted())
                .instructorId(instructor != null ? instructor.getId() : null)
                .instructorName(instructor != null ? instructor.getName() : null)
                .enrollmentCount(enrollmentCount)
                .registrationOpen(isRegistrationOpen(course))
                .build();
    }

    public CourseRequest toRequest(Course course) {
        return CourseRequest.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .registrationStartTime(course.getRegistrationStartTime())
                .registrationEndTime(course.getRegistrationEndTime())
                .instructorId(course.getInstructor() != null ? course.getInstructor().getId() : null)
                .build();
    }

    private boolean isRegistrationOpen(Course course) {
        LocalDateTime now = LocalDateTime.now();
        if (course.getRegistrationStartTime() != null && now.isBefore(course.getRegistrationStartTime())) {
            return false;
        }
        return course.getRegistrationEndTime() == null || !now.isAfter(course.getRegistrationEndTime());
    }
}
