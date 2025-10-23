package com.stis.alumni.repository;

import com.stis.alumni.entity.User;
import com.stis.alumni.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByNim(String nim);

    long countByStatus(UserStatus status);

    @Query("SELECT u.angkatan, COUNT(u) FROM User u GROUP BY u.angkatan ORDER BY u.angkatan")
    List<Object[]> countByAngkatan();
}
