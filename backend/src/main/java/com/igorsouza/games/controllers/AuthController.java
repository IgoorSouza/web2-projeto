package com.igorsouza.games.controllers;

import com.igorsouza.games.dtos.auth.Login;
import com.igorsouza.games.dtos.auth.LoginResponse;
import com.igorsouza.games.dtos.auth.NewUser;
import com.igorsouza.games.exceptions.BadRequestException;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.exceptions.UnauthorizedException;
import com.igorsouza.games.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody NewUser newUser)
            throws ConflictException {
        authService.register(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("User successfully registered.");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Login login)
            throws NotFoundException, BadRequestException {
        LoginResponse authData = authService.login(login);
        return ResponseEntity.ok(authData);
    }

    @PostMapping("/request-verification")
    public ResponseEntity<String> requestVerification()
            throws UnauthorizedException, ConflictException {
        authService.requestVerification();
        return ResponseEntity.ok("Verification email successfully sent.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> requestVerification(@RequestParam String token)
            throws UnauthorizedException, ConflictException {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email successfully verified.");
    }
}
