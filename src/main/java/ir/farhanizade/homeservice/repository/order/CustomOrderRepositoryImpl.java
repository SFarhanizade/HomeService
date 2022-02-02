package ir.farhanizade.homeservice.repository.order;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

public class CustomOrderRepositoryImpl implements CustomOrderRepository {

    @Autowired
    EntityManager em;
}
