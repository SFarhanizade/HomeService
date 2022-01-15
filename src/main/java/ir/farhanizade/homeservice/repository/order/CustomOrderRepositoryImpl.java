package ir.farhanizade.homeservice.repository.order;

import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.service.SubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ir.farhanizade.homeservice.entity.order.OrderStatus.WAITING_FOR_SELECTION;
import static ir.farhanizade.homeservice.entity.order.OrderStatus.WAITING_FOR_SUGGESTION;

public class CustomOrderRepositoryImpl implements CustomOrderRepository {

    @Autowired
    EntityManager em;

    @Override
    public CustomPage<Order> findByExpertises(Set<SubService> expertises, Pageable pageable) {
        int pageNumber = pageable.getPageNumber()+1;
        int pageSize = pageable.getPageSize();
        List<Long> subServices = expertises.stream().map(s -> s.getId()).toList();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> from = cq.from(Order.class);
        Join join = from.join("service");

        Expression<Long> ids = join.get("id");
        Predicate expertise = ids.in(subServices);
        Predicate status1 = cb.equal(from.get("status"), WAITING_FOR_SUGGESTION);
        Predicate status2 = cb.equal(from.get("status"), WAITING_FOR_SELECTION);
        Predicate status = cb.or(status1, status2);
        cq.where(expertise, status);

        TypedQuery<Order> query = em.createQuery(cq);


        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.from(Order.class).join("service");
        countQuery.select(cb.count(from));
        countQuery.where(expertise,status);
        Long count = em.createQuery(countQuery).getSingleResult();
        int totalPage = (count.intValue() / 10) + 1;

        List<Order> resultList = new ArrayList<>();
        if (pageNumber <=totalPage) {
            query.setFirstResult(pageNumber - 1);
            query.setMaxResults(pageSize);
            resultList = query.getResultList();
        }
        return CustomPage.<Order>builder()
                .pageNumber(pageNumber)
                .lastPage(totalPage)
                .pageSize(pageSize)
                .data(resultList)
                .build();
    }
}
