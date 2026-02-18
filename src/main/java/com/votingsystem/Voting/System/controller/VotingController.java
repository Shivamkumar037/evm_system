package com.votingsystem.Voting.System.controller;

import com.votingsystem.Voting.System.Dto.Responce.CandidateResponceDto;
import com.votingsystem.Voting.System.entity.type.Status;
import com.votingsystem.Voting.System.service.CandidateService;
import com.votingsystem.Voting.System.service.ElectionService;
import com.votingsystem.Voting.System.service.VotingService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/Voter_Controller")
@AllArgsConstructor
public class VotingController {
    private ElectionService electionService;

    private final CandidateService candidateService;
    private final VotingService votingService;

    @GetMapping("/Voter")
    public String voter(Model model) {
        // 1. Authenticated email nikalna
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        model.addAttribute("email", email);

        // 2. Status check karna (Service se status mangiye)
        String status = electionService.Status().toLowerCase();
        model.addAttribute("status", status);

        // 3. Logic based on Status
        if ("stop".equals(status)) {
            // Hamesha List bhejein, bhale hi ek winner ho
            List<CandidateResponceDto> result = new ArrayList<>();
            result.add(candidateService.result());
            model.addAttribute("winner", result);
        }
        else if ("start".equals(status)) {
            // Active candidates ki list
            model.addAttribute("activeCandidates", candidateService.AllActiveCandidate());
            // Votesheet ka dynamic link
            model.addAttribute("votesheet", "/Voter_Controller/votesheet_page?email=" + email);
        }

        return "Voter"; // Aapka template name "Voter.html" hona chahiye
    }

    @GetMapping("/votesheet_page")
    public String vote(@RequestParam("email") String email, Model model) {
        if (votingService.isUserVoted(email)) {
            model.addAttribute("voted", "Alredy Voted");
            return "redirect:/Voter_Controller/voter";
        }
        List<CandidateResponceDto> list = candidateService.AllActiveCandidate();
        model.addAttribute("email", email);
        model.addAttribute("activeCandidates", list);
        return "votingsheet";
    }

    @GetMapping("/vote")
    public String vote2(@RequestParam("candidate") String candidateEmail, Model model) {
        // Security Context se authenticated user ki email lein
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();

        if (votingService.votetoCandidate(userEmail, candidateEmail)) {
            // Vote success hone par dashboard par bhejien jahan "Voted" dikhega
            return "redirect:/Voter_Controller/Voter";
        }

        // Agar fail hua toh error message ke saath wapas ballot page par
        model.addAttribute("errormassage", "Vote failed: Either you already voted or the election is closed.");
        // Ensure karein ki model mein candidates ki list wapas bheji jaye warna page empty dikhega
        model.addAttribute("activeCandidates", candidateService.AllActiveCandidate());
        return "votingsheet";
    }
}