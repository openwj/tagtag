package dev.tagtag.framework.config;

import dev.tagtag.framework.security.CustomAccessDeniedHandler;
import dev.tagtag.framework.security.CustomAuthenticationEntryPoint;
import dev.tagtag.framework.security.TokenVersionFilter;
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
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import dev.tagtag.common.constant.GlobalConstants;
import dev.tagtag.kernel.constant.SecurityClaims;
import dev.tagtag.kernel.constant.Permissions;
import dev.tagtag.kernel.constant.Roles;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.core.env.Environment;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

/**
 * Spring Security 核心配置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProperties jwtProperties;
    private final Environment environment;


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
                                                   JwtAuthenticationConverter jwtAuthenticationConverter,
                                                   TokenVersionFilter tokenVersionFilter) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    String extra = environment.getProperty("security.permit-paths");
                    List<String> permits = new ArrayList<>();
                    if (extra != null && !extra.isBlank()) {
                        for (String s : extra.split(",")) {
                            if (s != null && !s.isBlank()) permits.add(s.trim());
                        }
                    }
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**", "/api/captcha/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(permits.toArray(new String[0])).permitAll()
                        .anyRequest().authenticated();
                })
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
        http.addFilterAfter(tokenVersionFilter, AnonymousAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 密码加密器（Spring 官方内置 Delegating，默认 {bcrypt}）
     * @return 加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * JwtDecoder（HS256），使用配置化密钥
     * @return 解码器
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            String pubPem = jwtProperties.getPublicKeyPem();
            if (pubPem != null && !pubPem.isBlank()) {
                String normalized = pubPem.replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\s", "");
                byte[] keyBytes = Base64.getDecoder().decode(normalized);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
                RSAPublicKey rsaPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
                return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
            }
        } catch (Exception ignore) {
        }
        SecretKeySpec key = new SecretKeySpec(jwtProperties.getSecret().getBytes(GlobalConstants.CHARSET_UTF8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    /**
     * 将 roles/perms 映射为 GrantedAuthority
     * @return 转换器
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new Converter<Jwt, Collection<GrantedAuthority>>() {
            /**
             * 读取 claims 中的 roles/perms 并映射为权限集合
             * @param jwt JWT
             * @return 权限集合
             */
            @Override
            public Collection<GrantedAuthority> convert(Jwt jwt) {
                Set<GrantedAuthority> auths = new HashSet<>();
                Object rolesObj = jwt.getClaims().get(SecurityClaims.ROLES);
                if (rolesObj instanceof Collection<?> rc) {
                    for (Object r : rc) if (r != null) auths.add(new SimpleGrantedAuthority(Roles.PREFIX + String.valueOf(r)));
                }
                Object permsObj = jwt.getClaims().get(SecurityClaims.PERMS);
                if (permsObj instanceof Collection<?> pc) {
                    for (Object p : pc) if (p != null) auths.add(new SimpleGrantedAuthority(Permissions.PREFIX + String.valueOf(p)));
                }
                return auths;
            }
        });
        return converter;
    }
}
