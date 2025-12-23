package dev.tagtag.framework.security.config;

import dev.tagtag.kernel.constant.Roles;
import dev.tagtag.kernel.constant.SecurityClaims;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class JwtAuthenticationConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new JwtGrantedAuthoritiesConverter());
        return converter;
    }

    public static class JwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

        @Override
        public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
            Set<GrantedAuthority> authorities = new LinkedHashSet<>();

            Collection<?> roles = extractClaimAsCollection(jwt, SecurityClaims.ROLES);
            if (roles != null) {
                authorities.addAll(roles.stream()
                        .map(role -> new SimpleGrantedAuthority(Roles.PREFIX + role))
                        .collect(Collectors.toSet()));
            }

            Collection<String> perms = extractClaimAsCollection(jwt, SecurityClaims.PERMS);
            if (perms != null) {
                authorities.addAll(perms.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet()));
            }

            return authorities;
        }

        @SuppressWarnings("unchecked")
        private <T> Collection<T> extractClaimAsCollection(Jwt jwt, String claimName) {
            Object claim = jwt.getClaim(claimName);
            if (claim == null) {
                return Collections.emptyList();
            }
            if (claim instanceof Collection) {
                return (Collection<T>) claim;
            }
            return Stream.of((T) claim).collect(Collectors.toList());
        }
    }
}
