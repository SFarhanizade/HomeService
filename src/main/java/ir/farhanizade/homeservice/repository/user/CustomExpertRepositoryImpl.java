package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class CustomExpertRepositoryImpl implements CustomExpertRepository {

    @Autowired
    EntityManager em;

    @Override
    public CustomPage<Expert> search(UserSearchInDto user, Pageable pageable) {
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
        if (firstname != null && !"".equals(firstname)) {
            predicates.add(cb.equal(from.get("fName"), firstname));
        }
        if (lastname != null && !"".equals(lastname)) {
            predicates.add(cb.equal(from.get("lName"), lastname));
        }
        if (email != null && !"".equals(email)) {
            predicates.add(cb.equal(from.get("email"), email));
        }
        if (points != null) {
            predicates.add(cb.equal(from.get("points"), points));
        }
        if (expertises != null && expertises.size() > 0) {
            Expression<Long> ids = join.get("id");
            expertises.stream().forEach(e -> predicates.add(ids.in(expertises)));
        }
        cq.where(predicates.toArray(new Predicate[]{})).distinct(true);
        TypedQuery<Expert> query = em.createQuery(cq);
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(from));
        countQuery.from(Expert.class).join("expertises");
        countQuery.where(predicates.toArray(new Predicate[]{})).distinct(true);
        Long count = em.createQuery(countQuery).getSingleResult();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int pages = count.intValue() / pageable.getPageSize();
        if (count.intValue() % pageable.getPageSize() == 0)
            pages--;
        List<Expert> resultList = new ArrayList<>();
        if (pageNumber <= pages) {
            query.setFirstResult(pageNumber);
            query.setMaxResults(pageSize);
            resultList = query.getResultList();
        }
        return CustomPage.<Expert>builder()
                .pageSize(pageSize)
                .totalElements(count)
                .lastPage(pages)
                .pageNumber(pageNumber)
                .data(resultList)
                .build();
    }
}
