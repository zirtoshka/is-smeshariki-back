package itma.smesharikiback.application.service;

import itma.smesharikiback.domain.model.Smesharik;
import itma.smesharikiback.domain.model.SmesharikRole;
import itma.smesharikiback.domain.repository.SmesharikRepository;
import itma.smesharikiback.presentation.dto.request.smesharik.SmesharikSignInRequest;
import itma.smesharikiback.presentation.dto.request.smesharik.SmesharikSignUpRequest;
import itma.smesharikiback.presentation.dto.response.JwtAuthenticationResponse;
import itma.smesharikiback.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final CommonService commonService;
    private final SmesharikService smesharikService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SmesharikRepository smesharikRepository;

    public JwtAuthenticationResponse signUp(SmesharikSignUpRequest request) {

        var user = new Smesharik()
                .setName(request.getName())
                .setPassword(passwordEncoder.encode(request.getPassword()))
                .setEmail(request.getEmail())
                .setLogin(request.getLogin())
                .setColor(request.getColor())
                .setRole(SmesharikRole.USER);

        smesharikService.create(user);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt, "OK");
    }

    public JwtAuthenticationResponse signIn(SmesharikSignInRequest request) {
        log.info("Authenticating user login={}", request.getLogin());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getLogin(),
                request.getPassword()
        ));

        var user = commonService
                .getByLogin(request.getLogin());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt, "OK");
    }
}













