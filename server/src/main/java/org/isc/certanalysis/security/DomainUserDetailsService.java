package org.isc.certanalysis.security;

import org.isc.certanalysis.domain.Role;
import org.isc.certanalysis.domain.User;
import org.isc.certanalysis.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * @author p.dzeviarylin
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public DomainUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		final String login = name.toLowerCase(Locale.ENGLISH);
		User user = userRepository.findOneWithRolesByLogin(name).orElseThrow(() -> new UsernameNotFoundException("User " + login + " was not found in the database"));

		return new org.springframework.security.core.userdetails.User(
				user.getLogin(), user.getPassword(), user.isEnabled(), true, true,
				true, getAuthorities(user.getRoles()));
	}

	private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
			role.getPrivileges().stream()
					.map(privilege -> new SimpleGrantedAuthority(privilege.getName()))
					.forEach(authorities::add);
		}

		return authorities;
	}


}
