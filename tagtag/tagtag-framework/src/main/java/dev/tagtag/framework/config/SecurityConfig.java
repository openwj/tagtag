package dev.tagtag.framework.config;

import dev.tagtag.common.constant.GlobalConstants;
import dev.tagtag.framework.security.CustomAccessDeniedHandler;
import dev.tagtag.framework.security.CustomAuthenticationEntryPoint;
import dev.tagtag.framework.security.TokenVersionFilter;
import dev.tagtag.kernel.constant.Permissions;
import dev.tagtag.kernel.constant.Roles;
import dev.tagtag.kernel.constant.SecurityClaims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Spring Security 核心配置
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String PUBLIC_KEY_ALGORITHM = "RSA";
    private static final String SECRET_KEY_ALGORITHM = "HmacSHA256";

    private static final List<String> DEFAULT_PERMIT_PATHS = List.of(
        "/api/auth/**",
        "/api/captcha/**",
        "/v3/api-docs/**",
        "/scalar/**",
        "/index.html",
        "/",
        "/favicon.ico",
        "/api/auth/tools/**"
    );

    private final JwtProperties jwtProperties;
    private final Environment environment;

    /**
     * 配置安全过滤链（无状态、开放认证端点）
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        CustomAuthenticationEntryPoint entryPoint,
        CustomAccessDeniedHandler accessDeniedHandler,
        JwtAuthenticationConverter jwtAuthenticationConverter,
        TokenVersionFilter tokenVersionFilter
    ) throws Exception {
        List<String> permitPaths = buildPermitPaths();

        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(permitPaths.toArray(new String[0])).permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(eh -> eh
                .authenticationEntryPoint(entryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                .bearerTokenResolver(new CustomBearerTokenResolver(permitPaths))
            )
            .addFilterAfter(tokenVersionFilter, AnonymousAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 密码加密器（Spring 官方内置 Delegating，默认 {bcrypt}）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * JwtDecoder（HS256），使用配置化密钥
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        String pubPem = jwtProperties.getPublicKeyPem();
        if (pubPem != null && !pubPem.isBlank()) {
            try {
                RSAPublicKey rsaPublicKey = parseRSAPublicKey(pubPem);
                return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
            } catch (Exception e) {
                log.warn("Failed to parse RSA public key, fallback to secret key", e);
            }
        }

        SecretKeySpec key = new SecretKeySpec(
            jwtProperties.getSecret().getBytes(GlobalConstants.CHARSET_UTF8),
            SECRET_KEY_ALGORITHM
        );
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    /**
     * 将 roles/perms 映射为 GrantedAuthority
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new JwtGrantedAuthoritiesConverter());
        return converter;
    }

    /**
     * 构建允许公开访问的路径列表
     */
    private List<String> buildPermitPaths() {
        String extraPaths = environment.getProperty("security.permit-paths");
        if (extraPaths == null || extraPaths.isBlank()) {
            return DEFAULT_PERMIT_PATHS;
        }

        return Stream.of(extraPaths.split(","))
            .map(String::trim)
            .filter(path -> !path.isBlank())
            .collect(Collectors.toList());
    }

    /**
     * 解析 RSA 公钥
     */
    private RSAPublicKey parseRSAPublicKey(String pubPem) throws Exception {
        String normalized = pubPem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(normalized);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return (RSAPublicKey) KeyFactory.getInstance(PUBLIC_KEY_ALGORITHM).generatePublic(spec);
    }
}
