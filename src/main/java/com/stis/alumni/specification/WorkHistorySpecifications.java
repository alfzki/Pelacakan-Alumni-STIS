package com.stis.alumni.specification;

import com.stis.alumni.dto.workhistory.WorkHistorySearchCriteria;
import com.stis.alumni.entity.Institution;
import com.stis.alumni.entity.WorkHistory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public final class WorkHistorySpecifications {

    private WorkHistorySpecifications() {
    }

    public static Specification<WorkHistory> byCriteria(WorkHistorySearchCriteria criteria) {
        return (root, query, cb) -> {
            if (criteria == null) {
                return cb.conjunction();
            }
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getUserId() != null) {
                predicates.add(cb.equal(root.get("user").get("id"), criteria.getUserId()));
            }

            if (criteria.getInstitutionId() != null) {
                predicates.add(cb.equal(root.get("institution").get("id"), criteria.getInstitutionId()));
            }

            if (criteria.getEmploymentType() != null) {
                predicates.add(cb.equal(root.get("employmentType"), criteria.getEmploymentType()));
            }

            if (criteria.getCurrentJob() != null) {
                predicates.add(cb.equal(root.get("currentJob"), criteria.getCurrentJob()));
            }

            Join<WorkHistory, Institution> institutionJoin = null;
            if (StringUtils.hasText(criteria.getProvince())) {
                if (institutionJoin == null) {
                    institutionJoin = root.join("institution", JoinType.LEFT);
                }
                predicates.add(cb.equal(cb.lower(institutionJoin.get("province")), criteria.getProvince().toLowerCase()));
            }

            if (StringUtils.hasText(criteria.getCity())) {
                if (institutionJoin == null) {
                    institutionJoin = root.join("institution", JoinType.LEFT);
                }
                predicates.add(cb.equal(cb.lower(institutionJoin.get("city")), criteria.getCity().toLowerCase()));
            }

            if (query != null) {
                query.distinct(true);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
