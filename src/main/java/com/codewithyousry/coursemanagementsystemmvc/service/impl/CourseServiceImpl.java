package com.codewithyousry.coursemanagementsystemmvc.service.impl;

import com.codewithyousry.coursemanagementsystemmvc.dto.request.CourseRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.response.CourseResponse;
import com.codewithyousry.coursemanagementsystemmvc.entity.Course;
import com.codewithyousry.coursemanagementsystemmvc.entity.Instructor;
import com.codewithyousry.coursemanagementsystemmvc.exception.ResourceNotFoundException;
import com.codewithyousry.coursemanagementsystemmvc.mapper.CourseMapper;
import com.codewithyousry.coursemanagementsystemmvc.repository.CourseRepository;
import com.codewithyousry.coursemanagementsystemmvc.repository.EnrollmentRepository;
import com.codewithyousry.coursemanagementsystemmvc.repository.InstructorRepository;
import com.codewithyousry.coursemanagementsystemmvc.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseMapper courseMapper;

    public CourseServiceImpl(CourseRepository courseRepository,
                             InstructorRepository instructorRepository,
                             EnrollmentRepository enrollmentRepository,
                             CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseMapper = courseMapper;
    }

    @Override
    public Page<CourseResponse> search(String query, Pageable pageable) {
        Page<Course> page = StringUtils.hasText(query)
                ? courseRepository.findByTitleContainingIgnoreCase(query, pageable)
                : courseRepository.findAll(pageable);
        return page.map(course ->
                courseMapper.toResponse(course, enrollmentRepository.countByCourseId(course.getId())));
    }

    @Override
    public CourseResponse getById(Long id) {
        Course course = findEntity(id);
        return courseMapper.toResponse(course, enrollmentRepository.countByCourseId(id));
    }

    @Override
    public CourseRequest getRequestById(Long id) {
        return courseMapper.toRequest(findEntity(id));
    }

    @Override
    @Transactional
    public CourseResponse create(CourseRequest request) {
        Instructor instructor = resolveInstructor(request.getInstructorId());
        Course saved = courseRepository.save(courseMapper.toEntity(request, instructor));
        return courseMapper.toResponse(saved, 0);
    }

    @Override
    @Transactional
    public CourseResponse update(Long id, CourseRequest request) {
        Course course = findEntity(id);
        Instructor instructor = resolveInstructor(request.getInstructorId());
        courseMapper.updateEntity(course, request, instructor);
        Course saved = courseRepository.save(course);
        return courseMapper.toResponse(saved, enrollmentRepository.countByCourseId(id));
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        Course course = findEntity(id);
        course.setDeleted(true);
        courseRepository.save(course);
    }

    @Override
    public long count() {
        return courseRepository.count();
    }

    private Course findEntity(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }

    private Instructor resolveInstructor(Long instructorId) {
        if (instructorId == null) {
            return null;
        }
        return instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", instructorId));
    }
}
