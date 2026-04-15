package vn.com.go.routex.identity.security.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtService.extractAllClaims(token);

            // sub is the userId (subject)
            String userId = claims.getSubject();

            // New spec: access/refresh are distinguished by claims["type"] (legacy: claims["tokenType"]).
            // Refresh tokens must not authenticate regular API calls.
            String tokenType = jwtService.extractTokenType(claims);
            if ("refresh".equalsIgnoreCase(tokenType)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Principal: prefer email, fallback to userId if email is null/blank.
            String email = claims.get(JwtService.CLAIM_EMAIL, String.class);
            String principal = (email != null && !email.isBlank()) ? email : userId;

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();

                Object rolesClaim = claims.get("roles");
                if (rolesClaim instanceof Collection<?> roles) {
                    roles.stream()
                            .filter(Objects::nonNull)
                            .map(Object::toString)
                            .map(String::trim)
                            .filter(role -> !role.isBlank())
                            .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                            .map(SimpleGrantedAuthority::new)
                            .forEach(grantedAuthorities::add);
                }

                Object authoritiesClaim = claims.get("authorities");
                if (authoritiesClaim instanceof Collection<?> authorities) {
                    authorities.stream()
                            .filter(Objects::nonNull)
                            .map(Object::toString)
                            .map(String::trim)
                            .filter(auth -> !auth.isBlank())
                            .map(SimpleGrantedAuthority::new)
                            .forEach(grantedAuthorities::add);
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                grantedAuthorities
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ignored) {
        }

        filterChain.doFilter(request, response);
    }
}
