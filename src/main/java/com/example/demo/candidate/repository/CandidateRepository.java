package com.example.demo.candidate.repository;

import com.example.demo.candidate.dto.CandidateStats;
import com.example.demo.candidate.model.Candidate;
import com.example.demo.candidate.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    @Query("select new com.example.demo.candidate.dto.CandidateStats(" +
           "count(c), " +
           "coalesce(avg(c.age), 0.0), " +
           "coalesce(avg(c.height), 0.0), " +
           "coalesce(avg(c.weight), 0.0), " +
           "coalesce(max(c.score), 0.0)" +
           ") from Candidate c")
    CandidateStats getStatistics();

    @Query("select c from Candidate c where " +
           "(:name is null or lower(c.fullName) like lower(concat('%', :name, '%'))) and " +
           "(:nationality is null or lower(c.nationality) = lower(:nationality)) and " +
           "(:status is null or c.status = :status) and " +
           "(:minHeight is null or c.height >= :minHeight) and " +
           "(:maxHeight is null or c.height <= :maxHeight)")
    Page<Candidate> searchCandidates(
            @Param("name") String name,
            @Param("nationality") String nationality,
            @Param("status") Status status,
            @Param("minHeight") Double minHeight,
            @Param("maxHeight") Double maxHeight,
            Pageable pageable);
}
