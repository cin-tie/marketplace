package com.cintie.marketplace_backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;



@Service
public class EmailService {
    
    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token){
        String verificationUrl = baseUrl + "/auth/verify-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Verify your email address");
        message.setText("Please click the folowing link to verify your email: " + verificationUrl);
        mailSender.send(message);
    }

    public void sendPasswordResetCode(String to, String resetCode){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Password Reset Code");
        message.setText(String.format(
            "Your password reset code is: %s\n\n" +
            "This code will expire in 10 minutes.", 
            resetCode
        ));
        mailSender.send(message);
    }

}
