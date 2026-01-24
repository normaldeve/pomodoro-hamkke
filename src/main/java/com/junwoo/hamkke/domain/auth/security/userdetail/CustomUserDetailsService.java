package com.junwoo.hamkke.domain.auth.security.userdetail;

import com.junwoo.hamkke.domain.auth.dto.AuthDTO;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("입력하신 아이디로 사용자를 찾을 수 없습니다: " + username));

        AuthDTO userDTO = new AuthDTO(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getProfileUrl(),
                user.getRole()
        );

        return new CustomUserDetails(userDTO, user.getPassword());
    }
}
