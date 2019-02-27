package org.isc.certanalysis.service;

import org.isc.certanalysis.repository.UserRepository;
import org.isc.certanalysis.security.SecurityUtils;
import org.isc.certanalysis.service.dto.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public UserDTO getUserWithAuthorities() {
		return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithRolesByName).map(UserDTO::new)
				.orElseThrow(() -> new RuntimeException("User could not be found"));
	}
}
