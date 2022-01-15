package ir.farhanizade.homeservice.repository.order;

import ir.farhanizade.homeservice.entity.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.EntityManager;

public class CustomOrderRepositoryImpl implements CustomOrderRepository {

    @Autowired
    EntityManager em;

    @Override
    public Page<Order> loadByExpertises(Specification<Order> spec, Pageable pageable) {
        return null;
    }
}
