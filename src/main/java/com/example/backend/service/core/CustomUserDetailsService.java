package com.example.backend.service.core;

import com.example.backend.entity.User;
import com.example.backend.repository.IUserRepository;
import com.example.backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserRepository _userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = _userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found !!!"));
        return new CustomUserDetails(user);
    }
}
