package itma.smesharikiback.auth;

import itma.smesharikiback.models.Smesharik;
import itma.smesharikiback.models.SmesharikRole;
import itma.smesharikiback.services.SmesharikService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final SmesharikService smesharikService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse signUp(SmesharikSignUpRequest request) {

        var user = new Smesharik()
                .setName(request.getName())
                .setPassword(passwordEncoder.encode(request.getPassword()))
                .setEmail(request.getEmail())
                .setLogin(request.getLogin())
                .setRole(SmesharikRole.USER);

        try {
            smesharikService.create(user);
        } catch (Exception e) {
            return new JwtAuthenticationResponse("", e.getMessage());
        }

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt, "OK");
    }

    public JwtAuthenticationResponse signIn(SmesharikSignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getLogin(),
                request.getPassword()
        ));

        var user = smesharikService
                .getByLogin(request.getLogin());


        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt, "OK");
    }
}