package com.codewithyousry.coursemanagementsystemmvc.repository;

import com.codewithyousry.coursemanagementsystemmvc.entity.Enrollment;
import com.codewithyousry.coursemanagementsystemmvc.entity.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    boolean existsByStudentId(Long studentId);

    boolean existsByCourseId(Long courseId);

    long countByStatus(EnrollmentStatus status);

    long countByCourseId(Long courseId);

    long countByStudentId(Long studentId);

    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);

    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);
}
