package com.assignment.AI_Powered.Document.Multimedia.Q.A.security;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.User;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Optional<User> userByUsername = userRepository.findByUsername(identifier);
        if (userByUsername.isPresent()) return userByUsername.get();

        throw new UsernameNotFoundException("No user or admin found with identifier: " + identifier);
    }
}
