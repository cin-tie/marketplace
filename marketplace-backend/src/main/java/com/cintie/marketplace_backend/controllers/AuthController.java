package com.cintie.marketplace_backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.cintie.marketplace_backend.entities.UserEntity;
import com.cintie.marketplace_backend.entities.UserEntity.UserRole;
import com.cintie.marketplace_backend.exceptions.ValidationException;
import com.cintie.marketplace_backend.repositories.UserRepository;
import com.cintie.marketplace_backend.services.EmailService;
import com.cintie.marketplace_backend.services.UserService;
import com.cintie.marketplace_backend.utils.TokenUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;
    private UserRepository userRepository;
    private UserService userService;
    private EmailService emailService;
    private TokenUtils tokenUtils;

    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();

    @GetMapping("/status")
    public ResponseEntity<StatusResp> status(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok().body(new StatusResp(false));
        }
        return ResponseEntity.ok().body(new StatusResp(true));
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResp> signup(@RequestBody SignUpReq signUpReq, HttpServletRequest request, HttpServletResponse response) {
        try{
            signUpReq.validate();
        } catch(ValidationException e){
            return ResponseEntity.ok().body(new SignUpResp(false, new String[] {e.getMessage()}));
        }

        UserEntity userEntity = UserEntity.builder().username(signUpReq.username)
                                                    .password(signUpReq.password)
                                                    .role(UserRole.ROLE_USER)
                                                    .email(signUpReq.email)
                                                    .telegram(signUpReq.telegram)
                                                    .enabled(true)
                                                    .emailVerified(false)
                                                    .emailVerificationToken(tokenUtils.generateTokenWithTimestamp())
                                                    .telegramVerified(false)
                                                    .telegramId(null)
                                                    .createdAt(System.currentTimeMillis())
                                                    .build();
        try {
            userEntity = userService.createUser(userEntity);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new SignUpResp(false, new String[] {e.getMessage()}));
        }

        emailService.sendVerificationEmail(userEntity.getEmail(), userEntity.getEmailVerificationToken());
        /*
        Authentication authenticationReq = UsernamePasswordAuthenticationToken.unauthenticated(signUpReq.username, signUpReq.password);
        Authentication authenticationResp = null;
        try {
            authenticationResp = authenticationManager.authenticate(authenticationReq);
        } catch (AuthenticationException e) {
            return ResponseEntity.ok().body(new SignUpResp(false, new String[] {"Wrong credentials"}));
        }

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authenticationResp);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        if(signUpReq.rememberme){
            persistentTokenBasedRememberMeServices.loginSuccess(request, response, authenticationResp);
        }
        */
        return ResponseEntity.ok().body(new SignUpResp(true, null));
        
    }
    
    @PostMapping("/signin")
    public ResponseEntity<SignInResp> signin(@RequestBody SignInReq signInReq, HttpServletRequest request, HttpServletResponse response) {
        
        try{
            signInReq.validate();
        } catch(ValidationException e){
            return ResponseEntity.ok().body(new SignInResp(false, new String[] {"Wrong credentials"}));
        }

        UserEntity user = userRepository.findByUsername(signInReq.usernameOrEmail())
            .or(() -> userRepository.findByEmail(signInReq.usernameOrEmail()))
            .orElse(null);

        if (user == null) {
            return ResponseEntity.ok().body(new SignInResp(false, new String[] {"Invalid credentials"}));
        }
        if (!user.isEmailVerified()) {
            return ResponseEntity.ok().body(new SignInResp(false, new String[] {"Email not verified. Please check your email for verification link."}));
        }
        if (!user.isTelegramVerified()) {
            return ResponseEntity.ok().body(new SignInResp(false, new String[] {"Telegram not verified. Please verify it in TelegramApp"}));
        }

        Authentication authenticationReq = UsernamePasswordAuthenticationToken.unauthenticated(user.getUsername(), signInReq.password);
        Authentication authenticationResp = null;
        try {
            authenticationResp = authenticationManager.authenticate(authenticationReq);
        } catch (AuthenticationException e) {
            return ResponseEntity.ok().body(new SignInResp(false, new String[] {"Wrong credentials"}));
        }

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authenticationResp);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        if(signInReq.rememberme){
            persistentTokenBasedRememberMeServices.loginSuccess(request, response, authenticationResp);
        }

        return ResponseEntity.ok().body(new SignInResp(true, null));
    }

    @PostMapping("/signout")
    public ResponseEntity<SignOutResp> signout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        persistentTokenBasedRememberMeServices.logout(request, response, authentication);
        securityContextLogoutHandler.logout(request, response, authentication);
        return ResponseEntity.ok().body(new SignOutResp(true, null));
    }

    private record SignInReq(String usernameOrEmail, String password, boolean rememberme) {
        public void validate() throws ValidationException{
            
            if (usernameOrEmail == null || usernameOrEmail.isBlank()) {throw new ValidationException("Username or email is required");}
            

            if (password == null || password.isBlank()) {throw new ValidationException("Password is required");}
            if (password.length() < 8) {throw new ValidationException("Password must contain more than 8 characters");}
            if (password.length() > 256) {throw new ValidationException("Password must contain less than 256 characters");}
            if (!password.matches(".*[a-z].*")) {throw new ValidationException("Password must contain at least one lowercase letter");}
            if (!password.matches(".*[A-Z].*")) {throw new ValidationException("Password must contain at least one uppercase letter");}
            if (!password.matches(".*[0-9].*")) {throw new ValidationException("Password must contain at least one number");}
        }
    }
    private record SignUpReq(String username, String email, String telegram, String password, boolean rememberme) {
        public void validate() throws ValidationException{
            if (username == null || username.isBlank()) {throw new ValidationException("Username is required");}
            if (username.length() < 6) {throw new ValidationException("Username must contain more than 6 characters");}
            if (username.length() > 256) {throw new ValidationException("Username must contain less than 256 characters");}
            if (!username.matches("^[a-zA-Z0-9_]+$")) {throw new ValidationException("Username must contain only letters, numbers or symbol underscore");}
        
            if(email == null || email.isBlank()){throw new ValidationException("Email is required");}
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {throw new ValidationException("Invalid email address");}

            if(telegram == null || telegram.isBlank()){throw new ValidationException("Telegram is required");}
            if (!telegram.matches("^@[a-zA-Z0-9_]{5,32}$")) {throw new ValidationException("Invalid telegram id");}

            if (password == null || password.isBlank()) {throw new ValidationException("Password is required");}
            if (password.length() < 8) {throw new ValidationException("Password must contain more than 8 characters");}
            if (password.length() > 256) {throw new ValidationException("Password must contain less than 256 characters");}
            if (!password.matches(".*[a-z].*")) {throw new ValidationException("Password must contain at least one lowercase letter");}
            if (!password.matches(".*[A-Z].*")) {throw new ValidationException("Password must contain at least one uppercase letter");}
            if (!password.matches(".*[0-9].*")) {throw new ValidationException("Password must contain at least one number");}
        }
    }

    private record StatusResp(Boolean auth) {}
    private record SignUpResp(Boolean success, String[] errors) {}
    private record SignInResp(Boolean success, String[] errors) {}
    private record SignOutResp(Boolean success, String[] errors) {}
}