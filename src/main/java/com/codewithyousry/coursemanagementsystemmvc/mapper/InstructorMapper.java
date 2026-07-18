package com.codewithyousry.coursemanagementsystemmvc.mapper;

import com.codewithyousry.coursemanagementsystemmvc.dto.request.InstructorRequest;
import com.codewithyousry.coursemanagementsystemmvc.dto.response.InstructorResponse;
import com.codewithyousry.coursemanagementsystemmvc.entity.Instructor;
import org.springframework.stereotype.Component;

@Component
public class InstructorMapper {

    public Instructor toEntity(InstructorRequest request) {
        return Instructor.builder()
                .name(request.getName())
                .email(request.getEmail())
                .bio(request.getBio())
                .build();
    }

    public void updateEntity(Instructor instructor, InstructorRequest request) {
        instructor.setName(request.getName());
        instructor.setEmail(request.getEmail());
        instructor.setBio(request.getBio());
    }

    public InstructorResponse toResponse(Instructor instructor, long courseCount) {
        return InstructorResponse.builder()
                .id(instructor.getId())
                .name(instructor.getName())
                .email(instructor.getEmail())
                .bio(instructor.getBio())
                .courseCount(courseCount)
                .build();
    }

    public InstructorRequest toRequest(Instructor instructor) {
        return InstructorRequest.builder()
                .id(instructor.getId())
                .name(instructor.getName())
                .email(instructor.getEmail())
                .bio(instructor.getBio())
                .build();
    }
}
