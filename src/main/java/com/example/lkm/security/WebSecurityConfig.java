package com.example.lkm.security;

import com.example.lkm.security.jwt.JwtAuthenticationFilter;
import com.example.lkm.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;

import java.net.InetAddress;
import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    private final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    private final CustomUserDetailService customUserDetailService;
    private final DynamicAuthorizationFilter dynamicAuthorizationFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) throws Exception {

        JsonUsernamePasswordAuthenticationFilter jsonFilter =
                new JsonUsernamePasswordAuthenticationFilter(authenticationManager,jwtTokenProvider);

        CsrfTokenRequestAttributeHandler handler = new CsrfTokenRequestAttributeHandler();
        handler.setCsrfRequestAttributeName("_csrf"); // 기본 이름

        InetAddress localHost = InetAddress.getLocalHost();
        String ipAddress = localHost.getHostAddress();
        String origin = "http://" + ipAddress + ":3000";

        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:3000",origin));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN", "Authorization"));
                    config.setExposedHeaders(List.of("Authorization"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                //새션인증시 사용
//                .csrf(csrf -> csrf
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                        .csrfTokenRequestHandler(handler)
//                        .ignoringRequestMatchers("/h2-console/**", "/api/login", "/api/logout", "/api/check-username","/api/save-user")
//                )
//                .sessionManagement(session -> session
//                        .invalidSessionUrl("/")
//                        .maximumSessions(1)
//                        .maxSessionsPreventsLogin(false)
//                )
                .csrf(csrf -> csrf.disable())
                //api호출 권한 설정
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/h2-console/**").permitAll()
//                        .requestMatchers("/api/api1").hasRole("USER")
//                        .requestMatchers("/api/api2").hasRole("ADMIN")
//                        .requestMatchers("/api/users/**").hasRole("MASTER")
//                        .requestMatchers("/api/**").permitAll()
//                        .requestMatchers("/h2-console/**", "/api/login", "/api/logout", "/api/save-user").permitAll()
//                        .anyRequest().authenticated()
//                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(dynamicAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)

//                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
//                .formLogin(form -> form
//                        .loginProcessingUrl("/api/login") // 이 경로로 POST 요청 시 자동 인증
//                        .successHandler((request, response, authentication) -> {
//                            response.setStatus(HttpServletResponse.SC_OK);
//                            logger.debug("로그인 성공");
//                        })
//                        .failureHandler((request, response, exception) -> {
//                            logger.warn("로그인 실패 - 이유: {}", exception.getMessage());
//                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 실패");
//                        })
//                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json; charset=UTF-8");
                            response.getWriter().write("{\"message\": \"로그아웃 성공\"}");
                            logger.debug("로그아웃 성공");
                        })
                        //세션기반일때만 사용
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
                )
                .addFilterAt(jsonFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder();
    }
}

