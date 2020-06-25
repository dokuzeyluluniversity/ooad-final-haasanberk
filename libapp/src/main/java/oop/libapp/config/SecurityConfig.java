package oop.libapp.config;

import oop.libapp.security.jwt.JWTAuthenticationFilter;
import oop.libapp.security.jwt.JWTAuthenticationManager;
import oop.libapp.security.jwt.JWTAuthenticationProvider;
import oop.libapp.register.CustomUserDetailsService;
import oop.libapp.register.IUserService;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements AuthenticationEntryPoint {

    @Order(1)
    @Configuration
    public static class JWTSecurityConfig extends WebSecurityConfigurerAdapter {

        @Bean
        public AuthenticationProvider jwtAuthenticationProvider() {
            return new JWTAuthenticationProvider();
        }

        @Bean
        public AuthenticationManager jwtAuthenticationManager() {
            return new JWTAuthenticationManager(jwtAuthenticationProvider());
        }

        @Bean
        public RequestMatcher jwtRequestMatcher() {
            return new AntPathRequestMatcher("/api/**");
        }

        @Bean
        public JWTAuthenticationFilter jwtAuthenticationFilter() {
            JWTAuthenticationFilter filter = new JWTAuthenticationFilter();
            filter.setAuthenticationManager(jwtAuthenticationManager());
            filter.setRequiresAuthenticationRequestMatcher(jwtRequestMatcher());
            filter.setContinueChainBeforeSuccessfulAuthentication(true);
            return filter;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .cors().and().csrf().disable()
                    .anonymous().disable()
                    .httpBasic().disable()
                    .requestMatcher(jwtRequestMatcher())
                    .authorizeRequests()
                    .antMatchers(HttpMethod.GET,"/api/**").hasRole("USER")
                    .antMatchers(HttpMethod.POST,"/api/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.PATCH, "/api/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .exceptionHandling()
                    .and()
                    .addFilterBefore(jwtAuthenticationFilter(), BasicAuthenticationFilter.class);
        }
    }

    @Order(2)
    @Configuration
    public static class BasicSecurityConfig extends WebSecurityConfigurerAdapter {

        private ApplicationContext context;
        private PasswordEncoder passwordEncoder;

        @Autowired
        public BasicSecurityConfig(ApplicationContext context, PasswordEncoder passwordEncoder) {
            this.context = context;
            this.passwordEncoder = passwordEncoder;
        }

        @Bean
        public RequestMatcher basicRequestMatcher() {
            return new AntPathRequestMatcher("/users/**");
        }

        @Bean
        public UserDetailsService userDetailsService() {
            IUserService userService = context.getBean(IUserService.class);
            return new CustomUserDetailsService(userService);
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder);
            
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .requestMatcher(basicRequestMatcher())
                    .authorizeRequests()
                    .antMatchers("/users/get-token").authenticated()
                    .antMatchers("/users/register").anonymous()
                    .and()
                    .httpBasic()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .exceptionHandling();
        }
    }

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
		
	}
    
   
}
