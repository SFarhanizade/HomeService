package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.CommentInDto;
import ir.farhanizade.homeservice.dto.in.RequestInDto;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.ExpertAddSuggestionOutDto;
import ir.farhanizade.homeservice.dto.out.OrderOutDto;
import ir.farhanizade.homeservice.dto.out.SuggestionOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.Transaction;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.OrderStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private RequestService requestService;

    @MockBean
    private SuggestionService suggestionService;

    @MockBean
    private TransactionService transactionService;

    @TestConfiguration
    @EnableAspectJAutoProxy
    @ComponentScan("ir.farhanizade.homeservice")
    public static class CustomerServiceTestConfig {
    }

    private UserInDto getValidCustomer() {
        return UserInDto.builder()
                .firstname("Customer")
                .lastname("Customer")
                .email("Customer@Customer.ir")
                .password("abcd1234")
                .build();
    }

    @Test
    void test_save_empty_field_customer_throws_exception() {
        UserInDto customer = UserInDto.builder()
                .firstname("")
                .lastname("")
                .email("")
                .password("")
                .build();
        assertThrows(Exception.class,
                () -> customerService.save(customer));
    }

    @Test
    void test_save__customer_with_not_valid_firstname_throws_exception() {
        UserInDto customer = getValidCustomer();
        customer.setFirstname("a");
        assertThrows(NameNotValidException.class,
                () -> customerService.save(customer));
    }

    @Test
    void test_save__customer_with_not_valid_lastname_throws_exception() {
        UserInDto customer = getValidCustomer();
        customer.setLastname("a");
        assertThrows(NameNotValidException.class,
                () -> customerService.save(customer));
    }

    @Test
    void test_save__customer_with_not_valid_email_throws_exception() {
        UserInDto customer = getValidCustomer();
        customer.setEmail("123456789");
        assertThrows(EmailNotValidException.class,
                () -> customerService.save(customer));
    }

    @Test
    void test_save__customer_with_not_valid_password_throws_exception() {
        UserInDto customer = getValidCustomer();
        customer.setPassword("123");
        assertThrows(PasswordNotValidException.class,
                () -> customerService.save(customer));
    }

    @Test
    void test_save_duplicate_customer_throws_exception() {
        UserInDto customer = getValidCustomer();
        try {
            customerService.save(customer);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        assertThrows(DuplicateEntityException.class, () -> customerService.save(customer));
    }

    @Test
    void test_save_customer_is_ok() {
        UserInDto customer = getValidCustomer();
        try {
            assertEquals(1L,
                    customerService.save(customer).getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_find_customer_by_not_valid_email_throws_exception() {
        assertThrows(EmailNotValidException.class,
                () -> customerService.findByEmail("123456789"));
    }

    @Test
    void test_find_customer_by_non_existing_email_throws_exception() {
        assertThrows(EntityNotFoundException.class,
                () -> customerService.findByEmail("123@123.ir"));
    }

    @Test
    void test_find_customer_by_email_is_ok() {
        UserInDto customer = getValidCustomer();
        try {
            customerService.save(customer);
            assertEquals(customer.getEmail(),
                    customerService.findByEmail(customer.getEmail()).getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_find_all_customers_throws_exception() {
        assertThrows(EntityNotFoundException.class,
                () -> customerService.findAll(Pageable.ofSize(10)));
    }

    @Test
    void test_find_all_customers_is_ok() {
        UserInDto customer = getValidCustomer();
        try {
            customerService.save(customer);
            assertEquals(1,
                    customerService.findAll(Pageable.ofSize(10)).getData().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_search_customers_throws_exception() {
        UserSearchInDto userSearchInDto = UserSearchInDto.builder()
                .type("customer")
                .build();
        try {
            assertThrows(EntityNotFoundException.class,
                    () -> customerService.search(userSearchInDto, Pageable.ofSize(10)));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_search_customers_is_ok() {
        UserInDto customer = getValidCustomer();

        UserSearchInDto userSearchInDto = UserSearchInDto.builder()
                .type("customer")
                .build();
        try {
            customerService.save(customer);
            assertEquals(1,
                    customerService.search(userSearchInDto, Pageable.ofSize(10)).getData().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_find_customer_by_id_throws_exception() {
        assertThrows(EntityNotFoundException.class,
                () -> customerService.findById(1L));
    }

    @Test
    void test_find_customer_by_id_is_ok() {
        UserInDto customer = getValidCustomer();
        try {
            customerService.save(customer);
            assertEquals(customer.getEmail(),
                    customerService.findById(1L).getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_get_orders_of_customer_is_ok() {
        CustomPage<OrderOutDto> result = CustomPage.<OrderOutDto>builder()
                .data(List.of(new OrderOutDto(),
                        new OrderOutDto(),
                        new OrderOutDto()))
                .build();

        Mockito.when(orderService.findAllByCustomerId(1L, Pageable.ofSize(10)))
                .thenReturn(result);

        try {
            customerService.save(getValidCustomer());
            assertEquals(3,
                    customerService.getOrders(1L, Pageable.ofSize(10)).getData().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_get_order_of_customer_is_ok() {
        OrderOutDto result = OrderOutDto.builder().id(1L).build();

        try {
            Mockito.when(orderService.findByIdAndCustomerId(1L, 1L))
                    .thenReturn(result);

            customerService.save(getValidCustomer());
            assertEquals(result.getId(),
                    customerService.getOrder(1L, 1L).getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_customer_request_is_ok() {
        RequestInDto requestInDto = new RequestInDto();
        EntityOutDto result = new EntityOutDto(1L);

        try {
            customerService.save(getValidCustomer());
            Mockito.when(requestService.save(customerService.findById(1L), requestInDto))
                    .thenReturn(result);
            assertEquals(result,
                    customerService.request(1L, requestInDto));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_customer_exists_is_ok() {
        try {
            customerService.save(getValidCustomer());
            assertTrue(customerService.exists(1L));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void test_customer_exists_throws_exception() {
        assertThrows(EntityNotFoundException.class,
                () -> customerService.exists(1L));
    }

    @Test
    void test_get_suggestions_by_order_is_ok() {
        CustomPage<SuggestionOutDto> result = CustomPage.<SuggestionOutDto>builder()
                .data(List.of(new SuggestionOutDto())).build();

        try {
            Mockito.when(suggestionService.findAllByOrderId(1L, Pageable.ofSize(10)))
                    .thenReturn(result);
            customerService.save(getValidCustomer());
            assertEquals(1,
                    customerService.getSuggestionsByOrder(1L, 1L, Pageable.ofSize(10)).getData().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_get_suggestion_is_ok() {
        SuggestionOutDto result = new SuggestionOutDto();

        try {
            Mockito.when(suggestionService.getById(1L))
                    .thenReturn(result);
            customerService.save(getValidCustomer());
            assertEquals(result,
                    customerService.getSuggestion(1L, 1L));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_get_suggestions_is_ok() {
        CustomPage<SuggestionOutDto> result = CustomPage.<SuggestionOutDto>builder()
                .data(List.of(new SuggestionOutDto())).build();

        try {
            Mockito.when(suggestionService.findAllByCustomerId(1L, Pageable.ofSize(10)))
                    .thenReturn(result);
            customerService.save(getValidCustomer());
            assertEquals(1,
                    customerService.getSuggestions(1L, Pageable.ofSize(10)).getData().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_remove_order_is_ok() {
        EntityOutDto result = new EntityOutDto(1L);

        try {
            Mockito.when(orderService.removeOrderByIdAndOwnerId(1L, 1L))
                    .thenReturn(result);
            customerService.save(getValidCustomer());
            assertEquals(result,
                    customerService.removeOrder(1L, 1L));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_accept_suggestion_is_ok() {
        EntityOutDto result = new EntityOutDto(1L);

        try {
            Mockito.when(orderService.acceptSuggestion(1L))
                    .thenReturn(result);
            customerService.save(getValidCustomer());
            assertEquals(result,
                    customerService.acceptSuggestion(1L, 1L));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_pay_paid_order_throws_exception() {
        EntityOutDto result = new EntityOutDto(1L);
        Suggestion suggestion = Suggestion.builder()
                .order(Order.builder()
                        .request(new Request())
                        .status(OrderStatus.PAID)
                        .build())
                .build();
        try {
            customerService.save(getValidCustomer());
            Mockito.when(suggestionService.findById(1L))
                    .thenReturn(suggestion);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThrows(BadEntryException.class,
                () -> customerService.pay(1L, 1L));
    }

    @Test
    void test_pay_is_ok() {
        EntityOutDto result = new EntityOutDto(1L);
        Suggestion suggestion = Suggestion.builder()
                .order(Order.builder()
                        .request(new Request())
                        .status(OrderStatus.DONE)
                        .build())
                .build();

        Customer customer = null;

        try {
            customerService.save(getValidCustomer());
            customer = customerService.findById(1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Mockito.when(suggestionService.findById(1L))
                    .thenReturn(suggestion);

            Mockito.when(transactionService.save(notNull()))
                    .thenReturn(result);

            Mockito.when(suggestionService.save(suggestion))
                    .thenReturn(new ExpertAddSuggestionOutDto());

            assertEquals(result,
                    customerService.pay(1L, 1L));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}