package es.urjc.daw.equis.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.repository.UserRepository;


@Service
public class RepositoryUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {

		User user = userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return new org.springframework.security.core.userdetails.User(
			user.getEmail(),
			user.getEncodedPassword(),
			user.getRoles().stream()
				.map(SimpleGrantedAuthority::new)
				.toList()
		);
	}
}
