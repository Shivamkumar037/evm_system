package com.votingsystem.Voting.System.service;

import com.votingsystem.Voting.System.entity.Candidate;
import com.votingsystem.Voting.System.entity.ElectionStatus;
import com.votingsystem.Voting.System.entity.User;
import com.votingsystem.Voting.System.entity.type.Status;
import com.votingsystem.Voting.System.exception.*;
import com.votingsystem.Voting.System.reposetory.CandidateRepo;
import com.votingsystem.Voting.System.reposetory.ElectionRepository;
import com.votingsystem.Voting.System.reposetory.UserRepo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VotingService {
    private final UserRepo userRepo;
    private final CandidateRepo candidateRepo;
    private ElectionRepository electionRepo;
    
    private static boolean isVotingStarted = false;

    public void startVoting() { isVotingStarted = true; }
    public void stopVoting() { isVotingStarted = false; }
    public boolean isVotingActive() { return isVotingStarted; }

    public List<Candidate> getResults() {
        return candidateRepo.findAll().stream()
                .sorted(Comparator.comparingLong(Candidate::getTotalVote).reversed())
                .collect(Collectors.toList());
    }

    public boolean isUserVoted(String email) {
        return userRepo.findByEmail(email).map(User::isIsvoted).orElse(false);
    }


    @Transactional
    public boolean votetoCandidate(String userEmail, String candidateEmail) {
        // databse me check krke btayega ki user hai db me ya nahi
        ElectionStatus electionStatus = electionRepo.findById(1).orElse(null);
        if (electionStatus == null || electionStatus.getStatus() != Status.Start) {
            return false; // Election start nahi hua ya rest/stop par hai
        }

        // logic hai dekhne ka ki user ne vote phle kiya hai ya nhi ager kiya hoga to vote nhi kr skta
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isIsvoted()) {
            return false; // user ne vote kr liya hai 
        }

        
        Candidate candidate = candidateRepo.findByEmail(candidateEmail)
                .orElseThrow(() -> new CandidateNotExistException("Invalid Candidate"));

        candidate.setTotalVote(candidate.getTotalVote() + 1);
        user.setIsvoted(true);

        candidateRepo.save(candidate);
        userRepo.save(user);

        return true;
    }
}
