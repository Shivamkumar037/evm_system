package com.votingsystem.Voting.System.service.type;

import com.votingsystem.Voting.System.Dto.Request.CandidateRequestDto;
import com.votingsystem.Voting.System.Dto.Responce.CandidateResponceDto;
import com.votingsystem.Voting.System.entity.Candidate;

import java.util.List;

public interface CandidateDao {
    CandidateResponceDto createCandidate(CandidateRequestDto requestDto);
    CandidateResponceDto deleteCandidate(String email);
    CandidateResponceDto UpdateCandidate(CandidateRequestDto candidate);
    Candidate findCandidate(String email);
    List< CandidateResponceDto> AllActiveCandidate();
    List< CandidateResponceDto> AllInActiveCandidate();
    void SetActive(String email);

}
