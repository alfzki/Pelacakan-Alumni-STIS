package com.stis.alumni.specification;

import com.stis.alumni.dto.user.UserSearchCriteria;
import com.stis.alumni.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public final class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<User> byCriteria(UserSearchCriteria criteria) {
        return (root, query, cb) -> {
            if (criteria == null) {
                return cb.conjunction();
            }
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(criteria.getSearch())) {
                String pattern = "%" + criteria.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(cb.lower(root.get("fullName")), pattern),
                        cb.like(cb.lower(root.get("nim")), pattern)
                ));
            }

            if (criteria.getAngkatan() != null) {
                predicates.add(cb.equal(root.get("angkatan"), criteria.getAngkatan()));
            }

            if (StringUtils.hasText(criteria.getProgramStudi())) {
                predicates.add(cb.equal(cb.lower(root.get("programStudi")), criteria.getProgramStudi().toLowerCase()));
            }

            if (StringUtils.hasText(criteria.getJurusan())) {
                predicates.add(cb.equal(cb.lower(root.get("jurusan")), criteria.getJurusan().toLowerCase()));
            }

            if (criteria.getTahunLulus() != null) {
                predicates.add(cb.equal(root.get("tahunLulus"), criteria.getTahunLulus()));
            }

            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            }

            if (criteria.getRole() != null) {
                predicates.add(cb.equal(root.get("role"), criteria.getRole()));
            }

            if (query != null) {
                query.distinct(true);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
