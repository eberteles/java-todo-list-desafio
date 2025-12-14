package com.stefanini.desafio.todolistapi.infrastructure.config;

import com.stefanini.desafio.todolistapi.application.exception.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${api.security.user.name}")
    private String username;

    @Value("${api.security.user.password}")
    private String password;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public SecurityConfig(CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    // 1. Configuração da Cadeia de Filtros de Segurança (Autorização)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF (Comum para APIs RESTful que não usam sessões)
                .csrf(AbstractHttpConfigurer::disable)
                // Define as regras de autorização
                .authorizeHttpRequests(authorize -> authorize
                        // Libera o acesso aos endpoints do Swagger
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Exige autenticação para todas as outras requisições
                        .anyRequest().authenticated()
                )
                // Habilita autenticação Basic Auth e define o entry point customizado
                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(customAuthenticationEntryPoint));

        return http.build();
    }

    // 2. Configuração de Usuários em Memória (Para simplificar o desafio)
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    // 3. Bean para o Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
