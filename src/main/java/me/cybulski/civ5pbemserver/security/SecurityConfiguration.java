package me.cybulski.civ5pbemserver.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author Michał Cybulski
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsProvider userDetailsProvider;
    private final DefaultAuthenticationProvider defaultAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                    .antMatchers(
                            "/user-accounts/register",
                            "/user-accounts/reset-access-token",
                            "/user-accounts/current").permitAll()
                    .anyRequest().fullyAuthenticated()
                .and()
                    .formLogin().permitAll().disable();
        http.addFilter(tokenAuthenticationFilterBean());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(defaultAuthenticationProvider);
    }

    @Bean
    public TokenAuthenticationFilterBean tokenAuthenticationFilterBean() throws Exception {
        TokenAuthenticationFilterBean tokenAuthenticationFilterBean = new TokenAuthenticationFilterBean(userDetailsProvider);
        tokenAuthenticationFilterBean.setAuthenticationManager(authenticationManagerBean());

        return tokenAuthenticationFilterBean;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
