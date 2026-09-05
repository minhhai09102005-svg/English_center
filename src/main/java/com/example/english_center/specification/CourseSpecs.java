package com.example.english_center.specification;

import com.example.english_center.builder.CourseSearchBuilder;
import com.example.english_center.entity.Course;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CourseSpecs {

    public static Specification<Course> byBuilder(CourseSearchBuilder builder) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (builder.getKeyword() != null && !builder.getKeyword().isBlank()) {
                String keyword = "%" + builder.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("courseCode")), keyword),
                        cb.like(cb.lower(root.get("courseName")), keyword),
                        cb.like(cb.lower(root.get("description")), keyword)
                ));
            }

            if (builder.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), builder.getStatus()));
            }

            return predicates.isEmpty()
                    ? cb.conjunction()
                    : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
