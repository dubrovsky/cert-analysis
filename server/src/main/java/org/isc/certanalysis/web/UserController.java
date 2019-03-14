package org.isc.certanalysis.web;

import org.isc.certanalysis.domain.Role;
import org.isc.certanalysis.service.UserService;
import org.isc.certanalysis.service.dto.CurrentUserDTO;
import org.isc.certanalysis.service.dto.SchemeDTO;
import org.isc.certanalysis.service.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author p.dzeviarylin
 */
@RestController
@RequestMapping("/api")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/users")
	public ResponseEntity<List<UserDTO>> getAll() {
		final List<UserDTO> users = userService.findAll();
		return ResponseEntity.ok().body(users);
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
		final UserDTO userDTO = userService.findUser(id);
		return ResponseEntity.ok().body(userDTO);
	}

	@PostMapping("/user")
	public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
		UserDTO result = userService.create(userDTO);
		return ResponseEntity.ok().body(result);
	}

	@PutMapping("/user")
	public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
		UserDTO result = userService.save(userDTO);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/user/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteById(id);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/user/account")
	public ResponseEntity<CurrentUserDTO> getAccount() {
		return ResponseEntity.ok().body(userService.getUserWithAuthorities());
	}

	@GetMapping("/roles")
	public ResponseEntity<List<Role>> getAllRoles() {
		return ResponseEntity.ok().body(userService.findAllRoles());
	}
}
