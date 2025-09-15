package com.intellicart.controller;

import com.intellicart.model.Role;
import com.intellicart.model.User;
import com.intellicart.repository.UserRepository;
import com.intellicart.service.auth.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
        }
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole() == null ? Role.CUSTOMER : req.getRole());
        user.setFullName(req.getFullName() == null ? req.getEmail() : req.getFullName());
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User created"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
            var user = userRepository.findByEmail(req.getEmail()).get();
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }

    public static class SignupRequest {
        private String email;
        private String password;
        private String fullName;
        private Role role;

        public String getEmail(){ return email; }
        public void setEmail(String email){ this.email = email; }
        public String getPassword(){ return password; }
        public void setPassword(String password){ this.password = password; }
        public String getFullName(){ return fullName; }
        public void setFullName(String fullName){ this.fullName = fullName; }
        public Role getRole(){ return role; }
        public void setRole(Role role){ this.role = role; }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail(){ return email; }
        public void setEmail(String email){ this.email = email; }
        public String getPassword(){ return password; }
        public void setPassword(String password){ this.password = password; }
    }
}
