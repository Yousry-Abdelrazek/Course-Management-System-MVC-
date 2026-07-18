package com.codewithyousry.coursemanagementsystemmvc.service;

import com.codewithyousry.coursemanagementsystemmvc.dto.request.EnrollmentCreateRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.request.EnrollmentUpdateRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.response.EnrollmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnrollmentService {

    Page<EnrollmentResponse> findAll(Pageable pageable);

    Page<EnrollmentResponse> findByStudent(Long studentId, Pageable pageable);

    Page<EnrollmentResponse> findByCourse(Long courseId, Pageable pageable);

    EnrollmentResponse getById(Long id);

    EnrollmentUpdateRequest getUpdateRequestById(Long id);

    EnrollmentResponse create(EnrollmentCreateRequest request);

    EnrollmentResponse update(Long id, EnrollmentUpdateRequest request);

    void delete(Long id);

    long countActive();
}
