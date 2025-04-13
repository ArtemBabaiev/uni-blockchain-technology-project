package com.ababaiev.security;

import com.ababaiev.views.home.HomeView;
import com.ababaiev.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers("/line-awesome/**").permitAll();
            authorize.requestMatchers("/VAADIN/dynamic/resource/**").permitAll();
        }).csrf(csrf -> {
            csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/VAADIN/dynamic/resource/**"));
        }).headers(headers -> {
            headers.frameOptions(Customizer.withDefaults()).disable();
        });;
        super.configure(http);

        setLoginView(http, LoginView.class, "/");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}