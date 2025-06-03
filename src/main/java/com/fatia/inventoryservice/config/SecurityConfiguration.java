package com.fatia.inventoryservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(
                                        "/api/v1/storage-keeping-unit/get-all",
                                        "/api/v1/storage-keeping-unit/get-sku/{id}",
                                        "/api/v1/shipment-batch/get-all",
                                        "/api/v1/shipment-batch/get-shipment-batch/{id}"
                                ).hasRole("ADMIN")
                                .requestMatchers(
                                        "/api/v1/storage-keeping-unit/get-all",
                                        "/api/v1/storage-keeping-unit/get-sku/{id}",
                                        "/api/v1/shipment-batch/get-all",
                                        "/api/v1/shipment-batch/get-shipment-batch/{id}"
                                ).hasRole("MANAGER")
                                .requestMatchers(
                                        "/api/v1/storage-keeping-unit/get-all",
                                        "/api/v1/storage-keeping-unit/get-sku/{id}",
                                        "/api/v1/storage-keeping-unit/add-sku",
                                        "/api/v1/storage-keeping-unit/change-sku",
                                        "/api/v1/storage-keeping-unit/delete-sku/{id}",
                                        "/api/v1/storage-keeping-unit/change-status",
                                        "/api/v1/shipment-batch/get-all",
                                        "/api/v1/shipment-batch/get-shipment-batch/{id}",
                                        "/api/v1/shipment-batch/add-shipment-batch",
                                        "/api/v1/shipment-batch/change-shipment-batch",
                                        "/api/v1/shipment-batch/change-status",
                                        "/api/v1/shipment-batch/delete-shipment-batch/{id}"
                                ).hasRole("WORKER")
                                .requestMatchers(
                                        "/api/v1/storage-keeping-unit/generate-code"
                                ).permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
