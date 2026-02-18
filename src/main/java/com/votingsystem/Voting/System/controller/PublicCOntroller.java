package com.votingsystem.Voting.System.controller;

import com.votingsystem.Voting.System.Dto.Request.SignupRequestDto;
import com.votingsystem.Voting.System.Dto.Responce.CandidateResponceDto;
import com.votingsystem.Voting.System.Dto.Responce.SignupResponceDto;
import com.votingsystem.Voting.System.entity.OtpEntity;
import com.votingsystem.Voting.System.entity.type.Status;
import com.votingsystem.Voting.System.service.CandidateService;
import com.votingsystem.Voting.System.service.ElectionService;
import com.votingsystem.Voting.System.service.EmailOtpService;
import com.votingsystem.Voting.System.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/public")
@AllArgsConstructor
public class PublicCOntroller {
    private SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    private  CandidateService candidateService;
    private UserService userService;
    private EmailOtpService emailOtpService;
    private   ElectionService electionService;

    @GetMapping("/allActivecandidate")
    public  String getActive(Model model){
        try {
            List<CandidateResponceDto> candidateRequestDtos = candidateService.AllActiveCandidate();
            if(candidateRequestDtos==null){
                model.addAttribute("startVoting",false);
            }else {
                model.addAttribute("activeCandidates", candidateRequestDtos);
            }
            return "activeCandidate";
        } catch (Exception e) {
            return "activeCandidate";
        }
    }





    @GetMapping("/")
    public String getHome(Model model){
        String status=electionService.Status();
        List<CandidateResponceDto> winners = new ArrayList<>();
        winners.add(candidateService.result());
        model.addAttribute("Candidate_winner", winners);

        model.addAttribute("activeCandidates", candidateService.AllActiveCandidate());
        model.addAttribute("status",status);
        return "home";
    }
    @GetMapping("/forgetpassword")
    public String forget(){
        return "recovery" ;
    }

    @PostMapping("/forget")
    public String forgetpaassword(@RequestParam("email") String email, Model model){
        if(email==null){
            model.addAttribute("emailnull","Enter Email");
            return "recovery" ;
        }else {
            try{
                userService.sendOtpForrget(email);
                model.addAttribute("email",email);
                return "Otp_verification" ;
            } catch (Exception e) {
                model.addAttribute("notexist","User Not Exist");
                model.addAttribute("email",email);
                return "recovery" ;
            }
        }

    }

    @GetMapping("/signup_form")
    public String getform(Model model){
        if(!model.containsAttribute("signup_user")){
            model.addAttribute("signup_user",new SignupRequestDto());
        }
        return "signup_page";
    }

    @PostMapping("/signup_data")
    public String signup(@Valid @ModelAttribute("signup_user") SignupRequestDto signupRequestDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors() || signupRequestDto.getDob() == null || Period.between(signupRequestDto.getDob(), LocalDate.now()).getYears() < 18) {
            if ( signupRequestDto.getDob() == null ||Period.between(signupRequestDto.getDob(), LocalDate.now()).getYears() < 18) {
                bindingResult.rejectValue("dob", "signuprequest.age.error");
            }
            return "signup_page";
        }
        SignupResponceDto user = userService.createUser(signupRequestDto);

        if (!model.containsAttribute("email")) {
            model.addAttribute("email", user.getEmail());
        }
        return "Otp_verification";
    }





    @GetMapping("/login_page")
    public String loginform(){
        return "login_form";
    }




    @PostMapping("/otp_verification_process")
    public String otpverification(@RequestParam("email") String email,
                                  @RequestParam("otp") String otp,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  Model model) {

        if (otp == null || email == null) {
            model.addAttribute("error", "Otp is Invalid");
            if (!model.containsAttribute("email")) {
                model.addAttribute("email", email);
            }
            return "Otp_verification";
        }

        OtpEntity otp1 = new OtpEntity();
        otp1.setEmail(email);
        otp1.setOtp(otp);

        if (emailOtpService.verifyOtp(otp1.getEmail(),otp1.getOtp())) {
            // 1. User ko DB se load karein (Authorities ke saath)
            var user = userService.findByEmail(email); // Ensure ye UserDetails return kare ya User object

            // 2. Manual Authentication banayein
            Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            // 3. Security Context mein set karein
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            // 4. SABSE ZAROORI: Session mein context save karein (Persistence)
            securityContextRepository.saveContext(context, request, response);

            // 5. Ab redirect karein
            return userService.getDefaultPage(email);
        } else {
            model.addAttribute("error", "Otp is Invalid");
            if (!model.containsAttribute("email")) {
                model.addAttribute("email",email);
            }
            return "Otp_verification";
        }
    }


    @GetMapping("/resend_otp")
    public String resendOtp(@RequestParam("email") String email, Model model) {
        try {
            userService.resendOtp(email);
            model.addAttribute("email", email);
            model.addAttribute("message", "A new OTP has been sent to your email.");
            return "Otp_verification";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to resend OTP.");
            if (!model.containsAttribute("email")) {
                model.addAttribute("email", email);
            }
            return "Otp_verification";
        }
    }

}
