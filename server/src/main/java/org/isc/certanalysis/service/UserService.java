package org.isc.certanalysis.service;

import org.hibernate.Hibernate;
import org.isc.certanalysis.domain.Role;
import org.isc.certanalysis.domain.User;
import org.isc.certanalysis.repository.NotificationGroupRepository;
import org.isc.certanalysis.repository.RoleRepository;
import org.isc.certanalysis.repository.UserRepository;
import org.isc.certanalysis.security.SecurityUtils;
import org.isc.certanalysis.service.bean.dto.CurrentUserDTO;
import org.isc.certanalysis.service.bean.dto.UserDTO;
import org.isc.certanalysis.service.mapper.Mapper;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final NotificationGroupRepository notificationGroupRepository;
	private final PasswordEncoder passwordEncoder;
	private final CacheManager cacheManager;
	private final Mapper mapper;

	public UserService(UserRepository userRepository, RoleRepository roleRepository, NotificationGroupRepository notificationGroupRepository, PasswordEncoder passwordEncoder, CacheManager cacheManager, Mapper mapper) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.notificationGroupRepository = notificationGroupRepository;
		this.passwordEncoder = passwordEncoder;
		this.cacheManager = cacheManager;
		this.mapper = mapper;
	}

	@Transactional(readOnly = true)
	public List<UserDTO> findAll() {
		return mapper.mapAsList(userRepository.findAll(), UserDTO.class);
	}

	@Transactional(readOnly = true)
	public UserDTO findUser(Long id) {
		final User user = userRepository.findOneWithRolesById(id).orElseThrow(() -> new RuntimeException("User record not found"));
		Hibernate.initialize(user.getNotificationGroups());
		return userToDTO(user);
	}

	private UserDTO userToDTO(User user) {
		final UserDTO userDTO = mapper.map(user, UserDTO.class);
		if(!user.getRoles().isEmpty()){
			userDTO.setRoleId(user.getRoles().iterator().next().getId());
		}
		user.getNotificationGroups().forEach(notificationGroup -> userDTO.getNotificationGroupIds().add(notificationGroup.getId()));
		return userDTO;
	}

	@Transactional(readOnly = true)
	public CurrentUserDTO getUserWithAuthorities() {
		return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithRolesByLogin).map(CurrentUserDTO::new)
				.orElseThrow(() -> new RuntimeException("User could not be found"));
	}

	@Transactional(readOnly = true)
	public List<Role> findAllRoles() {
		return roleRepository.findAll();
	}

	public UserDTO create(UserDTO userDTO) {
		User user = mapper.map(userDTO, User.class);
		for (Long id : userDTO.getNotificationGroupIds()) {
			user.addNotificationGroup(notificationGroupRepository.getOne(id));
		}
		user.addRole(roleRepository.getOne(userDTO.getRoleId()));
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		user = userRepository.save(user);
		return userToDTO(user);
	}

	public UserDTO save(UserDTO userDTO) {
		User user = userRepository.findOneWithRolesById(userDTO.getId()).orElseThrow(() -> new RuntimeException("User record not found"));
		Hibernate.initialize(user.getNotificationGroups());
		mapper.map(userDTO, user);

		user.removeNotificationGroups();
		for (Long id : userDTO.getNotificationGroupIds()) {
			user.addNotificationGroup(notificationGroupRepository.getOne(id));
		}

		user.removeRoles();
		user.addRole(roleRepository.getOne(userDTO.getRoleId()));

		user = userRepository.save(user);
		clearUserCaches(user);
		return userToDTO(user);
	}

	public void deleteById(Long id) {
		userRepository.deleteById(id);
	}

	private void clearUserCaches(User user) {
//		Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_ID_CACHE)).evict(user.getId());
		Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
	}
}
