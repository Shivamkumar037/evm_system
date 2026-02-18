package com.votingsystem.Voting.System.reposetory;

import com.votingsystem.Voting.System.entity.ElectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ElectionRepository extends JpaRepository<ElectionStatus,Integer> {
    Optional<ElectionStatus> findByStatus(String statusOfElection);
}
