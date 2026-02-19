package com.votingsystem.Voting.System.Dto.Request;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {
    @Email(message = "Email formet not currect")
    @NotBlank
    @NonNull
    @NotEmpty
    private String email;
    @Column(nullable = false)
    @NotBlank
    @NonNull
    @Size(min = 6, message = "password should be grater then 6 digits")
    private String password;
    @NotBlank
    @NotEmpty
    private String name;
    @Past
    @NotNull(message = "fill your DOB")
    private LocalDate dob;
}
