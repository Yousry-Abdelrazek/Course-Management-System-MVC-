package com.codewithyousry.coursemanagementsystemmvc.service.impl;

import com.codewithyousry.coursemanagementsystemmvc.dto.request.StudentRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.response.StudentResponse;
import com.codewithyousry.coursemanagementsystemmvc.entity.Student;
import com.codewithyousry.coursemanagementsystemmvc.exception.BusinessException;
import com.codewithyousry.coursemanagementsystemmvc.exception.DuplicateResourceException;
import com.codewithyousry.coursemanagementsystemmvc.exception.ResourceNotFoundException;
import com.codewithyousry.coursemanagementsystemmvc.mapper.StudentMapper;
import com.codewithyousry.coursemanagementsystemmvc.repository.EnrollmentRepository;
import com.codewithyousry.coursemanagementsystemmvc.repository.StudentRepository;
import com.codewithyousry.coursemanagementsystemmvc.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentMapper studentMapper;

    public StudentServiceImpl(StudentRepository studentRepository,
                              EnrollmentRepository enrollmentRepository,
                              StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.studentMapper = studentMapper;
    }

    @Override
    public Page<StudentResponse> search(String query, Pageable pageable) {
        Page<Student> page = StringUtils.hasText(query)
                ? studentRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, pageable)
                : studentRepository.findAll(pageable);
        return page.map(student ->
                studentMapper.toResponse(student, enrollmentRepository.countByStudentId(student.getId())));
    }

    @Override
    public StudentResponse getById(Long id) {
        Student student = findEntity(id);
        return studentMapper.toResponse(student, enrollmentRepository.countByStudentId(id));
    }

    @Override
    public StudentRequest getRequestById(Long id) {
        return studentMapper.toRequest(findEntity(id));
    }

    @Override
    @Transactional
    public StudentResponse create(StudentRequest request) {
        if (studentRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException("A student with email '" + request.getEmail() + "' already exists");
        }
        Student saved = studentRepository.save(studentMapper.toEntity(request));
        return studentMapper.toResponse(saved, 0);
    }

    @Override
    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = findEntity(id);
        if (studentRepository.existsByEmailIgnoreCaseAndIdNot(request.getEmail(), id)) {
            throw new DuplicateResourceException("A student with email '" + request.getEmail() + "' already exists");
        }
        studentMapper.updateEntity(student, request);
        Student saved = studentRepository.save(student);
        return studentMapper.toResponse(saved, enrollmentRepository.countByStudentId(id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Student student = findEntity(id);
        if (enrollmentRepository.existsByStudentId(id)) {
            throw new BusinessException("Cannot delete student '" + student.getName()
                    + "' because they have existing enrollments. Remove the enrollments first.");
        }
        studentRepository.delete(student);
    }

    @Override
    public long count() {
        return studentRepository.count();
    }

    private Student findEntity(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }
}
