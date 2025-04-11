package com.cintie.marketplace_backend.entities;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`user`")
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column
    private String password;

    @Column(nullable = false)
    private String role;
    
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String telegram;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean enabled = true;
    
    @Column(nullable = false)
    private boolean isEmailVerified;
    
    @Column
    private String emailVerificationToken;

    @Column(nullable = false)
    private boolean isTelegramVerified;
    
    @Column
    private String telegramVerificationToken;

    @Column(unique = true)
    private Long telegramChatId;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<ProductEntity> products;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}