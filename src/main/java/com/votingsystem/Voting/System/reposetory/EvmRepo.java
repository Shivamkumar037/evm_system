package com.votingsystem.Voting.System.reposetory;

import com.votingsystem.Voting.System.entity.Evm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvmRepo extends JpaRepository<Evm,Integer> {

}
