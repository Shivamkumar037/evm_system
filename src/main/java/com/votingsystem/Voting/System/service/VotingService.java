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
    // In-Memory Flag (Resets on server restart)
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
        // 1. Database se check karein ki voting sach mein START hai ya nahi
        ElectionStatus electionStatus = electionRepo.findById(1).orElse(null);
        if (electionStatus == null || electionStatus.getStatus() != Status.Start) {
            return false; // Election start nahi hua ya rest/stop par hai
        }

        // 2. User dhundhein aur check karein ki wo pehle vote toh nahi kar chuka
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isIsvoted()) {
            return false; // User already voted
        }

        // 3. Candidate dhundhein
        Candidate candidate = candidateRepo.findByEmail(candidateEmail)
                .orElseThrow(() -> new CandidateNotExistException("Invalid Candidate"));

        // 4. Vote count badhayein aur user ka status update karein
        candidate.setTotalVote(candidate.getTotalVote() + 1);
        user.setIsvoted(true);

        // 5. Save changes
        candidateRepo.save(candidate);
        userRepo.save(user);

        return true;
    }
}