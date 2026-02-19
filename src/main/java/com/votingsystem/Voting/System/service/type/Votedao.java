package com.votingsystem.Voting.System.service.type;

import com.votingsystem.Voting.System.entity.Candidate;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface Votedao {
    List<Candidate> winnerCandidate();
    void voteCount(String email);
    void voteInitialize();
}
