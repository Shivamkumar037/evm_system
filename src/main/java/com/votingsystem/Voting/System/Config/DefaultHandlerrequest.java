package com.votingsystem.Voting.System.Config;

import com.votingsystem.Voting.System.entity.type.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class DefaultHandlerrequest implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Set<String> roles= AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_Admin")) {
            response.sendRedirect("/admin/Admin");
        } else if (roles.contains("ROLE_Member")) {
            response.sendRedirect("/member/Member");
        } else if (roles.contains("ROLE_Voter")) {
            response.sendRedirect("/Voter_Controller/Voter");
        } else {
            response.sendRedirect("/public/");
        }
    }
}
