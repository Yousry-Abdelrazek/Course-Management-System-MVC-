package com.codewithyousry.coursemanagementsystemmvc.service;

import com.codewithyousry.coursemanagementsystemmvc.dto.request.InstructorRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.response.InstructorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InstructorService {

    Page<InstructorResponse> search(String query, Pageable pageable);

    InstructorResponse getById(Long id);

    InstructorRequest getRequestById(Long id);

    InstructorResponse create(InstructorRequest request);

    InstructorResponse update(Long id, InstructorRequest request);

    void delete(Long id);

    long count();
}
