package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class CustomUserRepositoryImpl implements CustomUserRepository {

    @Autowired
    EntityManager em;

    @Override
    public List<User> search(UserSearchInDto user) {
        String firstname = user.getFirstname();
        String lastname = user.getLastname();
        String email = user.getEmail();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> from = cq.from(User.class);
        List<Predicate> predicates = new ArrayList<>();
        if(firstname !=null && !"".equals(firstname)){
            predicates.add(cb.equal(from.get("fName"),firstname));
        }
        if(lastname !=null && !"".equals(lastname)){
            predicates.add(cb.equal(from.get("lName"),lastname));
        }
        if(email !=null && !"".equals(email)){
            predicates.add(cb.equal(from.get("email"),email));
        }
        cq.where(predicates.toArray(new Predicate[] {})).distinct(true);
        return em.createQuery(cq).getResultList();
    }
}
