package com.cintie.marketplace_backend.configurations;

import javax.sql.DataSource;

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
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;

import com.cintie.marketplace_backend.services.UserService;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    private UserService userService;
    private DataSource dataSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> corsConfiguration())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).sessionFixation(fixation -> fixation.newSession()))
            .logout(logout -> logout.logoutSuccessUrl("/auth/status").deleteCookies("JSESSIONID", "remember-me"))
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.deny()))
            .rememberMe(rememberme -> rememberme.rememberMeServices(rememberMeServices()))
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/telegram/**", "/auth/**"))
            .authorizeHttpRequests(req -> req
            .requestMatchers("/api/telegram/**").permitAll()
            .requestMatchers("/auth/**").permitAll()
            .anyRequest().authenticated()
        );
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
        cors.addAllowedOrigin("http://localhost");
        cors.addAllowedOrigin("http://frontend");
        cors.addAllowedOrigin("http://backend");
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("*");
        cors.setAllowCredentials(true);
        cors.setMaxAge(3600L);
        return cors;
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    @Bean
    public PersistentTokenBasedRememberMeServices rememberMeServices(){
        PersistentTokenBasedRememberMeServices rememberMeServices = new PersistentTokenBasedRememberMeServices("sjkgdshjlshglksjgjlsfnjdvnls", userService, persistentTokenRepository());
        rememberMeServices.setCookieName("remember-me");
        //rememberMeServices.setUseSecureCookie(true); // For https
        rememberMeServices.setAlwaysRemember(true);
        rememberMeServices.setTokenValiditySeconds(4809600);
        return rememberMeServices;

    }
}
