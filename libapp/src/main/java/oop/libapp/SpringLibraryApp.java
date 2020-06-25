package oop.libapp;

import oop.libapp.message.ErrorMessage;
import oop.libapp.message.IErrorMessage;
import oop.libapp.security.secret.ISecretGenerator;
import oop.libapp.security.secret.SecretGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class SpringLibraryApp {

    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST)
    public IErrorMessage errorMessage() {
        return new ErrorMessage();
    }

    @Bean
    public ISecretGenerator secretGenerator() {
        return new SecretGenerator(32);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    public static void main(String[] args) {
        SpringApplication.run(SpringLibraryApp.class, args);
    }
}
