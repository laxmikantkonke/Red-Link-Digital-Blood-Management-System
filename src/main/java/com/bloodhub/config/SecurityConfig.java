package com.bloodhub.config;

import com.bloodhub.component.CustomAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(
                        "/",
                        "/about",
                        "/blood-info",
                        "/achievements",
                        "/login",
                        "/register",
                        "/forgot-password",
                        "/reset-password",
                        "/hospitals/nearby/**",
                        "/hospitals/view/**",
                        "/css/**",
                        "/js/**",
                        "/vendor/**",
                        "/images/**",
                        "/api/**",
                        "/error")

                .permitAll()

                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .successHandler(customAuthenticationSuccessHandler)
                .and()
                .logout()
                .logoutSuccessUrl("/");

        return http.build();
    }
}
