package com.votingsystem.Voting.System.Dto.Responce;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateResponceDto {
    private Long id;
    private String email;
    private String name;
    private String party;
    private Long totalVote;
    private boolean isveryfied;
}
