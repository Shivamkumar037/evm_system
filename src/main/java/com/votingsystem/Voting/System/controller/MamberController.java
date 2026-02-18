package com.votingsystem.Voting.System.controller;

import com.votingsystem.Voting.System.Dto.Request.CandidateRequestDto;
import com.votingsystem.Voting.System.Dto.Responce.CandidateResponceDto;
import com.votingsystem.Voting.System.entity.type.Status;
import com.votingsystem.Voting.System.exception.CandidateExistException;
import com.votingsystem.Voting.System.exception.CandidateNotExistException;
import com.votingsystem.Voting.System.service.CandidateService;
import com.votingsystem.Voting.System.service.ElectionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/member")
public class MamberController {

    private  CandidateService candidateService;
    private ElectionService electionService;

    @GetMapping("/Member")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        model.addAttribute("email", email);

        // Status ko lowercase mein lein taaki comparison mein galti na ho
        String status = electionService.Status().toLowerCase();
        model.addAttribute("status", status);

        if ("stop".equals(status)) {
            // Hamesha List bhejein taaki HTML loop na phate
            List<CandidateResponceDto> winners = new ArrayList<>();
            winners.add(candidateService.result());
            // Controller mein ensure karein:
            model.addAttribute("Candidate_winner",winners);

            model.addAttribute("Candidate_winner", winners);

        } else if ("start".equals(status)) {
            model.addAttribute("activeCandidates", candidateService.AllActiveCandidate());
            // In links ki ab HTML mein direct @{...} use kar sakte hain
        }

        return "Member";
    }
    @GetMapping("/memberallInActivecandidate")
    public  String getActive(Model model){
        try {
            List<CandidateResponceDto> candidateRequestDtos = candidateService.AllInActiveCandidate();
                model.addAttribute("activeInCandidates", candidateRequestDtos);


            return "memberactivepages";
        } catch (Exception e) {
            return "memberactivepages";
        }
    }

    @GetMapping("/signup_candidate")
    public String signup_form(Model model) {
        if (!model.containsAttribute("candidate_signup")) {
            model.addAttribute("candidate_signup", new CandidateRequestDto());
        }
        return "candidate_signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("candidate_signup") CandidateRequestDto dto, BindingResult result) {
        if (result.hasErrors()) return "candidate_signup";
        try {
            candidateService.createCandidate(dto);
            return "redirect:/member/Member";
        } catch (CandidateExistException e) {
            result.rejectValue("email", "error.email", "Candidate already exists");
            return "candidate_signup";
        }
    }

    @GetMapping("/update_candidate")
    public String update_form(@RequestParam("email") String email, Model model) {
        CandidateRequestDto dto = new CandidateRequestDto();
        dto.setEmail(email);
        model.addAttribute("candidate_update", dto);
        return "update_candidate";
    }

    @PostMapping("/candidate_update_data")
    public String update(@Valid @ModelAttribute("candidate_update") CandidateRequestDto dto, BindingResult result) {
        try {
            candidateService.UpdateCandidate(dto);
            return "redirect:/member/Member";
        } catch (CandidateNotExistException e) {
            return "update_candidate";
        }
    }
}