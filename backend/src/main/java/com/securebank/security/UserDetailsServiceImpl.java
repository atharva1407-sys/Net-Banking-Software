package com.securebank.security;

import com.securebank.customer.entity.UserCredential;
import com.securebank.customer.repository.UserCredentialRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserCredentialRepository userCredentialRepository;

    public UserDetailsServiceImpl(UserCredentialRepository userCredentialRepository) {
        this.userCredentialRepository = userCredentialRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserCredential credential = userCredentialRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with Customer ID: " + username));

        return new User(
                credential.getUsername(),
                credential.getPasswordHash(),
                credential.isEnabled(),
                true,
                true,
                credential.getLockedUntil() == null || credential.getLockedUntil().isBefore(java.time.LocalDateTime.now()),
                Collections.singletonList(new SimpleGrantedAuthority(credential.getRole()))
        );
    }
}
