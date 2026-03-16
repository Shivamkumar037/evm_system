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
       
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        model.addAttribute("email", email);

        String status = electionService.Status().toLowerCase();
        model.addAttribute("status", status);

 
        if ("stop".equals(status)) {
            
            List<CandidateResponceDto> result = new ArrayList<>();
            result.add(candidateService.result());
            model.addAttribute("winner", result);
        }
        else if ("start".equals(status)) {

            model.addAttribute("activeCandidates", candidateService.AllActiveCandidate());
          
            model.addAttribute("votesheet", "/Voter_Controller/votesheet_page?email=" + email);
        }

        return "Voter"; 
    }

    @GetMapping("/votesheet_page")
    public String vote(@RequestParam("email") String email, Model model, RedirectAttributes redirectAttributes) {
        if (votingService.isUserVoted(email)) {
            redirectAttributes.addFlashAttribute("error", "You have already cast your vote!");
            return "redirect:/Voter_Controller/Voter";
        }

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
            return "redirect:/Voter_Controller/Voter";
        }

        model.addAttribute("errormassage", "Vote failed: Either you already voted or the election is closed.");
        model.addAttribute("activeCandidates", candidateService.AllActiveCandidate());
        return "votingsheet";
    }
}
