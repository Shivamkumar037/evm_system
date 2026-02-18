package com.votingsystem.Voting.System.Config;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@AllArgsConstructor
public class SpringConfig {
    private DefaultHandlerrequest defaultHandlerrequest;
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new
                BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {

        return security.authorizeHttpRequests(a -> a
                .requestMatchers("/admin/**").hasAuthority("ROLE_Admin")
                .requestMatchers("/member/**").hasAnyAuthority("ROLE_Admin", "ROLE_Member")
                .requestMatchers("/Voter_Controller/**").hasAuthority("ROLE_Voter")
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
        ).formLogin(form -> form
                .loginPage("/public/")
                .loginProcessingUrl("/authenticateTheUser")
                .successHandler(defaultHandlerrequest)
                .failureUrl("/public/")
                .permitAll()
        ).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))


        .build();
    }
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public MessageSource messageSource(){
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource=new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setBasename("classpath:messages");
        reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
        return reloadableResourceBundleMessageSource;
    }
}
