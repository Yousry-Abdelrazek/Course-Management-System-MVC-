package com.codewithyousry.coursemanagementsystemmvc.service.impl;

import com.codewithyousry.coursemanagementsystemmvc.dto.request.InstructorRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.response.InstructorResponse;
import com.codewithyousry.coursemanagementsystemmvc.entity.Instructor;
import com.codewithyousry.coursemanagementsystemmvc.exception.BusinessException;
import com.codewithyousry.coursemanagementsystemmvc.exception.DuplicateResourceException;
import com.codewithyousry.coursemanagementsystemmvc.exception.ResourceNotFoundException;
import com.codewithyousry.coursemanagementsystemmvc.mapper.InstructorMapper;
import com.codewithyousry.coursemanagementsystemmvc.repository.CourseRepository;
import com.codewithyousry.coursemanagementsystemmvc.repository.InstructorRepository;
import com.codewithyousry.coursemanagementsystemmvc.service.InstructorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final InstructorMapper instructorMapper;

    public InstructorServiceImpl(InstructorRepository instructorRepository,
                                 CourseRepository courseRepository,
                                 InstructorMapper instructorMapper) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
        this.instructorMapper = instructorMapper;
    }

    @Override
    public Page<InstructorResponse> search(String query, Pageable pageable) {
        Page<Instructor> page = StringUtils.hasText(query)
                ? instructorRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, pageable)
                : instructorRepository.findAll(pageable);
        return page.map(instructor ->
                instructorMapper.toResponse(instructor, courseRepository.countByInstructorId(instructor.getId())));
    }

    @Override
    public InstructorResponse getById(Long id) {
        Instructor instructor = findEntity(id);
        return instructorMapper.toResponse(instructor, courseRepository.countByInstructorId(id));
    }

    @Override
    public InstructorRequest getRequestById(Long id) {
        return instructorMapper.toRequest(findEntity(id));
    }

    @Override
    @Transactional
    public InstructorResponse create(InstructorRequest request) {
        if (instructorRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException("An instructor with email '" + request.getEmail() + "' already exists");
        }
        Instructor saved = instructorRepository.save(instructorMapper.toEntity(request));
        return instructorMapper.toResponse(saved, 0);
    }

    @Override
    @Transactional
    public InstructorResponse update(Long id, InstructorRequest request) {
        Instructor instructor = findEntity(id);
        if (instructorRepository.existsByEmailIgnoreCaseAndIdNot(request.getEmail(), id)) {
            throw new DuplicateResourceException("An instructor with email '" + request.getEmail() + "' already exists");
        }
        instructorMapper.updateEntity(instructor, request);
        Instructor saved = instructorRepository.save(instructor);
        return instructorMapper.toResponse(saved, courseRepository.countByInstructorId(id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Instructor instructor = findEntity(id);
        long activeCourses = courseRepository.countByInstructorId(id);
        if (activeCourses > 0) {
            throw new BusinessException("Cannot delete instructor '" + instructor.getName()
                    + "' because " + activeCourses + " active course(s) are assigned. Reassign or delete them first.");
        }
        instructorRepository.delete(instructor);
    }

    @Override
    public long count() {
        return instructorRepository.count();
    }

    private Instructor findEntity(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", id));
    }
}
