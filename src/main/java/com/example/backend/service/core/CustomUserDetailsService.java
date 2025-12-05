package com.example.backend.service.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.backend.entity.User;
import com.example.backend.repository.IUserRepository;
import com.example.backend.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserRepository _userRepository;

    // DÙNG CHO LOGIN
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = _userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return new CustomUserDetails(user);
    }

    // DÙNG CHO JWT AUTH FILTER
    public UserDetails loadUserById(Integer id) throws UsernameNotFoundException {
        User user = _userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found id=" + id));

        return new CustomUserDetails(user);
    }
}
