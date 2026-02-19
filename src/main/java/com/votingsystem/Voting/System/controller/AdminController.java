package com.votingsystem.Voting.System.controller;

import com.votingsystem.Voting.System.Dto.Request.CandidateRequestDto;
import com.votingsystem.Voting.System.Dto.Request.SignupRequestDto;
import com.votingsystem.Voting.System.Dto.Responce.CandidateResponceDto;
import com.votingsystem.Voting.System.Dto.Responce.SignupResponceDto;
import com.votingsystem.Voting.System.exception.CandidateExistException;
import com.votingsystem.Voting.System.service.CandidateService;
import com.votingsystem.Voting.System.service.ElectionService;
import com.votingsystem.Voting.System.service.UserService;
import com.votingsystem.Voting.System.service.VotingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final ElectionService electionService;
    private final CandidateService candidateService;
    private final UserService userService;
    private final VotingService votingService;

    // 1. Create Member Form Load
    @GetMapping("/create_member")
    public String create(Model model) {
        // model.addAttribute("", new SignupRequestDto()); <-- Ye galat tha (Empty key)
        if (!model.containsAttribute("signup_user")) {
            model.addAttribute("signup_user", new SignupRequestDto());
        }
        return "signupmemberr";
    }

    // 2. Signup Member Process
    @PostMapping("/signup_MemberData")
    public String signup(@Valid @ModelAttribute("signup_user") SignupRequestDto signupRequestDto,
                         BindingResult bindingResult, Model model) {

        // Age Validation Logic
        if (signupRequestDto.getDob() != null) {
            int age = Period.between(signupRequestDto.getDob(), LocalDate.now()).getYears();
            if (age < 18) {
                bindingResult.rejectValue("dob", "error.dob", "Age must be 18 or older");
            }
        }

        // AGAR ERROR HAI TO REDIRECT NA KAREIN (Warna error message gayab ho jayenge)
        if (bindingResult.hasErrors()) {
            return "signupmemberr";
        }

        try {
            userService.createMember(signupRequestDto);
            return "redirect:/admin/Admin";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Member already exists with this email!");
            return "signupmemberr";
        }
    }

    // 3. Admin Dashboard
    @GetMapping("/Admin")
    public String dashboard(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String status = electionService.Status();

        model.addAttribute("status", status);
        model.addAttribute("email", email);
        model.addAttribute("activeCandidates", candidateService.AllActiveCandidate());
        model.addAttribute("InactiveCandidates", candidateService.AllInActiveCandidate());
        model.addAttribute("totalUsers", userService.getAllUsersCount());

        return "Admin"; // Make sure file is Admin.html or admin.html (Case sensitive on Render)
    }

    // 4. Status Change Logic
    @GetMapping("/statuschange")
    public String statuschange(Model model) {
        String status = electionService.StatusChange();

        List<CandidateResponceDto> winners = new ArrayList<>();
        CandidateResponceDto winner = candidateService.result();
        if (winner != null) {
            winners.add(winner);
        }

        model.addAttribute("Candidate_winner", winners);
        model.addAttribute("status", status);
        model.addAttribute("activeCandidates", candidateService.AllActiveCandidate());
        model.addAttribute("InactiveCandidates", candidateService.AllInActiveCandidate());
        model.addAttribute("totalUsers", userService.getAllUsersCount());
        model.addAttribute("email", SecurityContextHolder.getContext().getAuthentication().getName());

        return "Admin";
    }

    @GetMapping("/allActivecandidate")
    public String getActive(Model model) {
        model.addAttribute("activeCandidates", candidateService.AllActiveCandidate());
        return "activeCandidate";
    }

    @GetMapping("/allInActivecandidate")
    public String getInActive(Model model) {
        model.addAttribute("inactiveCandidates", candidateService.AllInActiveCandidate());
        return "InActiveCandidates";
    }

    @GetMapping("/activate")
    public String activate(@RequestParam("email") String email) {
        candidateService.SetActive(email);
        return "redirect:/admin/allInActivecandidate";
    }

    @GetMapping("/delete-user")
    public String deleteUser(@RequestParam("email2") String email) {
        candidateService.deleteCandidate(email);
        return "redirect:/admin/allInActivecandidate";
    }

    @GetMapping("/signup_candidate")
    public String signup_form(Model model) {
        if (!model.containsAttribute("candidate_signup")) {
            model.addAttribute("candidate_signup", new CandidateRequestDto());
        }
        return "create_candidateBymember";
    }

    @PostMapping("/signup_toadmin")
    public String signupCandidate(@Valid @ModelAttribute("candidate_signup") CandidateRequestDto dto,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "create_candidateBymember";
        }
        try {
            candidateService.createCandidate(dto);
            return "redirect:/admin/Admin";
        } catch (CandidateExistException e) {
            result.rejectValue("email", "error.email", "Candidate already exists");
            return "create_candidateBymember";
        }
    }
}