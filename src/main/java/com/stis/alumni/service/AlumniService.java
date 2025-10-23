package com.stis.alumni.service;

import com.stis.alumni.dto.PageResponse;
import com.stis.alumni.dto.user.UserListItemResponse;
import com.stis.alumni.dto.user.UserProfileResponse;
import com.stis.alumni.dto.user.UserSearchCriteria;
import com.stis.alumni.entity.User;
import com.stis.alumni.exception.ResourceNotFoundException;
import com.stis.alumni.mapper.UserMapper;
import com.stis.alumni.repository.UserRepository;
import com.stis.alumni.specification.UserSpecifications;
import com.stis.alumni.util.PageUtils;
import com.stis.alumni.exception.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlumniService {

    private final UserRepository userRepository;

    public AlumniService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<UserListItemResponse> getAlumniList(int page, int size, String sortBy, String sortDirection, UserSearchCriteria criteria) {
        String sortProperty = StringUtils.hasText(sortBy) ? sortBy : "fullName";
        Sort.Direction direction = Sort.Direction.ASC;
        if (StringUtils.hasText(sortDirection)) {
            try {
                direction = Sort.Direction.fromString(sortDirection);
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Invalid sort direction: " + sortDirection);
            }
        }
        Sort sort = Sort.by(direction, sortProperty);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> result = userRepository.findAll(UserSpecifications.byCriteria(criteria), pageable);
        Page<UserListItemResponse> mapped = result.map(UserMapper::toListItem);
        return PageUtils.from(mapped);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getAlumniDetail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni not found with id: " + id));
        return UserMapper.toProfile(user);
    }
}
