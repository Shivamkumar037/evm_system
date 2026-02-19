package com.votingsystem.Voting.System.reposetory;

import com.votingsystem.Voting.System.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepo extends JpaRepository<Candidate,Integer> {

    Optional<Candidate> findByEmail(String email);
    @Transactional
    @Modifying
    @Query("update Candidate c set c.totalVote=0l")
    void resetAllVotes();

    List<Candidate> findAllByOrderByTotalVoteDesc();
}
