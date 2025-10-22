package OrangeCloud.UserRepo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Swagger UI 경로 허용
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        // 인증 API 허용
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/groups/**").permitAll()
//                        .requestMatchers("/api/projects/**").permitAll()
                        .requestMatchers("/api/userinfo/**").permitAll()
                        .requestMatchers("/api/teams/**").permitAll()
                        .requestMatchers("/api/users/**").permitAll()
                        // H2 Console 허용
                        .requestMatchers("/h2-console/**").permitAll()
                        // 테스트 엔드포인트 허용
                        .requestMatchers("/test").permitAll()
                        .requestMatchers("/error").permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .build();
    }
    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }

}