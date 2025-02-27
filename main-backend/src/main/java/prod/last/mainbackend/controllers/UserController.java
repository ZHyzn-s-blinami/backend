package prod.last.mainbackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prod.last.mainbackend.configurations.jwt.JwtUtils;
import prod.last.mainbackend.models.UserModel;
import prod.last.mainbackend.models.request.LoginRequest;
import prod.last.mainbackend.models.request.RegisterRequest;
import prod.last.mainbackend.models.response.JwtResponse;
import prod.last.mainbackend.services.TokenService;
import prod.last.mainbackend.services.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        UserModel userModel = userService.createUser(request.getLogin(), request.getEmail(), request.getPassword());

        tokenService.updateTokenUUID(userModel);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userModel.getId().toString(),
                        request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication, userModel.getTokenUUID());

        return ResponseEntity.status(HttpStatus.CREATED).body(new JwtResponse(jwt));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {

        UserModel user = userService.getUserByEmail(loginRequest.getEmail());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getId().toString(),
                        loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        tokenService.updateTokenUUID(user);

        String jwt = jwtUtils.generateJwtToken(authentication, user.getTokenUUID());

        return ResponseEntity.ok(new JwtResponse(jwt));
    }
}
