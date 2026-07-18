package com.codewithyousry.coursemanagementsystemmvc.dto.request;

import com.codewithyousry.coursemanagementsystemmvc.entity.EnrollmentStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class EnrollmentUpdateRequest {

    private Long id;

    @Size(max = 5, message = "Grade must be at most 5 characters")
    private String grade;

    @NotNull(message = "Progress is required")
    @Min(value = 0, message = "Progress must be at least 0")
    @Max(value = 100, message = "Progress must be at most 100")
    private Integer progress;

    @NotNull(message = "Status is required")
    private EnrollmentStatus status;
}
