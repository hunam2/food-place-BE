package com.github.foodplacebe.config.security;

import com.github.foodplacebe.config.security.exception.CustomAccessDeniedHandler;
import com.github.foodplacebe.config.security.exception.CustomAuthenticationEntryPoint;
import com.github.foodplacebe.web.filters.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenConfig jwtTokenConfig;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(h->h.frameOptions(f->f.sameOrigin()))
                .csrf((c)->c.disable())
                .httpBasic((h)->h.disable())
                .formLogin(f->f.disable())

                .rememberMe(r->r.disable())
                .cors(c->{
                    c.configurationSource(corsConfigurationSource());
                })
                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


                .exceptionHandling(e->{
                    e.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
                    e.accessDeniedHandler(new CustomAccessDeniedHandler());

                })
                .authorizeRequests(a ->
                            a
                                    .requestMatchers("/resources/static/**","/v1/api/test", "/auth/*","/").permitAll()

                )
                .logout(l->{
                    l.logoutRequestMatcher(new AntPathRequestMatcher("/api/account/logout"));
                    l.logoutSuccessUrl("/api/account/login");
                    l.invalidateHttpSession(true);
                })
                .addFilterBefore(new JwtFilter(jwtTokenConfig), UsernamePasswordAuthenticationFilter.class);
    return http.build();
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("*"));
//        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addExposedHeader("Token");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowedMethods(Arrays.asList("GET","PUT","POST","PATCH","DELETE","OPTIONS"));
        corsConfiguration.setMaxAge(1000L*60*60);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
