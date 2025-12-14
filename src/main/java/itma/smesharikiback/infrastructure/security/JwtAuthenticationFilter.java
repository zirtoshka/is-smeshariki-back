package itma.smesharikiback.infrastructure.security;

import io.jsonwebtoken.JwtException;
import itma.smesharikiback.domain.model.Smesharik;
import itma.smesharikiback.domain.repository.SmesharikRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private static final String TOKEN_COOKIE = "token";
    private final JwtService jwtService;
    private final SmesharikRepository smesharikRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (request.getServletPath().startsWith("/auth")) {
            log.debug("Skip JWT filter for auth endpoint {}", request.getServletPath());
            filterChain.doFilter(request, response);
            return;
        }

        if ("OPTIONS".equals(request.getMethod())) {
            log.trace("Skip JWT filter for OPTIONS {}", request.getServletPath());
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = resolveToken(request);
        if (jwt == null || jwt.isBlank()) {
            log.debug("No JWT token found for path {}", request.getServletPath());
            filterChain.doFilter(request, response);
            return;
        }

        String login;
        try {
            login = jwtService.extractLogin(jwt);
        } catch (JwtException e) {
            log.warn("Failed to extract login from JWT: {}", e.getMessage());
            clearTokenCookie(response);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        if (!login.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<Smesharik> userOpt = smesharikRepository
                    .findByLogin(login);

            if (userOpt.isEmpty()) {
                log.warn("User not found for login {}", login);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Smesharik user = userOpt.get();

            if (jwtService.isTokenValid(jwt, user)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );
                user.setIsOnline(true);
                smesharikRepository.save(user);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
                log.debug("Authenticated user login={} via JWT", login);
            } else {
                log.warn("JWT is not valid for login {}", login);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void clearTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HEADER_NAME);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> TOKEN_COOKIE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}













