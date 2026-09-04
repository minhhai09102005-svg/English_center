package com.example.english_center.specification;

import com.example.english_center.builder.UserAccountSearchBuilder;
import com.example.english_center.entity.Users;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserAccountSpecs {

    public static Specification<Users> byBuilder(UserAccountSearchBuilder builder) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (builder.getKeyword() != null && !builder.getKeyword().isBlank()) {
                String keyword = "%" + builder.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("fullname")), keyword),
                        cb.like(cb.lower(root.get("email")), keyword),
                        cb.like(root.get("phoneNumber"), keyword)
                ));
            }

            if (builder.getRole() != null) {
                predicates.add(cb.equal(root.get("role"), builder.getRole()));
            }

            if (builder.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), builder.getStatus()));
            }

            if (builder.getGender() != null) {
                predicates.add(cb.equal(root.get("gender"), builder.getGender()));
            }

            return predicates.isEmpty()
                    ? cb.conjunction()
                    : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
