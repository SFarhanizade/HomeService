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
}
