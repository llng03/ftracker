package de.ftracker;

import de.ftracker.domain.model.AppUser;
import de.ftracker.services.JwtService;
import de.ftracker.services.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        //already authentificated - do nothing
        if (SecurityContextHolder.getContext().getAuthentication() != null){
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(header == null || !header.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        if(!jwtService.validate(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = jwtService.getUserId(token);

        AppUser user = userRepository.findById(userId).orElse(null);
        if(user ==null) {
            filterChain.doFilter(request, response);
            return;
        }

        //optional save demo doubled
        if(Boolean.TRUE.equals(user.isDemo())
            && user.getExpiresAt() != null
            && user.getExpiresAt().isBefore(LocalDateTime.now())) {
            filterChain.doFilter(request, response);
            return;
        }

        var authorities = List.of(new SimpleGrantedAuthority(
                Boolean.TRUE.equals(user.isDemo()) ? "ROLE_DEMO" : "ROLE_USER"

        ));

        var auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
