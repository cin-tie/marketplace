package com.cintie.marketplace_backend.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import com.cintie.marketplace_backend.services.UserService;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    private UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.cors(cors->corsConfiguration())
            .authorizeHttpRequests(req->req.requestMatchers("/auth/**").permitAll().anyRequest().authenticated())
            .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).sessionFixation(fixation-> fixation.newSession()))
            .logout(logout->logout.logoutSuccessUrl("/auth/status").deleteCookies("JSESSIONID"))
            .headers(headers->headers.frameOptions(frameOptions->frameOptions.deny()));
            
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public CorsConfiguration corsConfiguration(){
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedOrigin("http://localhost:3000");
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("GET");
        cors.addAllowedMethod("POST");
        cors.addAllowedMethod("PUT");
        cors.addAllowedMethod("DELETE");
        cors.addAllowedMethod("OPTIONS");
        cors.setAllowCredentials(true);
        cors.addAllowedOriginPattern("/**");
        cors.setMaxAge(3600L);
        return cors;
    }
}
