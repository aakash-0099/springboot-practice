package com.example.usermanagement.service;

import com.example.usermanagement.dto.CreateUserRequest;
import com.example.usermanagement.dto.UserResponse;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.mapper.UserMapper;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.dto.auth.LoginRequest;
import com.example.usermanagement.dto.auth.LoginResponse;
import com.example.usermanagement.dto.auth.RegisterRequest;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.security.JwtService;
import com.example.usermanagement.exception.EmailAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
		UserRepository userRepository,
		PasswordEncoder passwordEncoder,
			JwtService jwtService
	) {
			this.userRepository = userRepository;
			this.passwordEncoder = passwordEncoder;
			this.jwtService = jwtService;
	}

    public UserResponse register(RegisterRequest request) {

			if (userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException(
					"Email already registered"
			);
			}

			String encodedPassword =
					passwordEncoder.encode(request.getPassword());

			User user = new User(
					null,
					request.getName(),
					request.getEmail(),
					encodedPassword,
					Role.USER,
					true
			);

			User savedUser = userRepository.save(user);

			return new UserResponse(
					savedUser.getId(),
					savedUser.getName(),
					savedUser.getEmail(),
					savedUser.getRole(),
					savedUser.isEnabled()
			);
	}

    public LoginResponse login(LoginRequest request) {

		User user = userRepository
				.findByEmail(request.getEmail())
				.orElseThrow(() ->
						new RuntimeException("Invalid email or password")
				);

		if (!user.isEnabled()) {
				throw new RuntimeException("User account is disabled");
		}

		boolean passwordMatches =
				passwordEncoder.matches(
						request.getPassword(),
						user.getPassword()
				);

		if (!passwordMatches) {
				throw new RuntimeException("Invalid email or password");
		}
		String accessToken = jwtService.generateToken(user);

		return new LoginResponse(accessToken);
	}
}