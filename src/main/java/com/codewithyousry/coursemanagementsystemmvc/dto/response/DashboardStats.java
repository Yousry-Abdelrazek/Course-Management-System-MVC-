package com.codewithyousry.coursemanagementsystemmvc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStats {
    private long instructorCount;
    private long courseCount;
    private long studentCount;
    private long activeEnrollmentCount;
    private List<CourseResponse> recentCourses;
    private List<StudentResponse> recentStudents;
}
