package security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/auth/login", 
								"/eureka/**",
								"/v3/api-docs/**", 
								"/swagger-ui.html", 
								"/swagger-ui/**",
								"/webjars/**")
						.permitAll() // Permitir acceso al endpoint de login (si decides mantenerlo)
						.anyRequest().authenticated() // Proteger todos los demás endpoints
				)
				.oauth2ResourceServer(
						oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No usar
																												// sesiones
				);

		return http.build();
	}

	/**
	 * Configura la conversión de JWT para extraer roles y autoridades.
	 */
	private JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		// Keycloak coloca los roles en 'realm_access.roles'
		grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); // Prefijo para los roles
		grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles");

		JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
		authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		return authenticationConverter;
	}
}
