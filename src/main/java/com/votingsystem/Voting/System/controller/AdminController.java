package com.votingsystem.Voting.System.controller;

import com.votingsystem.Voting.System.Dto.Request.CandidateRequestDto;
import com.votingsystem.Voting.System.Dto.Request.SignupRequestDto;
import com.votingsystem.Voting.System.Dto.Responce.CandidateResponceDto;
import com.votingsystem.Voting.System.Dto.Responce.SignupResponceDto;
import com.votingsystem.Voting.System.entity.type.Status;
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

    @GetMapping("/create_member")
    public String create(Model model){
        model.addAttribute("",new SignupRequestDto());
        if(!model.containsAttribute("signup_user")){
            model.addAttribute("signup_user",new SignupRequestDto());
        }
        return "signupmemberr";
    }
    @PostMapping("/signup_MemberData")
    public String signup(@Valid @ModelAttribute("signup_user") SignupRequestDto signupRequestDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors() || signupRequestDto.getDob() == null || Period.between(signupRequestDto.getDob(), LocalDate.now()).getYears() < 18) {
            if ( signupRequestDto.getDob() == null ||Period.between(signupRequestDto.getDob(), LocalDate.now()).getYears() < 18) {
                bindingResult.rejectValue("dob", "signuprequest.age.error");
            }
            return "signupmemberr";
        }
        try{
            SignupResponceDto user = userService.createMember(signupRequestDto);
        } catch (Exception e) {

                model.addAttribute("member exist","member Alredy Have");
            }


        if (!model.containsAttribute("email")) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            model.addAttribute("email", email);
        }
        return "redirect:/admin/Admin";    }

//    @GetMapping("/Admin")
//    public String dashboard(Model model) {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//String status=electionService.StatusChange();
//        model.addAttribute("status", status);
//        model.addAttribute("status_changeBtn", "/admin/statuschange");
//        model.addAttribute("email", email);
//        model.addAttribute("activeCandidates", candidateService.AllActiveCandidate());
//        model.addAttribute("InactiveCandidates", candidateService.AllInActiveCandidate());
//        model.addAttribute("totalUsers", userService.getAllUsersCount());
//        model.addAttribute("createMember", "/admin/create_member");
//
//        return "Admin";
//    }
//@GetMapping("/statuschange")
//public String statuschange(Model model){
//    String status=electionService.StatusChange();
//    String email = SecurityContextHolder.getContext().getAuthentication().getName();
//    model.addAttribute("Candidate_winner",candidateService.result());
//    model.addAttribute("email", email);
//    model.addAttribute("status",status);
//    model.addAttribute("activeCandidates", "/admin/allActivecandidate");
//    model.addAttribute("totalUsers", userService.getAllUsersCount());
//    model.addAttribute("inactiveCandidates", "/admin/allInActivecandidate");
//    return "Admin";
//
//}

    @GetMapping("/Admin")
    public String dashboard(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        // Status Change nahi, sirf current status mangiye yahan dashboard load karte waqt
        String status = electionService.Status();

        model.addAttribute("status", status);
        model.addAttribute("email", email);
        // Hamesha actual List bhejiye
        model.addAttribute("activeCandidates", candidateService.AllActiveCandidate());
        model.addAttribute("InactiveCandidates", candidateService.AllInActiveCandidate());
        model.addAttribute("totalUsers", userService.getAllUsersCount());

        return "Admin";
    }

    @GetMapping("/statuschange")
    public String statuschange(Model model) {
        String status = electionService.StatusChange();

        // Hamesha List bhejein taaki HTML me .isEmpty() aur th:each na phate
        List<CandidateResponceDto> winners = new ArrayList<>();
        winners.add(candidateService.result());
        // Controller mein ensure karein:
        model.addAttribute("Candidate_winner",winners);

        model.addAttribute("status", status);
        model.addAttribute("activeCandidates", candidateService.AllActiveCandidate());
        model.addAttribute("InactiveCandidates", candidateService.AllInActiveCandidate());
        model.addAttribute("totalUsers", userService.getAllUsersCount());
        model.addAttribute("email", SecurityContextHolder.getContext().getAuthentication().getName());

        return "Admin";
    }


    @GetMapping("/allActivecandidate")
    public String getActive(Model model) {
        List<CandidateResponceDto> list = candidateService.AllActiveCandidate();
        model.addAttribute("activeCandidates", list);
        return "activeCandidate";
    }


    @GetMapping("/activate")
    public String activate(@RequestParam("email") String email) {
        try {
            candidateService.SetActive(email);
        } catch (Exception e) {

        }
        return "redirect:/admin/allInActivecandidate";
    }

    @GetMapping("/allInActivecandidate")
    public String getInActive(Model model) {
        List<CandidateResponceDto> list = candidateService.AllInActiveCandidate();
        model.addAttribute("inactiveCandidates", list);
        return "InActiveCandidates";
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
    public String signup(@Valid @ModelAttribute("candidate_signup") CandidateRequestDto dto, BindingResult result) {
        if (result.hasErrors())  return "create_candidateBymember";
        try {
            candidateService.createCandidate(dto);
            return "redirect:/admin/Admin";
        } catch (CandidateExistException e) {
            result.rejectValue("email", "error.email", "Candidate already exists");
            return "create_candidateBymember";
        }
    }

}