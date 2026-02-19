package com.votingsystem.Voting.System.controller;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    public String vote(@RequestParam("email") String email, Model model, RedirectAttributes redirectAttributes) {
        // 1. Check if user has already voted
        if (votingService.isUserVoted(email)) {
            // "error" message ko flash attribute mein add karein
            redirectAttributes.addFlashAttribute("error", "You have already cast your vote!");
            // Redirect back to the main voter dashboard
            return "redirect:/Voter_Controller/Voter";
        }

        // 2. Agar vote nahi kiya, tabhi niche ka logic chalega
        List<CandidateResponceDto> list = candidateService.AllActiveCandidate();
        model.addAttribute("email", email);
        model.addAttribute("activeCandidates", list);
        return "votingsheet";
    }
    @GetMapping("/vote")
    public String vote2(@RequestParam("candidate") String candidateEmail, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();

        if (votingService.votetoCandidate(userEmail, candidateEmail)) {
            // Yahan "Voter" ka V capital rakhein jo aapke mapping se match kare
            return "redirect:/Voter_Controller/Voter";
        }

        // Fail hone par dobara list dikhani hogi
        model.addAttribute("errormassage", "Vote failed: Either you already voted or the election is closed.");
        model.addAttribute("activeCandidates", candidateService.AllActiveCandidate());
        return "votingsheet"; // Direct template return karein
    }
}