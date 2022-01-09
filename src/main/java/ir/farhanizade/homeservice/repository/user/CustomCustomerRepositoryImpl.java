package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.entity.user.Customer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class CustomCustomerRepositoryImpl implements CustomCustomerRepository {

    @Autowired
    EntityManager em;

    @Override
    public List<Customer> search(UserSearchInDto user) {
        String firstname = user.getFirstname();
        String lastname = user.getLastname();
        String email = user.getEmail();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> cq = cb.createQuery(Customer.class);
        Root<Customer> from = cq.from(Customer.class);
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
