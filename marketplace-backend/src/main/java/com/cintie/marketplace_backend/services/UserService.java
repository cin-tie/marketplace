package com.cintie.marketplace_backend.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cintie.marketplace_backend.entities.UserEntity;
import com.cintie.marketplace_backend.exceptions.EmailAlreadyExistsException;
import com.cintie.marketplace_backend.exceptions.TelegramAlreadyExistsException;
import com.cintie.marketplace_backend.exceptions.UsernameAlreadyExistsException;
import com.cintie.marketplace_backend.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService{
    
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("Username not found"));
        return userEntity;
    }
    public UserEntity createUser(UserEntity userEntity) throws UsernameAlreadyExistsException, EmailAlreadyExistsException, TelegramAlreadyExistsException{
        if(userRepository.existsByUsername(userEntity.getUsername())){
            throw new UsernameAlreadyExistsException();
        }
        if(userRepository.existsByEmail(userEntity.getEmail())){
            throw new EmailAlreadyExistsException();
        }
        if(userRepository.existsByTelegram(userEntity.getTelegram())){
            throw new TelegramAlreadyExistsException();
        }
        if(userRepository.existsByTelegramId(userEntity.getTelegramId()) && userEntity.getTelegramId() != null){
            throw new TelegramAlreadyExistsException();
        }

        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userRepository.save(userEntity);
        return userEntity;
    }
}
