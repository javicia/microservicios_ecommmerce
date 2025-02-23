package security;

import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	  @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http
	            // Deshabilitar CSRF ya que Eureka y las comunicaciones entre microservicios no trabajan con él
	            .csrf(csrf -> csrf.disable())
	       
	            // reglas de autorización:
	            // - Cualquier otra solicitud requiere autenticación.
	            .authorizeHttpRequests(auth -> auth
	            	.requestMatchers("/eureka/**").permitAll() // Permitir acceso a endpoints de Eureka
	                .anyRequest().authenticated()
	            )
	            // Habilita la autenticación HTTP Basic
	            .httpBasic(Customizer.withDefaults());

	        return http.build();
	    }
	    
   @Bean
   public InMemoryUserDetailsManager userdetails() throws Exception {
      Collection<UserDetails> users = List.of(
            User.withUsername("admin")
             .password("{noop}admin") 
              .roles("ADMIN")
               .build());
      return new InMemoryUserDetailsManager(users);
    }
}


