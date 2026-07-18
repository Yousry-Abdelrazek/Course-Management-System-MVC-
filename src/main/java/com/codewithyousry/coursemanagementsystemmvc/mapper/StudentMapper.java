package com.codewithyousry.coursemanagementsystemmvc.mapper;

import com.codewithyousry.coursemanagementsystemmvc.dto.request.StudentRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.response.StudentResponse;
import com.codewithyousry.coursemanagementsystemmvc.entity.Student;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class StudentMapper {

    public Student toEntity(StudentRequest request) {
        return Student.builder()
                .name(request.getName())
                .email(request.getEmail())
                .joinedOn(request.getJoinedOn() != null ? request.getJoinedOn() : LocalDate.now())
                .build();
    }

    public void updateEntity(Student student, StudentRequest request) {
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        if (request.getJoinedOn() != null) {
            student.setJoinedOn(request.getJoinedOn());
        }
    }

    public StudentResponse toResponse(Student student, long enrollmentCount) {
        return StudentResponse.builder()
                .id(student.getId())
                .name(student.getName())
                .email(student.getEmail())
                .joinedOn(student.getJoinedOn())
                .enrollmentCount(enrollmentCount)
                .build();
    }

    public StudentRequest toRequest(Student student) {
        return StudentRequest.builder()
                .id(student.getId())
                .name(student.getName())
                .email(student.getEmail())
                .joinedOn(student.getJoinedOn())
                .build();
    }
}
