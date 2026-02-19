package com.votingsystem.Voting.System.reposetory;

import com.votingsystem.Voting.System.entity.OtpEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepo extends JpaRepository<OtpEntity,Integer> {
   Optional<OtpEntity> findByEmail(String email) ;


}
