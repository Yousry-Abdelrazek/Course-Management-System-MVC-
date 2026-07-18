package com.codewithyousry.coursemanagementsystemmvc.service;

import com.codewithyousry.coursemanagementsystemmvc.dto.request.CourseRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.response.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {

    Page<CourseResponse> search(String query, Pageable pageable);

    CourseResponse getById(Long id);

    CourseRequest getRequestById(Long id);

    CourseResponse create(CourseRequest request);

    CourseResponse update(Long id, CourseRequest request);

    void softDelete(Long id);

    long count();
}
