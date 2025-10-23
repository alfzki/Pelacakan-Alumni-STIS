package com.stis.alumni.service;

import com.stis.alumni.config.JwtProperties;
import com.stis.alumni.dto.auth.AuthLoginRequest;
import com.stis.alumni.dto.auth.AuthLoginResponse;
import com.stis.alumni.dto.auth.AuthRegisterRequest;
import com.stis.alumni.dto.auth.AuthRegisterResponse;
import com.stis.alumni.dto.user.UserSummaryResponse;
import com.stis.alumni.entity.User;
import com.stis.alumni.enums.UserStatus;
import com.stis.alumni.exception.BadRequestException;
import com.stis.alumni.exception.ConflictException;
import com.stis.alumni.mapper.UserMapper;
import com.stis.alumni.repository.UserRepository;
import com.stis.alumni.security.JwtTokenProvider;
import com.stis.alumni.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public AuthRegisterResponse register(AuthRegisterRequest request) {
        validateRegisterRequest(request);
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = UserMapper.toEntity(request, encodedPassword);
        user.setStatus(UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);
        return UserMapper.toRegisterResponse(savedUser);
    }

    public AuthLoginResponse login(AuthLoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(principal);
        long expiresIn = jwtProperties.getExpirationSeconds();
        UserSummaryResponse userSummary = new UserSummaryResponse(
                principal.getId(),
                principal.getUsername(),
                principal.getEmail(),
                principal.getFullName(),
                principal.getRole()
        );
        return new AuthLoginResponse(token, "Bearer", expiresIn, userSummary);
    }

    private void validateRegisterRequest(AuthRegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password confirmation does not match");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }
        if (request.getNim() != null && userRepository.existsByNim(request.getNim())) {
            throw new ConflictException("NIM already registered");
        }
    }
}
