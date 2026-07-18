package com.codewithyousry.coursemanagementsystemmvc.service.impl;

import com.codewithyousry.coursemanagementsystemmvc.dto.request.EnrollmentCreateRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.request.EnrollmentUpdateRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.response.EnrollmentResponse;
import com.codewithyousry.coursemanagementsystemmvc.entity.Course;
import com.codewithyousry.coursemanagementsystemmvc.entity.Enrollment;
import com.codewithyousry.coursemanagementsystemmvc.entity.EnrollmentStatus;
import com.codewithyousry.coursemanagementsystemmvc.entity.Student;
import com.codewithyousry.coursemanagementsystemmvc.exception.DuplicateResourceException;
import com.codewithyousry.coursemanagementsystemmvc.exception.RegistrationClosedException;
import com.codewithyousry.coursemanagementsystemmvc.exception.ResourceNotFoundException;
import com.codewithyousry.coursemanagementsystemmvc.mapper.EnrollmentMapper;
import com.codewithyousry.coursemanagementsystemmvc.repository.CourseRepository;
import com.codewithyousry.coursemanagementsystemmvc.repository.EnrollmentRepository;
import com.codewithyousry.coursemanagementsystemmvc.repository.StudentRepository;
import com.codewithyousry.coursemanagementsystemmvc.service.EnrollmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 StudentRepository studentRepository,
                                 CourseRepository courseRepository,
                                 EnrollmentMapper enrollmentMapper) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentMapper = enrollmentMapper;
    }

    @Override
    public Page<EnrollmentResponse> findAll(Pageable pageable) {
        return enrollmentRepository.findAll(pageable).map(enrollmentMapper::toResponse);
    }

    @Override
    public Page<EnrollmentResponse> findByStudent(Long studentId, Pageable pageable) {
        return enrollmentRepository.findByStudentId(studentId, pageable).map(enrollmentMapper::toResponse);
    }

    @Override
    public Page<EnrollmentResponse> findByCourse(Long courseId, Pageable pageable) {
        return enrollmentRepository.findByCourseId(courseId, pageable).map(enrollmentMapper::toResponse);
    }

    @Override
    public EnrollmentResponse getById(Long id) {
        return enrollmentMapper.toResponse(findEntity(id));
    }

    @Override
    public EnrollmentUpdateRequest getUpdateRequestById(Long id) {
        Enrollment enrollment = findEntity(id);
        return EnrollmentUpdateRequest.builder()
                .id(enrollment.getId())
                .grade(enrollment.getGrade())
                .progress(enrollment.getProgress())
                .status(enrollment.getStatus())
                .build();
    }

    @Override
    @Transactional
    public EnrollmentResponse create(EnrollmentCreateRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", request.getStudentId()));

        // Deleted courses are hidden by @SQLRestriction, so this naturally blocks enrolling into them.
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with id " + request.getCourseId()
                                + " (it may have been deleted)"));

        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new DuplicateResourceException("Student '" + student.getName()
                    + "' is already enrolled in course '" + course.getTitle() + "'");
        }

        validateRegistrationWindow(course);

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrolledOn(LocalDate.now())
                .progress(0)
                .status(EnrollmentStatus.ACTIVE)
                .build();
        return enrollmentMapper.toResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    @Transactional
    public EnrollmentResponse update(Long id, EnrollmentUpdateRequest request) {
        Enrollment enrollment = findEntity(id);
        enrollment.setGrade(request.getGrade());
        enrollment.setProgress(request.getProgress());
        enrollment.setStatus(request.getStatus());
        return enrollmentMapper.toResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Enrollment enrollment = findEntity(id);
        enrollmentRepository.delete(enrollment);
    }

    @Override
    public long countActive() {
        return enrollmentRepository.countByStatus(EnrollmentStatus.ACTIVE);
    }

    private void validateRegistrationWindow(Course course) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = course.getRegistrationStartTime();
        LocalDateTime end = course.getRegistrationEndTime();

        if (start != null && now.isBefore(start)) {
            throw new RegistrationClosedException("Registration has not started yet");
        }
        if (end != null && now.isAfter(end)) {
            throw new RegistrationClosedException("Registration has ended");
        }
    }

    private Enrollment findEntity(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", id));
    }
}
