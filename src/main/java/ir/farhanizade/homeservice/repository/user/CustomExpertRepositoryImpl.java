package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.entity.user.Expert;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class CustomExpertRepositoryImpl implements CustomExpertRepository {

    @Autowired
    EntityManager em;

    @Override
    public List<Expert> search(UserSearchInDto user) {
        String firstname = user.getFirstname();
        String lastname = user.getLastname();
        String email = user.getEmail();
        Integer points = user.getPoints();
        List<Long> expertises = user.getExpertises();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Expert> cq = cb.createQuery(Expert.class);
        Root<Expert> from = cq.from(Expert.class);
        Join join = from.join("expertises");
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
        if(points !=null){
            predicates.add(cb.equal(from.get("points"),points));
        }
        if(expertises !=null && expertises.size()>0){
            Expression<Long> ids = join.get("id");
            expertises.stream().forEach(e -> predicates.add(ids.in(expertises)));
        }
        cq.where(predicates.toArray(new Predicate[] {})).distinct(true);
        return em.createQuery(cq).getResultList();
    }
}
