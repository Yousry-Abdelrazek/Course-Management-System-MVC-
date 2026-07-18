package com.codewithyousry.coursemanagementsystemmvc.service;

import com.codewithyousry.coursemanagementsystemmvc.dto.request.StudentRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.response.StudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {

    Page<StudentResponse> search(String query, Pageable pageable);

    StudentResponse getById(Long id);

    StudentRequest getRequestById(Long id);

    StudentResponse create(StudentRequest request);

    StudentResponse update(Long id, StudentRequest request);

    void delete(Long id);

    long count();
}
