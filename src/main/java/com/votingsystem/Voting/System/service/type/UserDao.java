package com.votingsystem.Voting.System.service.type;

import com.votingsystem.Voting.System.Dto.Request.SignupRequestDto;
import com.votingsystem.Voting.System.Dto.Responce.SignupResponceDto;
import com.votingsystem.Voting.System.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserDao {
    long getAllUsersCount();

    SignupResponceDto createUser(SignupRequestDto user);
    SignupResponceDto  deleteuser(String email);

    @Transactional
    SignupResponceDto deleteUser(String email);

    Optional< SignupResponceDto > getUser(String email);
     SignupResponceDto updateUser(SignupRequestDto requestDto);

}
