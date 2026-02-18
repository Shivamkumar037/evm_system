package com.votingsystem.Voting.System.service;

import com.votingsystem.Voting.System.entity.ElectionStatus;
import com.votingsystem.Voting.System.entity.type.Status;
import com.votingsystem.Voting.System.reposetory.ElectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ElectionService {

    private UserService userService;

    @Autowired
    private ElectionRepository electionRepo;

    @Autowired
    private CandidateService candidateService;

    @Transactional
    public String StatusChange() {
        ElectionStatus electionStatus = electionRepo.findById(1).orElseGet(() -> {
            ElectionStatus newStatus = new ElectionStatus();
            newStatus.setId(1);
            newStatus.setStatus(Status.Rest);
            return electionRepo.save(newStatus);
        });

        Status current = electionStatus.getStatus();

        if (current == Status.Stop) {
            // RESET LOGIC YAHAN HONA CHAHIYE: Results dikhne ke baad jab system rest pe jaye
            candidateService.candidateInitialize(); // Ab votes 0 honge
            userService.InitializeUser();
            electionStatus.setStatus(Status.Rest);

        } else if (current == Status.Rest) {
            // Election Start hone par kuch reset nahi karna
            electionStatus.setStatus(Status.Start);

        } else {
            // START se STOP (Yahan results calculate honge, reset nahi!)
            electionStatus.setStatus(Status.Stop);
        }

        electionRepo.save(electionStatus);
        return electionStatus.getStatus().name().toLowerCase();
    }

    public String Status() {
        ElectionStatus electionStatus = electionRepo.findById(1).orElse(null);
        if (electionStatus == null) {
            electionStatus = new ElectionStatus();
            electionStatus.setStatus(Status.Rest);
            electionRepo.save(electionStatus);
        }
        return electionStatus.getStatus().name().toLowerCase();
    }
}