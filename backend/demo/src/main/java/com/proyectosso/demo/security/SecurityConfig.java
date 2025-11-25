package com.proyectosso.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configuracion de seguridad principal de la aplicacion.
 *
 * Esta clase integra Spring Security con un servidor de autorizacion basado en JWT
 * (por ejemplo, Keycloak), y habilita la seguridad a nivel de metodos.
 *
 * Define el {@link SecurityFilterChain} que:
 * <ul>
 *     <li>Desactiva CSRF para escenarios tipicos de APIs REST.</li>
 *     <li>Exige autenticacion para cualquier solicitud HTTP.</li>
 *     <li>Configura el recurso OAuth2 para validar tokens JWT.</li>
 * </ul>
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Define la cadena de filtros de seguridad utilizada por Spring Security.
     *
     * <p>Configuraciones principales:</p>
     * <ul>
     *     <li>CSRF deshabilitado.</li>
     *     <li>Cualquier request requiere estar autenticado.</li>
     *     <li>Se configura un servidor de recursos OAuth2 basado en JWT,
     *         usando un convertidor personalizado para extraer los roles.</li>
     * </ul>
     *
     * @param http objeto de configuracion HTTP de Spring Security
     * @return instancia construida de {@link SecurityFilterChain}
     * @throws Exception si ocurre algun problema al construir la configuracion
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
            );
        return http.build();
    }

    /**
     * Crea el convertidor de autenticacion JWT que se utiliza para mapear
     * los roles del token a autoridades de Spring Security.
     *
     * <p>Internamente usa {@link KeycloakRealmRoleConverter} para transformar
     * los roles del realm de Keycloak en instancias de {@link GrantedAuthority}.</p>
     *
     * @return convertidor de JWT a {@link AbstractAuthenticationToken}
     */
    private Converter<Jwt, AbstractAuthenticationToken> jwtAuthConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return converter;
    }
}

/**
 * Convertidor que extrae los roles del realm de Keycloak desde el token JWT
 * y los transforma en autoridades de Spring Security.
 *
 * <p>Lee el claim {@code realm_access.roles} del JWT, antepone el prefijo
 * {@code ROLE_} a cada rol y devuelve una lista de {@link SimpleGrantedAuthority}.</p>
 */
class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    /**
     * Convierte un token JWT en una coleccion de autoridades de Spring Security.
     *
     * <p>Si el claim {@code realm_access} no esta presente o esta vacio,
     * se devuelve una lista vacia.</p>
     *
     * @param jwt token JWT emitido por Keycloak
     * @return coleccion de {@link GrantedAuthority} basada en los roles del realm
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        final Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return List.of();
        }
        return ((List<String>) realmAccess.get("roles")).stream()
                .map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
