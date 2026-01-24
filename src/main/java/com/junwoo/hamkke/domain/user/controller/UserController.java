package com.junwoo.hamkke.domain.user.controller;

import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import com.junwoo.hamkke.domain.user.dto.SignupRequest;
import com.junwoo.hamkke.domain.user.dto.SignupResponse;
import com.junwoo.hamkke.domain.user.dto.UserSearchResponse;
import com.junwoo.hamkke.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
            @Validated @RequestBody SignupRequest request
    ) {

        SignupResponse response = userService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UserSearchResponse>> searchUsers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String keyword
    ) {
        Long myId = userDetails.getUser().id();

        List<UserSearchResponse> result = userService.searchUsers(myId, keyword);

        return ResponseEntity.ok(result);
    }

    @PatchMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("file") MultipartFile file
    ) {
        Long userId = userDetails.getUser().id();
        String newImageUrl = userService.updateProfile(userId, file);

        return ResponseEntity.ok(newImageUrl);
    }
}