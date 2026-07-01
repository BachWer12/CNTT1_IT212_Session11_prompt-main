package com.example.demo.candidate.dto;

import com.example.demo.candidate.model.Status;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Candidate must be at least 18 years old")
    @Max(value = 60, message = "Candidate cannot be older than 60 years old")
    private Integer age;

    @NotNull(message = "Height is required")
    @DecimalMin(value = "150.0", message = "Height must be at least 150.0 cm")
    private Double height;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "45.0", message = "Weight must be at least 45.0 kg")
    private Double weight;

    @NotBlank(message = "Nationality is required")
    @Size(max = 50, message = "Nationality cannot exceed 50 characters")
    private String nationality;

    @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
    private String bio;

    private String photoUrl;

    @Min(value = 0, message = "Score cannot be negative")
    @Max(value = 100, message = "Score cannot exceed 100")
    private Double score;

    @NotNull(message = "Status is required")
    private Status status;
}
