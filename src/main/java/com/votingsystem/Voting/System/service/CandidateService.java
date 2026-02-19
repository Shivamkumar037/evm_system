package com.votingsystem.Voting.System.service;

import com.votingsystem.Voting.System.Dto.Request.CandidateRequestDto;
import com.votingsystem.Voting.System.Dto.Responce.CandidateResponceDto;
import com.votingsystem.Voting.System.entity.Candidate;
import com.votingsystem.Voting.System.exception.CandidateExistException;
import com.votingsystem.Voting.System.exception.CandidateNotExistException;
import com.votingsystem.Voting.System.exception.NoCandidate;
import com.votingsystem.Voting.System.reposetory.CandidateRepo;
import com.votingsystem.Voting.System.service.type.CandidateDao; // Agar interface hai
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CandidateService implements CandidateDao {

    private final ModelMapper modelMapper;
    private final CandidateRepo candidateRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public CandidateResponceDto createCandidate(CandidateRequestDto requestDto) {
        if (candidateRepo.findByEmail(requestDto.getEmail().trim()).isPresent()) {
            throw new CandidateExistException("Candidate with this email already exists");
        }

        Candidate candidate = modelMapper.map(requestDto, Candidate.class);
        candidate.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        candidate.setIsverified(false); // Admin verification required
        candidate.setTotalVote(0l);      // Initial votes

        Candidate savedCandidate = candidateRepo.save(candidate);
        return modelMapper.map(savedCandidate, CandidateResponceDto.class);
    }

    @Transactional
    @Override
    public CandidateResponceDto deleteCandidate(String email) {
        Candidate candidate = candidateRepo.findByEmail(email.trim())
                .orElseThrow(() -> new CandidateNotExistException("Candidate not found with email: " + email));

        candidateRepo.delete(candidate);
        return modelMapper.map(candidate, CandidateResponceDto.class);
    }

    @Transactional
    @Override
    public CandidateResponceDto UpdateCandidate(CandidateRequestDto requestDto) {
        Candidate existingCandidate = candidateRepo.findByEmail(requestDto.getEmail().trim())
                .orElseThrow(() -> new CandidateNotExistException("Candidate not found to update"));

        // Password ko purana hi rehne dete hain agar DTO mein password null/empty hai
        String oldPassword = existingCandidate.getPassword();

        modelMapper.map(requestDto, existingCandidate);

        if (requestDto.getPassword() != null && !requestDto.getPassword().isEmpty()) {
            existingCandidate.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        } else {
            existingCandidate.setPassword(oldPassword);
        }

        Candidate updated = candidateRepo.save(existingCandidate);
        return modelMapper.map(updated, CandidateResponceDto.class);
    }

    @Override
    public Candidate findCandidate(String email) {
        return candidateRepo.findByEmail(email.trim())
                .orElseThrow(() -> new CandidateNotExistException("Candidate not found"));
    }

    @Override
    public List<CandidateResponceDto> AllActiveCandidate() {
        // Exception throw nahi karni hai, bas khali list return karni hai
        List<Candidate> activeCandidates = candidateRepo.findAll().stream()
                .filter(Candidate::isIsverified)
                .toList();

        return activeCandidates.stream()
                .map(c -> modelMapper.map(c, CandidateResponceDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CandidateResponceDto> AllInActiveCandidate() {
        return candidateRepo.findAll().stream()
                .filter(c -> !c.isIsverified())
                .map(c -> modelMapper.map(c, CandidateResponceDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void SetActive(String email) {
        Candidate candidate = candidateRepo.findByEmail(email.trim())
                .orElseThrow(() -> new CandidateNotExistException("Candidate not found"));
        candidate.setIsverified(true);
        candidateRepo.save(candidate);
    }

    public void candidateInitialize(){
           candidateRepo.deleteAll();
    }



    private CandidateResponceDto createNotaCandidate() {
        CandidateResponceDto nota = new CandidateResponceDto();
        nota.setTotalVote(0L);
        nota.setName("DRAW / NOTA");
        nota.setParty("N/A");
        return nota;
    }

    public CandidateResponceDto result(){
        List<Candidate> list=candidateRepo.findAll();
        if(list==null|| list.isEmpty()){

            return createNotaCandidate();
        }else if(list.size()==1) {
            return modelMapper.map(list.get(0),CandidateResponceDto.class);
        }else {
         List<Candidate> list1=   list.stream()
                    .sorted((c1, c2) -> Long.compare(c2.getTotalVote(), c1.getTotalVote()))
                    .collect(Collectors.toList());
         if(list1.get(0).getTotalVote()==list1.get(1).getTotalVote()){
             return createNotaCandidate();
         }else {
             return modelMapper.map(list1.get(0),CandidateResponceDto.class);
         }
        }

    }
}