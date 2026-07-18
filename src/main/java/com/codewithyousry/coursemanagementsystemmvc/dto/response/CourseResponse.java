package com.codewithyousry.coursemanagementsystemmvc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime registrationStartTime;
    private LocalDateTime registrationEndTime;
    private boolean deleted;
    private Long instructorId;
    private String instructorName;
    private long enrollmentCount;
    private boolean registrationOpen;
}
