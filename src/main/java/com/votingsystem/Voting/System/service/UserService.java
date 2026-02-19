package com.votingsystem.Voting.System.service;

import com.votingsystem.Voting.System.Dto.Request.SignupRequestDto;
import com.votingsystem.Voting.System.Dto.Responce.SignupResponceDto;
import com.votingsystem.Voting.System.entity.Authority;
import com.votingsystem.Voting.System.entity.User;
import com.votingsystem.Voting.System.entity.type.Role;
import com.votingsystem.Voting.System.exception.InvalidCandidate;
import com.votingsystem.Voting.System.exception.UserFoundException;
import com.votingsystem.Voting.System.exception.UserNotFoundException;
import com.votingsystem.Voting.System.reposetory.UserRepo;
import com.votingsystem.Voting.System.service.type.UserDao;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService implements UserDao {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    @Lazy
    private final EmailOtpService emailOtpService;
    private ModelMapper modelMapper;

    public long getAllUsersCount() {
        return userRepo.count();
    }

    @Transactional
    public SignupResponceDto deleteUser(String email) {
        User user=userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("not found"));
        userRepo.delete(user);
        return modelMapper.map(user,SignupResponceDto.class);
    }

    @Transactional
    @Override
    public SignupResponceDto createUser(SignupRequestDto requestDto) {
        if(userRepo.existsByEmail(requestDto.getEmail().trim())) {
            throw new UserFoundException("User already exists");
        }

        User user = modelMapper.map(requestDto, User.class);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        Authority auth = new Authority();
        // Aapke schema ke mutabiq String set karna
        auth.setRole(Role.ROLE_Voter);
        user.setAuthorities(Set.of(auth));
        user.setIsverified(false);

        User savedUser = userRepo.save(user);
        emailOtpService.otpGenerator(savedUser.getEmail().trim(), savedUser.getName());

        return modelMapper.map(savedUser, SignupResponceDto.class);
    }

    @Transactional
    public SignupResponceDto createMember(SignupRequestDto requestDto) {
        if(userRepo.existsByEmail(requestDto.getEmail().trim())) {
            throw new UserFoundException("User already exists");
        }

        User user = modelMapper.map(requestDto, User.class);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        Authority auth = new Authority();
        auth.setRole(Role.ROLE_Member);
        user.setAuthorities(Set.of(auth));
        user.setIsverified(false);

        User savedUser = userRepo.save(user);
        emailOtpService.otpGenerator(savedUser.getEmail().trim(), savedUser.getName());

        return modelMapper.map(savedUser, SignupResponceDto.class);
    }

    @Transactional
    @Override
    public SignupResponceDto deleteuser(String email) {
        User user = userRepo.findByEmail(email.trim()).orElseThrow(()->new UserNotFoundException("User Not Exist"));
        userRepo.delete(user);
        return modelMapper.map(user, SignupResponceDto.class);
    }

    @Override
    public Optional<SignupResponceDto> getUser(String email) {
        User user = userRepo.findByEmail(email.trim()).orElseThrow(()->new UserNotFoundException("User Not Exist"));
        return Optional.of(modelMapper.map(user, SignupResponceDto.class));
    }

    @Transactional
    @Override
    public SignupResponceDto updateUser(SignupRequestDto requestDto){
        User user = userRepo.findByEmail(requestDto.getEmail().trim()).orElseThrow(() -> new InvalidCandidate("User Not Present"));
        modelMapper.map(requestDto, user);
        userRepo.save(user);
        return modelMapper.map(user, SignupResponceDto.class);
    }

    @Transactional
    public void sendOtpForrget(String email) {
        SignupResponceDto responceDto = getUser(email.trim()).orElseThrow(()->new UserNotFoundException(" User Not Exist"));
        emailOtpService.otpGenerator(responceDto.getEmail().trim(), responceDto.getName());
    }

    public String getDefaultPage(String email) {
        User user = userRepo.findByEmail(email.trim()).orElseThrow(() -> new UserNotFoundException("User missing"));
        String role = user.getAuthorities().iterator().next().getAuthority();

        if (role.equalsIgnoreCase("ROLE_Admin")) return "redirect:/admin/Admin";
        if (role.equalsIgnoreCase("ROLE_Member")) return "redirect:/member/Member";
        if (role.equalsIgnoreCase("ROLE_Voter")) return "redirect:/Voter_Controller/Voter";

        return "redirect:/public/";
    }

    @Transactional
    public void resendOtp(String email) {
        User user = userRepo.findByEmail(email.trim()).orElseThrow(() -> new UserNotFoundException("User not found"));
        emailOtpService.otpGenerator(email.trim(), user.getName());
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email.trim()).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public void  InitializeUser(){
        List<User> list=userRepo.findAll();

        if(list==null|| list.isEmpty()){
            return;
        }else {
            list.forEach(user -> {
                user.setIsvoted(false);
            });
            userRepo.saveAll(list);
        }

    }
}