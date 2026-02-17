package es.urjc.daw.equis.service;

import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.repository.UserRepository;

import org.springframework.stereotype.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        System.out.println("LOGIN ATTEMPT → " + email);
        System.out.println("USER FOUND → " + user.getEmail());
        System.out.println("PASSWORD DB → " + user.getEncodedPassword());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getEncodedPassword())
                .authorities(
                        user.getRoles().stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList()
                )
                .build();
    }
}
