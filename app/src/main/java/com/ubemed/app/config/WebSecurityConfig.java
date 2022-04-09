package com.ubemed.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Value("${server_ip}")
    private String ip;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(ip, "http://localhost:4200", "http://localhost:80"));
        configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers(
                        "/swagger*/**",
                        "/v2/api-docs"
                );
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        // all url by default need token
        httpSecurity
                // token + admin only
                .authorizeRequests()
                .antMatchers("/admin/**")
                .hasAuthority("admin")
                // token + user
                .antMatchers(HttpMethod.PUT, "/forum/{id}").hasAnyAuthority("user")
                .antMatchers(HttpMethod.POST, "/store/{id}").hasAnyAuthority("user")
                .antMatchers(
                        "/users/check",
                        "users/coins",
                        "/forum/new",
                        "/store/add")
                .hasAnyAuthority("user")

                // no token
                .antMatchers(
                        "/swagger*/**",
                        "/v2/api-docs",
                        "/forum/**",
                        "/store/**",
                        "/users/**",
                        "/**"
                )
                .permitAll()
                .anyRequest().authenticated()
                // make sure we use stateless session; session won't be used to
                // store user's state.
                .and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.csrf().disable();
        httpSecurity.cors();
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}

