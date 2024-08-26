package com.example.demo.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;  

/**  
 * 스프링 스큐리티 설정 
 */
@Configuration  
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
	
    private final UserDetailsService userDetailsService;

    @Bean  
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {  
    	
    	http.headers(headers -> 
    			headers.frameOptions(option -> option.disable()));
    	
    	http.cors(cors ->
    			cors.disable());
    	
        http.csrf(AbstractHttpConfigurer::disable)
        	.authorizeHttpRequests(auth ->  
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(
                    		"/swagger-ui/**",
                    		"/h2-console/**",
                    		"/ws/**",
                    		"/chat"
                    		)
                    		.permitAll()
                            .anyRequest().authenticated()
            ).formLogin(form -> form
                    .loginPage("/login_page")
                    .loginProcessingUrl("/login")
                    .usernameParameter("id")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/", true)
                    .permitAll()
            )
            .logout(logout -> logout
                    .permitAll()
                    .logoutSuccessUrl("/login_page") 
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID"))
        	.authenticationProvider(this.authenticationProvider())
            .exceptionHandling(exception ->  
                    exception.authenticationEntryPoint(
                    		(request, response, authException)
                            -> response.sendRedirect("/login_page")));  
//        -> response.sendError(
//                HttpServletResponse.SC_UNAUTHORIZED,
//                authException.getLocalizedMessage()
//              )));  
        return http.build();  
    }  
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
       return authenticationConfiguration.getAuthenticationManager();
    }
    
    // TODO : 추후 제거될 항목
    @Deprecated
    @Bean
    PasswordEncoder passwordEncoder() {
      return NoOpPasswordEncoder.getInstance();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean  
    public WebSecurityCustomizer webSecurityCustomizer() {  
        return (web) -> web.ignoring().requestMatchers(
        		"/api/v1/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/js/**",
                "/css/**",
                "/img/**",
                "/lib/**",
                "/scss/**"
        		);  
    }
  

}