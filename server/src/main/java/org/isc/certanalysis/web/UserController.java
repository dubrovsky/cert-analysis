package org.isc.certanalysis.web;

import org.isc.certanalysis.service.UserService;
import org.isc.certanalysis.service.dto.CurrentUserDTO;
import org.isc.certanalysis.service.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

	@GetMapping("/user/account")
	public ResponseEntity<CurrentUserDTO> getAccount() {
		return ResponseEntity.ok().body(userService.getUserWithAuthorities());
	}
}
