
package com.example.application.security;

import com.example.application.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Konfigurationsklasse für die Sicherheitseinstellungen der Anwendung.
 *
 * @author Kennet
 */
@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    /**
     * Erstellt ein PasswordEncoder-Objekt, das zum Hashen von Passwörtern verwendet wird.
     *
     * @author Kennet
     * @return Ein PasswordEncoder-Objekt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Konfiguriert die Sicherheitseinstellungen der Anwendung.
     * Hier werden die Zugriffsrechte für verschiedene URLs festgelegt und die Login-Seite konfiguriert.
     *
     * @author Kennet
     * @param http Ein HttpSecurity-Objekt, das zur Konfiguration der Sicherheitseinstellungen verwendet wird.
     * @throws Exception Wenn ein Fehler bei der Konfiguration auftritt.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
                authorize -> authorize.requestMatchers(new AntPathRequestMatcher("/images/*.png")).permitAll());

        // Icons from the line-awesome addon
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll());

        super.configure(http);
        setLoginView(http, LoginView.class);

        http.formLogin(
                formLogin -> {
                    formLogin.loginPage("/login");
                    formLogin.loginProcessingUrl("/login");
                    formLogin.defaultSuccessUrl("/", true);
                    //formLogin.failureHandler(new CustomAuthenticationFailureHandler());
                });
    }
}
