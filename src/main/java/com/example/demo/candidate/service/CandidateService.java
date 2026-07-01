package com.example.demo.candidate.service;

import com.example.demo.candidate.dto.CandidateRequest;
import com.example.demo.candidate.dto.CandidateResponse;
import com.example.demo.candidate.dto.CandidateStats;
import com.example.demo.candidate.model.Candidate;
import com.example.demo.candidate.model.Status;
import com.example.demo.candidate.repository.CandidateRepository;
import com.example.demo.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;

    @Transactional(readOnly = true)
    public List<CandidateResponse> getAllCandidates() {
        return candidateRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CandidateResponse getCandidateById(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));
        return mapToResponse(candidate);
    }

    @Transactional
    public CandidateResponse createCandidate(CandidateRequest request) {
        Candidate candidate = mapToEntity(request);
        Candidate savedCandidate = candidateRepository.save(candidate);
        return mapToResponse(savedCandidate);
    }

    @Transactional
    public CandidateResponse updateCandidate(Long id, CandidateRequest request) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

        candidate.setFullName(request.getFullName());
        candidate.setAge(request.getAge());
        candidate.setHeight(request.getHeight());
        candidate.setWeight(request.getWeight());
        candidate.setNationality(request.getNationality());
        candidate.setBio(request.getBio());
        candidate.setPhotoUrl(request.getPhotoUrl());
        candidate.setScore(request.getScore());
        candidate.setStatus(request.getStatus());

        Candidate updatedCandidate = candidateRepository.save(candidate);
        return mapToResponse(updatedCandidate);
    }

    @Transactional
    public void deleteCandidate(Long id) {
        if (!candidateRepository.existsById(id)) {
            throw new ResourceNotFoundException("Candidate not found with id: " + id);
        }
        candidateRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<CandidateResponse> searchCandidates(
            String name,
            String nationality,
            Status status,
            Double minHeight,
            Double maxHeight,
            Pageable pageable) {
        return candidateRepository.searchCandidates(name, nationality, status, minHeight, maxHeight, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public CandidateResponse voteCandidate(Long id, Double points) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));
        
        double currentScore = candidate.getScore() != null ? candidate.getScore() : 0.0;
        double addedPoints = points != null ? points : 1.0;
        candidate.setScore(Math.min(100.0, Math.max(0.0, currentScore + addedPoints)));
        
        Candidate savedCandidate = candidateRepository.save(candidate);
        return mapToResponse(savedCandidate);
    }

    @Transactional
    public CandidateResponse updateStatus(Long id, Status status) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));
        
        candidate.setStatus(status);
        Candidate savedCandidate = candidateRepository.save(candidate);
        return mapToResponse(savedCandidate);
    }

    @Transactional(readOnly = true)
    public CandidateStats getStats() {
        return candidateRepository.getStatistics();
    }

    private CandidateResponse mapToResponse(Candidate candidate) {
        return CandidateResponse.builder()
                .id(candidate.getId())
                .fullName(candidate.getFullName())
                .age(candidate.getAge())
                .height(candidate.getHeight())
                .weight(candidate.getWeight())
                .nationality(candidate.getNationality())
                .bio(candidate.getBio())
                .photoUrl(candidate.getPhotoUrl())
                .score(candidate.getScore())
                .status(candidate.getStatus())
                .build();
    }

    private Candidate mapToEntity(CandidateRequest request) {
        return Candidate.builder()
                .fullName(request.getFullName())
                .age(request.getAge())
                .height(request.getHeight())
                .weight(request.getWeight())
                .nationality(request.getNationality())
                .bio(request.getBio())
                .photoUrl(request.getPhotoUrl())
                .score(request.getScore() != null ? request.getScore() : 0.0)
                .status(request.getStatus())
                .build();
    }
}
