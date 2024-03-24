package com.deksi.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(this.userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    @Lazy
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationTokenFilter authenticationTokenFilterBean()
            throws Exception {
        AuthenticationTokenFilter authenticationTokenFilter = new AuthenticationTokenFilter();
        authenticationTokenFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationTokenFilter;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.headers().cacheControl().disable();
        httpSecurity.cors();
        httpSecurity.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/users/signup").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users/change-password").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/users/update-password").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users/update-score").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/user-scores").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users/sudoku-update-time").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/sudoku-user-time").permitAll()
                .antMatchers(HttpMethod.GET, "/api/slagalica/random-round").permitAll()
                .antMatchers(HttpMethod.GET, "/api/slagalica/get/{id}").permitAll()
                .antMatchers(HttpMethod.GET, "/api/korakPoKorak/random-round").permitAll()
                .antMatchers(HttpMethod.GET, "/api/korakPoKorak/get/{id}").permitAll()
                .antMatchers(HttpMethod.GET, "/api/spojnice/random-round").permitAll()
                .antMatchers(HttpMethod.GET, "/api/spojnice/get/{id}").permitAll()
                .antMatchers(HttpMethod.GET, "/api/koznazna/random-rounds").permitAll()
                .antMatchers(HttpMethod.GET, "/api/koznazna/get/{id}").permitAll()
                .anyRequest().authenticated();

        httpSecurity.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }
}