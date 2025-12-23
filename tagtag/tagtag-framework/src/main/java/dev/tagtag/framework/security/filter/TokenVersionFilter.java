package dev.tagtag.framework.security.filter;

import dev.tagtag.framework.constant.SecurityClaims;
import dev.tagtag.framework.security.service.TokenVersionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class TokenVersionFilter extends OncePerRequestFilter {

    private final TokenVersionService tokenVersionService;

    public TokenVersionFilter(TokenVersionService tokenVersionService) {
        this.tokenVersionService = tokenVersionService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (shouldValidateToken(auth)) {
            Jwt jwt = (Jwt) auth.getPrincipal();
            Long userId = extractUserId(jwt);
            Long tokenVersion = extractTokenVersion(jwt);
            
            if (userId != null && tokenVersion != null) {
                if (!tokenVersionService.isTokenVersionValid(userId, tokenVersion)) {
                    log.warn("Token version mismatch for user {}: token version={}, current version={}", 
                            userId, tokenVersion, tokenVersionService.getCurrentVersion(userId));
                    SecurityContextHolder.clearContext();
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean shouldValidateToken(Authentication auth) {
        return auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String) && auth.getPrincipal() instanceof Jwt;
    }

    private Long extractUserId(Jwt jwt) {
        Object uidObj = jwt.getClaims().get(SecurityClaims.UID);
        if (uidObj == null) {
            return null;
        }
        try {
            return Long.valueOf(uidObj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long extractTokenVersion(Jwt jwt) {
        Object verObj = jwt.getClaims().get(SecurityClaims.VER);
        if (verObj == null) {
            return null;
        }
        try {
            return Long.valueOf(verObj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
