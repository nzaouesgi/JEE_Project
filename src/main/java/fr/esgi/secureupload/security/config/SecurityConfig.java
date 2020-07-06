package fr.esgi.secureupload.security.config;

import fr.esgi.secureupload.security.jwt.JWTFilter;
import fr.esgi.secureupload.security.jwt.JWTProvider;

import fr.esgi.secureupload.security.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@ComponentScan
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JWTProvider jwtProvider;

    SecurityConfig(JWTProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .authorizeRequests().anyRequest().permitAll()
                .and()
                .addFilterBefore(new JWTFilter(this.jwtProvider), UsernamePasswordAuthenticationFilter.class);

        String csp = "default-src 'none'";

        http.headers()
                .xssProtection().xssProtectionEnabled(true)
                .and()
                .contentTypeOptions()
                .and()
                .addHeaderWriter(new StaticHeadersWriter("Content-Security-Policy", csp))
                .addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy", csp))
                .addHeaderWriter(new StaticHeadersWriter("X-WebKit-CSP", csp));
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(getApplicationContext().getBean(UserDetailsServiceImpl.class));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(32, 32, 2, 4096, 5);
    }

}