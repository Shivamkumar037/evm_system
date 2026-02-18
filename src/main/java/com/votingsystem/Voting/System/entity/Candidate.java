package com.votingsystem.Voting.System.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    @Column(unique = true)
    private String email;
    @NotNull
    private String password;
    private String name;
    @NotNull
    @Size(min = 1,max = 20,message = "len can be between 1 to 20 ")
    private String party;
    private Long totalVote=0l;
    private boolean isverified;





}


