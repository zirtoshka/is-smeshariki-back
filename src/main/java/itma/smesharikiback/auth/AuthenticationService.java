package itma.smesharikiback.auth;

import itma.smesharikiback.models.Smesharik;
import itma.smesharikiback.models.SmesharikRole;
import itma.smesharikiback.requests.smesharik.SmesharikSignInRequest;
import itma.smesharikiback.requests.smesharik.SmesharikSignUpRequest;
import itma.smesharikiback.services.CommonService;
import itma.smesharikiback.services.SmesharikService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final CommonService commonService;
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
                .setColor(request.getColor())
                .setRole(SmesharikRole.USER);

        smesharikService.create(user);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt, "OK");
    }

    public JwtAuthenticationResponse signIn(SmesharikSignInRequest request) {
        System.out.println(new UsernamePasswordAuthenticationToken(
                request.getLogin(),
                request.getPassword()
        ));
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
