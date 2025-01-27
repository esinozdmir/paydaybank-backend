package com.infonal.paydaybank.controller;

import com.infonal.paydaybank.model.User;
import com.infonal.paydaybank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.getProfile(email);

            // Hassas bilgileri çıkar
            user.setPassword(null);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Profil bilgileri alınırken bir hata oluştu: " + e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @RequestBody User updatedUser
    ) {
        try {
            String email = authentication.getName();
            User user = userService.updateProfile(email, updatedUser);

            // Hassas bilgileri çıkar
            user.setPassword(null);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Profil güncellenirken bir hata oluştu: " + e.getMessage());
        }
    }
}