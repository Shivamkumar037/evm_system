package com.votingsystem.Voting.System.entity;

import com.votingsystem.Voting.System.entity.type.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Authority implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    private Role role=Role.ROLE_Admin;



    @Override
    public @Nullable String getAuthority() {
        return this.role.name();
    }
}
