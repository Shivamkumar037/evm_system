package com.votingsystem.Voting.System.entity;

import com.votingsystem.Voting.System.entity.type.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ElectionStatus {
    @Id
    private Integer id = 1;
    @Enumerated(EnumType.STRING)
    private Status status = Status.Rest;
}