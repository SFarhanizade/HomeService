package ir.farhanizade.homeservice.repository.order;

import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.user.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomOrderRepository {
    Page<Order> loadByExpertises(Specification<Order> spec, Pageable pageable);
}
