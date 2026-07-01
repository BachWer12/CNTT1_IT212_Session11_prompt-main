package com.example.demo.candidate.dto;

import com.example.demo.candidate.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateResponse {
    private Long id;
    private String fullName;
    private Integer age;
    private Double height;
    private Double weight;
    private String nationality;
    private String bio;
    private String photoUrl;
    private Double score;
    private Status status;
}
