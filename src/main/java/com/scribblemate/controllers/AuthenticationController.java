package com.scribblemate.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scribblemate.dto.LoginDto;
import com.scribblemate.dto.RegistrationDto;
import com.scribblemate.entities.User;
import com.scribblemate.exceptions.users.UserInactiveException;
import com.scribblemate.responses.LoginResponse;
import com.scribblemate.services.AuthenticationService;
import com.scribblemate.services.JwtAuthenticationService;
import com.scribblemate.utility.Utils.Status;

@RequestMapping("/auth")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthenticationController {
	private final JwtAuthenticationService jwtService;

	private final AuthenticationService authenticationService;

	public AuthenticationController(JwtAuthenticationService jwtService, AuthenticationService authenticationService) {
		this.jwtService = jwtService;
		this.authenticationService = authenticationService;
	}

	@PostMapping("/signup")
	public ResponseEntity<User> register(@RequestBody RegistrationDto registerDto) {
		User registeredUser = authenticationService.signUp(registerDto);
		return ResponseEntity.ok(registeredUser);
	}

	@PostMapping("/forgot")
	public ResponseEntity<Boolean> forgotPassword(@RequestParam String email) {
		boolean isSent = authenticationService.forgot(email);
		return ResponseEntity.ok(isSent);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginDto loginUserDto) {
		User authenticatedUser = authenticationService.authenticate(loginUserDto);
		if (authenticatedUser.getStatus().equals(Status.INACTIVE))
			throw new UserInactiveException();
		String jwtToken = jwtService.generateToken(authenticatedUser);
		LoginResponse loginResponse = new LoginResponse().setToken(jwtToken)
				.setExpiresIn(jwtService.getExpirationTime());

		return ResponseEntity.ok(loginResponse);
	}

}