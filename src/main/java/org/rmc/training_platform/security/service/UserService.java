package org.rmc.training_platform.security.service;

import lombok.RequiredArgsConstructor;
import org.rmc.training_platform.security.domain.UserApp;
import org.rmc.training_platform.security.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserApp userApp = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        userApp.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getRole().getName())));

        return new User(userApp.getUsername(), userApp.getPassword(), authorities);
    }

}
