package com.practice.growth.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class AdminAbstractSecurityConfiguration {

    @Bean
    public AdminAuthenticationSuccessHandlerImpl successHandler() {
        return new AdminAuthenticationSuccessHandlerImpl();
    }

    @Bean
    public AdminAuthenticationFailureHandlerImpl failureHandler() {
        return new AdminAuthenticationFailureHandlerImpl();
    }
}
