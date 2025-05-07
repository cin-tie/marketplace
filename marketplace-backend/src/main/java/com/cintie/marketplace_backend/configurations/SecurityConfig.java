package com.cintie.marketplace_backend.configurations;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;

import com.cintie.marketplace_backend.filters.ApiKeyAuthFilter;
import com.cintie.marketplace_backend.services.UserService;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    private final UserService userService;
    private final DataSource dataSource;
    private final String apiKey;
    private final String apiKeyHeader;

    public SecurityConfig(
        UserService userService,
        DataSource dataSource,
        @Value("${app.api.key}") String apiKey,
        @Value("${app.api.key-header}") String apiKeyHeader
    ) {
        this.userService = userService;
        this.dataSource = dataSource;
        this.apiKey = apiKey;
        this.apiKeyHeader = apiKeyHeader;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> corsConfiguration())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .logout(logout -> logout.logoutSuccessUrl("/auth/status").deleteCookies("JSESSIONID", "remember-me"))
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.deny()))
            .rememberMe(rememberme -> rememberme.rememberMeServices(rememberMeServices()))
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/telegram/**", "/auth/**"))
            .addFilterBefore(apiKeyAuthFilter(), UsernamePasswordAuthenticationFilter.class) // Изменено здесь
            .authorizeHttpRequests(req -> req
                .requestMatchers("/api/telegram/**").authenticated()
                .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }

    @Bean
    public ApiKeyAuthFilter apiKeyAuthFilter() {
        return new ApiKeyAuthFilter(apiKeyHeader, apiKey);
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
