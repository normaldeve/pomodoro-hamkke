package com.junwoo.hamkke.domain.notification.controller;

import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import com.junwoo.hamkke.domain.dial.dto.RegisterTokenRequest;
import com.junwoo.hamkke.domain.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 12.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/register-token")
    public ResponseEntity<Void> registerToken(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RegisterTokenRequest request
    ) {
        Long userId = userDetails.getUser().id();
        notificationService.registerToken(userId, request.fcmToken(), request.deviceType());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-token")
    public ResponseEntity<Void> deactivateToken(
            @RequestParam String fcmToken
    ) {
        notificationService.deactivateToken(fcmToken);
        return ResponseEntity.noContent().build();
    }
}
