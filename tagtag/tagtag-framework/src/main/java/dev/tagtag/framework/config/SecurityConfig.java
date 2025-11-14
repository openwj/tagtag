package dev.tagtag.framework.config;

import dev.tagtag.framework.security.CustomAccessDeniedHandler;
import dev.tagtag.framework.security.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 核心配置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * 配置安全过滤链（无状态、开放认证端点）
     * @param http HttpSecurity
     * @param entryPoint 未认证处理
     * @param accessDeniedHandler 无权限处理
     * @return 过滤链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CustomAuthenticationEntryPoint entryPoint,
                                                   CustomAccessDeniedHandler accessDeniedHandler,
                                                   JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
        return http.build();
    }

    /**
     * 密码加密器（BCrypt）
     * @return 加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * JwtDecoder（HS256），使用配置化密钥
     * @return 解码器
     */
    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret:tagtag-default-secret}") String secret) {
        javax.crypto.spec.SecretKeySpec key = new javax.crypto.spec.SecretKeySpec(secret.getBytes(dev.tagtag.common.constant.GlobalConstants.CHARSET_UTF8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    /**
     * 将 roles/perms 映射为 GrantedAuthority
     * @return 转换器
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new Converter<Jwt, java.util.Collection<GrantedAuthority>>() {
            /**
             * 读取 claims 中的 roles/perms 并映射为权限集合
             * @param jwt JWT
             * @return 权限集合
             */
            @Override
            public java.util.Collection<GrantedAuthority> convert(Jwt jwt) {
                java.util.Set<GrantedAuthority> auths = new java.util.HashSet<>();
                Object rolesObj = jwt.getClaims().get("roles");
                if (rolesObj instanceof java.util.Collection<?> rc) {
                    for (Object r : rc) if (r != null) auths.add(new SimpleGrantedAuthority("ROLE_" + String.valueOf(r)));
                }
                Object permsObj = jwt.getClaims().get("perms");
                if (permsObj instanceof java.util.Collection<?> pc) {
                    for (Object p : pc) if (p != null) auths.add(new SimpleGrantedAuthority("PERM_" + String.valueOf(p)));
                }
                return auths;
            }
        });
        return converter;
    }
}
