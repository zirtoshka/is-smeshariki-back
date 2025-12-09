package itma.smesharikiback.presentation.controller;

import itma.smesharikiback.application.service.AuthenticationService;
import itma.smesharikiback.presentation.dto.request.smesharik.SmesharikSignInRequest;
import itma.smesharikiback.presentation.dto.request.smesharik.SmesharikSignUpRequest;
import itma.smesharikiback.presentation.dto.response.JwtAuthenticationResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    private static final String TOKEN_COOKIE = "token";
    private static final Duration TOKEN_TTL = Duration.ofDays(7);

    @PostMapping("/signup")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SmesharikSignUpRequest request,
                                            HttpServletResponse response) {
        JwtAuthenticationResponse authResponse = authenticationService.signUp(request);
        writeAuthCookie(response, authResponse.getToken(), TOKEN_TTL.getSeconds());
        return authResponse;
    }

    @PostMapping("/signin")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SmesharikSignInRequest request,
                                            HttpServletResponse response) {
        JwtAuthenticationResponse authResponse = authenticationService.signIn(request);
        writeAuthCookie(response, authResponse.getToken(), TOKEN_TTL.getSeconds());
        return authResponse;
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        writeAuthCookie(response, "", 0);
    }

    private void writeAuthCookie(HttpServletResponse response, String token, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(TOKEN_COOKIE, token == null ? "" : token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}













