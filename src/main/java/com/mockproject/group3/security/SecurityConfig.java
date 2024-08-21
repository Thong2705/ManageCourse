package com.mockproject.group3.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import com.mockproject.group3.filter.JwtAuthenticationFilter;
import com.mockproject.group3.service.UsersService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
        // private static final String[] PUBLIC_ENPOINT = { "/**" };
        private final OAuthAuthenicationSuccessHandler handler;
        private final UsersService usersService;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final CustomLogoutHandler logoutHandler;

        // @Bean
        // public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws
        // Exception {
        // httpSecurity.authorizeHttpRequests(request -> request
        // .requestMatchers(HttpMethod.POST, PUBLIC_ENPOINT).permitAll()
        // .requestMatchers(HttpMethod.GET, PUBLIC_ENPOINT).permitAll()
        // .requestMatchers(HttpMethod.PUT, PUBLIC_ENPOINT).permitAll()
        // .requestMatchers(HttpMethod.DELETE, PUBLIC_ENPOINT).permitAll()
        // .anyRequest().authenticated())
        // .csrf(AbstractHttpConfigurer::disable)
        // .exceptionHandling(handling -> handling
        // .authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
        // httpSecurity.oauth2Login(oauth -> {
        // oauth.loginPage("/login");
        // oauth.successHandler(handler);
        // });
        // return httpSecurity.build();
        // }

        public SecurityConfig(OAuthAuthenicationSuccessHandler handler, @Lazy UsersService usersService,
                        @Lazy JwtAuthenticationFilter jwtAuthenticationFilter, CustomLogoutHandler logoutHandler) {
                this.handler = handler;
                this.usersService = usersService;
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.logoutHandler = logoutHandler;
        }

        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/static/favicon.ico");
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http.csrf(AbstractHttpConfigurer::disable)
                        .authorizeHttpRequests(
                                req->req.requestMatchers("/login/**",
                                "/register_google/**","/validation/**","/forgot-password/**","/vnpay_return/**","/users/**", "/payment/**").permitAll()
                                        .anyRequest().authenticated()
                        ).userDetailsService(usersService)
                        .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                        .logout(l->l.logoutUrl("/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(
                                (request, response, authentication)-> SecurityContextHolder.clearContext()
                        
                        ))
                        .oauth2Login(oauth -> {
                                oauth.loginPage("/login/login");
                                oauth.successHandler(handler);
                        })
                        .build();

        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
        }

}
