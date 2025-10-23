package com.stis.alumni.service;

import com.stis.alumni.dto.user.AccountDeletionRequest;
import com.stis.alumni.dto.user.ChangePasswordRequest;
import com.stis.alumni.dto.user.UserProfileResponse;
import com.stis.alumni.dto.user.UserUpdateRequest;
import com.stis.alumni.entity.User;
import com.stis.alumni.enums.UserStatus;
import com.stis.alumni.exception.BadRequestException;
import com.stis.alumni.exception.ResourceNotFoundException;
import com.stis.alumni.exception.UnauthorizedException;
import com.stis.alumni.mapper.UserMapper;
import com.stis.alumni.repository.UserRepository;
import com.stis.alumni.util.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final String DELETE_CONFIRMATION = "DELETE";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentProfile() {
        User user = getCurrentUser();
        return UserMapper.toProfile(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(UserUpdateRequest request) {
        User user = getCurrentUser();
        UserMapper.applyUpdate(user, request);
        return UserMapper.toProfile(userRepository.save(user));
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BadRequestException("New password confirmation does not match");
        }
        User user = getCurrentUser();
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(AccountDeletionRequest request) {
        if (!DELETE_CONFIRMATION.equals(request.getConfirmation())) {
            throw new BadRequestException("Confirmation text is invalid");
        }
        User user = getCurrentUser();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Password confirmation failed");
        }
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    private User getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow(() -> new UnauthorizedException("User not authenticated"));
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}
