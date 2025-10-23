package com.stis.alumni.repository;

import com.stis.alumni.entity.Institution;
import com.stis.alumni.enums.InstitutionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface InstitutionRepository extends JpaRepository<Institution, Long>, JpaSpecificationExecutor<Institution> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Institution> findByNameIgnoreCase(String name);

    long countByType(InstitutionType type);
}
