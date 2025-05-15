package com.cintie.marketplace_backend.entities;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, length = 255)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, unique = true, length = 255)
    private String telegram;

    @Column(length = 500)
    private String bio;

    @Lob
    @Column(columnDefinition = "BYTEA", length = 1048576)
    private byte[] avatar;

    @Column(length = 50)
    private String avatarContentType;

    @Column(nullable = true, unique = true)
    private Long telegramId;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean enabled = true;
    
    @Column(nullable = false)
    private boolean emailVerified;
    
    @Column
    private String emailVerificationToken;

    @Column(nullable = false)
    private boolean telegramVerified; 

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Long createdAt = System.currentTimeMillis();

    @Column
    private Long updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = System.currentTimeMillis();
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ProductEntity> products;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

     @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public enum UserRole{
        ROLE_USER,
        ROLE_MODERATOR,
        ROLE_ADMIN
    }

    public void setAvatar(MultipartFile file) throws IOException {
        this.avatar = file.getBytes();
        this.avatarContentType = file.getContentType();
    }

    public boolean hasAvatar() {
        return this.avatar != null && this.avatar.length > 0;
    }
}