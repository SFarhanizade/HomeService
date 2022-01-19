package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.repository.order.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.notNull;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderServiceTest {

    @TestConfiguration
    @EnableAspectJAutoProxy
    @ComponentScan("ir.farhanizade.homeservice")
    public static class UserServiceTestConfig {
    }

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @Test
    void save() {
    }

    @Test
    void loadByExpertises() {
    }

    @Test
    void loadAll() {
    }

    @Test
    void findById() {
    }

    @Test
    void findByIdAndCustomerId() {
    }

    @Test
    void findAllByCustomerId() {
    }

    @Test
    void removeOrderByIdAndOwnerId() {
    }

    @Test
    void acceptSuggestion() {
    }

    @Test
    void findAllByExpertId() {
    }
}