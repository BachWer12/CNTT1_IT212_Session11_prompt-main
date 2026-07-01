package com.example.demo.candidate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateStats {
    private long totalCandidates;
    private double averageAge;
    private double averageHeight;
    private double averageWeight;
    private double maxScore;
}
