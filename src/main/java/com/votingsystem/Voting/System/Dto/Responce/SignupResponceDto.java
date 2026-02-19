package com.votingsystem.Voting.System.Dto.Responce;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponceDto {
    private Long id;
    private String email;
    private String dob;
    private String name;
}
