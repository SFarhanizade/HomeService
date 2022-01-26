package ir.farhanizade.homeservice.controller.api.filter;

import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.entity.user.Expert;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserSpecification<T> {
    public Specification<T> getUsers(UserSearchInDto filter) {
        return (root, query, cb) -> {
            String firstname = filter.getFirstname();
            String lastname = filter.getLastname();
            String email = filter.getEmail();
            Integer points = filter.getPoints();
            List<Long> expertises = filter.getExpertises();
            List<Predicate> predicates = new ArrayList<>();
            if (firstname != null && !"".equals(firstname)) {
                predicates.add(cb.equal(root.get("fName"), firstname));
            }
            if (lastname != null && !"".equals(lastname)) {
                predicates.add(cb.equal(root.get("lName"), lastname));
            }
            if (email != null && !"".equals(email)) {
                predicates.add(cb.equal(root.get("email"), email));
            }
            if (points != null) {
                predicates.add(cb.equal(root.get("points"), points));
            }
            if (expertises != null && expertises.size() > 0) {
                Join join = root.join("expertises");
                Expression<Long> ids = join.get("id");
                expertises.stream().forEach(e -> predicates.add(ids.in(expertises)));
            }
            return cb.and(predicates.toArray(new Predicate[]{}));
        };
    }
}
