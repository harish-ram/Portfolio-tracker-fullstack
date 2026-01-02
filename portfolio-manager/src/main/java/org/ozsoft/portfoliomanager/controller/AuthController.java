package org.ozsoft.portfoliomanager.controller;

import org.ozsoft.portfoliomanager.domain.User;
import org.ozsoft.portfoliomanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public ResponseEntity<?> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(new ErrorResponse("Not authenticated"));
        }

        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String googleId = oauth2User.getAttribute("sub");
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String picture = oauth2User.getAttribute("picture");

            User user = userService.saveOrUpdateUser(googleId, email, name, picture);
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("email", user.getEmail());
            userInfo.put("name", user.getName());
            userInfo.put("picture", user.getPicture());
            userInfo.put("sub", googleId);
            return ResponseEntity.ok(userInfo);
        }

        return ResponseEntity.status(401).body(new ErrorResponse("Invalid authentication"));
    }

    @GetMapping("/status")
    public ResponseEntity<?> getAuthStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            return ResponseEntity.ok(new AuthStatus(true, "Authenticated"));
        }
        
        return ResponseEntity.ok(new AuthStatus(false, "Not authenticated"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    public static class AuthStatus {
        public boolean authenticated;
        public String message;

        public AuthStatus(boolean authenticated, String message) {
            this.authenticated = authenticated;
            this.message = message;
        }

        public boolean isAuthenticated() {
            return authenticated;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class MessageResponse {
        public String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}
