package com.codewithyousry.coursemanagementsystemmvc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorResponse {
    private Long id;
    private String name;
    private String email;
    private String bio;
    private long courseCount;
}
