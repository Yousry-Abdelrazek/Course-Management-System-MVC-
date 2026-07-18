package com.codewithyousry.coursemanagementsystemmvc.service.impl;

import com.codewithyousry.coursemanagementsystemmvc.dto.response.CourseResponse;
import com.codewithyousry.coursemanagementsystemmvc.dto.response.DashboardStats;
import com.codewithyousry.coursemanagementsystemmvc.dto.response.StudentResponse;
import com.codewithyousry.coursemanagementsystemmvc.entity.EnrollmentStatus;
import com.codewithyousry.coursemanagementsystemmvc.mapper.CourseMapper;
import com.codewithyousry.coursemanagementsystemmvc.mapper.StudentMapper;
import com.codewithyousry.coursemanagementsystemmvc.repository.CourseRepository;
import com.codewithyousry.coursemanagementsystemmvc.repository.EnrollmentRepository;
import com.codewithyousry.coursemanagementsystemmvc.repository.InstructorRepository;
import com.codewithyousry.coursemanagementsystemmvc.repository.StudentRepository;
import com.codewithyousry.coursemanagementsystemmvc.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseMapper courseMapper;
    private final StudentMapper studentMapper;

    public DashboardServiceImpl(InstructorRepository instructorRepository,
                                CourseRepository courseRepository,
                                StudentRepository studentRepository,
                                EnrollmentRepository enrollmentRepository,
                                CourseMapper courseMapper,
                                StudentMapper studentMapper) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseMapper = courseMapper;
        this.studentMapper = studentMapper;
    }

    @Override
    public DashboardStats getStats() {
        List<CourseResponse> recentCourses = courseRepository.findTop5ByOrderByIdDesc().stream()
                .map(course -> courseMapper.toResponse(course, enrollmentRepository.countByCourseId(course.getId())))
                .toList();

        List<StudentResponse> recentStudents = studentRepository.findTop5ByOrderByIdDesc().stream()
                .map(student -> studentMapper.toResponse(student, enrollmentRepository.countByStudentId(student.getId())))
                .toList();

        return DashboardStats.builder()
                .instructorCount(instructorRepository.count())
                .courseCount(courseRepository.count())
                .studentCount(studentRepository.count())
                .activeEnrollmentCount(enrollmentRepository.countByStatus(EnrollmentStatus.ACTIVE))
                .recentCourses(recentCourses)
                .recentStudents(recentStudents)
                .build();
    }
}
