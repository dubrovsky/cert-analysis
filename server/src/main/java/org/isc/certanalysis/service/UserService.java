package org.isc.certanalysis.service;

import org.isc.certanalysis.repository.UserRepository;
import org.isc.certanalysis.security.SecurityUtils;
import org.isc.certanalysis.service.dto.CurrentUserDTO;
import org.isc.certanalysis.service.dto.UserDTO;
import org.isc.certanalysis.service.mapper.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;
	private final Mapper mapper;

	public UserService(UserRepository userRepository, Mapper mapper) {
		this.userRepository = userRepository;
		this.mapper = mapper;
	}

	@Transactional(readOnly = true)
	public List<UserDTO> findAll() {
		return mapper.mapAsList(userRepository.findAll(), UserDTO.class);
	}

	@Transactional(readOnly = true)
	public CurrentUserDTO getUserWithAuthorities() {
		return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithRolesByLogin).map(CurrentUserDTO::new)
				.orElseThrow(() -> new RuntimeException("User could not be found"));
	}
}
