package com.infonal.paydaybank.controller;

import com.infonal.paydaybank.config.JwtUtil;
import com.infonal.paydaybank.model.JwtResponse;
import com.infonal.paydaybank.model.LoginRequest;
import com.infonal.paydaybank.model.MessageResponse;
import com.infonal.paydaybank.model.RegisterRequest;
import com.infonal.paydaybank.model.User;
import com.infonal.paydaybank.repository.UserRepository;
import com.infonal.paydaybank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // Eğer email zaten kayıtlıysa hata döndür
            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse("Bu email adresi zaten kayıtlı."));
            }

            // Yeni kullanıcı oluştur
            User newUser = new User();
            newUser.setFullName(registerRequest.getFullname());
            newUser.setEmail(registerRequest.getEmail());
            newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Şifreyi hashle
            newUser.setTitle(registerRequest.getTitle());

            // Kullanıcıyı veritabanına kaydet
            userRepository.save(newUser);

            return ResponseEntity.ok(new MessageResponse("Kayıt başarılı!"));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Kayıt sırasında hata oluştu: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Veritabanından kullanıcıyı bul
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            // Şifre kontrolü (şifre hash karşılaştırma)
            // Login endpoint'inde
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Hatalı şifre"));
            }

            // JWT token oluştur
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword()) // Hashlenmiş şifre kullanılmalı
                    .authorities("ROLE_USER")
                    .build();

            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new JwtResponse(token, user.getFullName()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Giriş başarısız: " + e.getMessage()));
        }
    }
}
