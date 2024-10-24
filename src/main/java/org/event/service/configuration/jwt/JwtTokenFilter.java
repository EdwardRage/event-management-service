package org.event.service.configuration.jwt;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.event.service.user.User;
import org.event.service.user.UserEntityConverter;
import org.event.service.user.UserRepository;
import org.event.service.user.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenManager jwtTokenManager;
    private final UserRepository userRepository;
    private final UserEntityConverter entityConverter;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null ||
            !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        var jwtToken = authorizationHeader.substring(7);

        String login;
        try {
            login = jwtTokenManager.getLoginFromJwt(jwtToken);
        } catch (Exception e) {
            log.error("Error while reading jwt", e);
            filterChain.doFilter(request, response);
            return;
        }

        var userEntity = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        var user = entityConverter.toDomain(userEntity);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority(user.role().toString()))
        );

        SecurityContextHolder.getContext().setAuthentication(token);

        filterChain.doFilter(request, response);
    }
}
