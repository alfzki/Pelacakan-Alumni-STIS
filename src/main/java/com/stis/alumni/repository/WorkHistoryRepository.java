package com.stis.alumni.repository;

import com.stis.alumni.entity.WorkHistory;
import com.stis.alumni.enums.EmploymentType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkHistoryRepository extends JpaRepository<WorkHistory, Long>, JpaSpecificationExecutor<WorkHistory> {

    List<WorkHistory> findByUserIdOrderByStartDateDesc(Long userId);

    Optional<WorkHistory> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndCurrentJobTrue(Long userId);

    long countByEmploymentType(EmploymentType employmentType);

    long countByCurrentJobTrue();

    @Query("SELECT COUNT(DISTINCT wh.user.id) FROM WorkHistory wh WHERE wh.institution.id = :institutionId")
    long countDistinctUserByInstitution(@Param("institutionId") Long institutionId);

    List<WorkHistory> findByInstitutionIdAndCurrentJobTrueOrderByStartDateDesc(Long institutionId);

    @Query("SELECT COUNT(DISTINCT wh.user.id) FROM WorkHistory wh WHERE wh.currentJob = true")
    long countDistinctUserWithCurrentJob();

    @Query("SELECT wh.employmentType, COUNT(DISTINCT wh.user.id) FROM WorkHistory wh WHERE wh.currentJob = true GROUP BY wh.employmentType")
    List<Object[]> countCurrentEmploymentDistribution();

    @Query("SELECT wh.institution.type, COUNT(DISTINCT wh.user.id) FROM WorkHistory wh WHERE wh.currentJob = true GROUP BY wh.institution.type")
    List<Object[]> countCurrentInstitutionTypeDistribution();

    @Query("SELECT wh.institution, COUNT(DISTINCT wh.user.id) FROM WorkHistory wh WHERE wh.currentJob = true GROUP BY wh.institution ORDER BY COUNT(DISTINCT wh.user.id) DESC")
    List<Object[]> findTopInstitutions(Pageable pageable);

    @Query("SELECT wh.institution.province, wh.institution.city, COUNT(DISTINCT wh.user.id) FROM WorkHistory wh WHERE wh.currentJob = true GROUP BY wh.institution.province, wh.institution.city ORDER BY COUNT(DISTINCT wh.user.id) DESC")
    List<Object[]> findTopLocations(Pageable pageable);

    @Query("SELECT wh.institution.province, COUNT(DISTINCT wh.user.id) FROM WorkHistory wh WHERE wh.currentJob = true GROUP BY wh.institution.province")
    List<Object[]> countCurrentByProvince();

    @Query("SELECT wh.institution.name, COUNT(DISTINCT wh.user.id) FROM WorkHistory wh WHERE wh.currentJob = true GROUP BY wh.institution.name")
    List<Object[]> countCurrentByInstitution();
}
