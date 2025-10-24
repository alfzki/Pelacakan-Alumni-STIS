package com.stis.alumni.specification;

import com.stis.alumni.dto.institution.InstitutionSearchCriteria;
import com.stis.alumni.entity.Institution;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public final class InstitutionSpecifications {

    private InstitutionSpecifications() {
    }

    public static Specification<Institution> byCriteria(InstitutionSearchCriteria criteria) {
        return (root, query, cb) -> {
            if (criteria == null) {
                return cb.conjunction();
            }
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(criteria.getSearch())) {
                String pattern = "%" + criteria.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("province")), pattern),
                        cb.like(cb.lower(root.get("city")), pattern)
                ));
            }

            if (criteria.getType() != null) {
                predicates.add(cb.equal(root.get("type"), criteria.getType()));
            }

            if (StringUtils.hasText(criteria.getProvince())) {
                predicates.add(cb.equal(cb.lower(root.get("province")), criteria.getProvince().toLowerCase()));
            }

            if (StringUtils.hasText(criteria.getCity())) {
                predicates.add(cb.equal(cb.lower(root.get("city")), criteria.getCity().toLowerCase()));
            }

            if (query != null) {
                query.distinct(true);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
