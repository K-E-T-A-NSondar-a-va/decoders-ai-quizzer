package com.decoder.aiquizzer.repository;

import com.decoder.aiquizzer.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByUserId(String username);

    @Query("SELECT s FROM Submission s WHERE s.submissionDate BETWEEN ?1 and ?2")
    List<Submission> findSubmissionBetweenDateRange(Date start, Date to);
}
