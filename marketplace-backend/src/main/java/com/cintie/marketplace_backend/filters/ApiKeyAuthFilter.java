package com.cintie.marketplace_backend.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cintie.marketplace_backend.utils.ApiKeyAuthentication;

import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Collections;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final String apiKeyHeaderName;
    private final String apiKey;

    public ApiKeyAuthFilter(String apiKeyHeaderName, String apiKey) {
        this.apiKeyHeaderName = apiKeyHeaderName;
        this.apiKey = apiKey;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        if (!request.getRequestURI().startsWith("/api/telegram/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKeyHeader = request.getHeader(apiKeyHeaderName);
        
        if (apiKeyHeader == null || !apiKeyHeader.equals(apiKey)) {
            throw new BadCredentialsException("Invalid API Key");
        }

        Authentication auth = new ApiKeyAuthentication(apiKeyHeader, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        filterChain.doFilter(request, response);
    }
}