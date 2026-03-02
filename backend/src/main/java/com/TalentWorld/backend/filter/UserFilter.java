package com.TalentWorld.backend.filter;

import com.TalentWorld.backend.dto.request.UserFilterRequest;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
public class UserFilter {

    public static Specification<User> filter(UserFilterRequest request) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.email() != null && !request.email().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("email")),
                                "%" + request.email().toLowerCase() + "%"
                        )
                );
            }

            if (request.name() != null && !request.name().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%" + request.name().toLowerCase() + "%"
                        )
                );
            }

            if (request.isActive() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("isActive"), request.isActive())
                );
            }

            if (request.roles() != null && !request.roles().isEmpty()) {

                Join<User, Role> roleJoin = root.join("roles");

                predicates.add(
                        roleJoin.in(request.roles())
                );

                query.distinct(true);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
