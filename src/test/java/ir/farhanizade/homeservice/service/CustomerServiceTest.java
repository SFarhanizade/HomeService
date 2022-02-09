package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.CommentInDto;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.order.MyOrder;
import ir.farhanizade.homeservice.entity.order.OrderStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.user.UserCustomer;
import ir.farhanizade.homeservice.entity.user.UserExpert;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.security.user.LoggedInUser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CustomerServiceTest {

    @SpyBean
    private CustomerService customerService;

    private static MockedStatic<LoggedInUser> loggedInUserMockedStatic;

    @MockBean
    private SuggestionService suggestionService;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private CommentService commentService;

    @BeforeAll()
    public static void init() {
        loggedInUserMockedStatic = Mockito.mockStatic(LoggedInUser.class);
    }

    @AfterAll()
    public static void close() {
        loggedInUserMockedStatic.close();
    }

    @TestConfiguration
    @EnableAspectJAutoProxy
    @ComponentScan("ir.farhanizade.homeservice")
    public static class CustomerServiceTestConfig {

    }
    private UserInDto getValidCustomer() {
        return UserInDto.builder()
                .type("customer")
                .firstname("Customer")
                .lastname("Customer")
                .email("Customer@Customer.ir")
                .password("abcd1234")
                .build();
    }

    @Test
    void test_save_empty_field_customer_throws_exception() {
        UserInDto customer = UserInDto.builder()
                .type("")
                .firstname("")
                .lastname("")
                .email("")
                .password("")
                .build();
        assertThrows(Exception.class,
                () -> customerService.save(customer));
    }

    @Test
    void test_save_customer_with_not_valid_firstname_throws_exception() {
        UserInDto customer = getValidCustomer();
        customer.setFirstname("a");
        assertThrows(NameNotValidException.class,
                () -> customerService.save(customer));
    }

    @Test
    void test_save_customer_with_not_valid_lastname_throws_exception() {
        UserInDto customer = getValidCustomer();
        customer.setLastname("a");
        assertThrows(NameNotValidException.class,
                () -> customerService.save(customer));
    }

    @Test
    void test_save_customer_with_not_valid_email_throws_exception() {
        UserInDto customer = getValidCustomer();
        customer.setEmail("123456789");
        assertThrows(EmailNotValidException.class,
                () -> customerService.save(customer));
    }

    @Test
    void test_save_customer_with_not_valid_password_throws_exception() {
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
            assertEquals(2L,
                    customerService.save(customer));
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
    void test_find_customers_by_credit_is_ok() throws EntityNotFoundException {
        CustomPage<UserOutDto> result = customerService.findByCredit(new BigDecimal(0), Pageable.ofSize(10));
        assertTrue(result.getPageSize() == 10);
    }

    @Test
    void test_find_customers_by_status_is_ok() throws EntityNotFoundException {
        CustomPage<UserOutDto> result = customerService.findByStatus(UserStatus.ACCEPTED, Pageable.ofSize(10));
        assertTrue(result.getPageSize() == 10);
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
    void test_pay_paid_order_throws_exception() {
        Suggestion suggestion = Suggestion.builder()
                .myOrder(MyOrder.builder()
                        .request(new Request())
                        .status(OrderStatus.PAID)
                        .build())
                .build();
        try {
            Long id = customerService.save(getValidCustomer());
            Mockito.when(suggestionService.findById(1L))
                    .thenReturn(suggestion);

            loggedInUserMockedStatic.when(LoggedInUser::id)
                    .thenReturn(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThrows(BadEntryException.class,
                () -> customerService.pay(1L, "credit"));
    }

    @Test
    void test_pay_is_ok() {
        EntityOutDto result = new EntityOutDto(1L);
        Suggestion suggestion = Suggestion.builder()
                .myOrder(MyOrder.builder()
                        .request(new Request())
                        .status(OrderStatus.DONE)
                        .build())
                .build();
        Long id = 0L;
        try {
            id = customerService.save(getValidCustomer());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            loggedInUserMockedStatic.when(LoggedInUser::id)
                    .thenReturn(id);
            Mockito.when(suggestionService.findById(1L))
                    .thenReturn(suggestion);

            Mockito.when(transactionService.save(any(), eq("credit")))
                    .thenReturn(result);

            Mockito.when(suggestionService.save(suggestion))
                    .thenReturn(new ExpertAddSuggestionOutDto());

            assertEquals(result,
                    customerService.pay(1L, "credit"));
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
                    customerService.findById(2L).getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void customer_comment_is_ok() throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        UserCustomer customer = UserCustomer.builder().build();
        UserExpert expert = UserExpert.builder().build();
        MyOrder order = MyOrder.builder().build();
        Suggestion suggestion = Suggestion.builder()
                .owner(expert)
                .myOrder(order)
                .build();

        loggedInUserMockedStatic.when(LoggedInUser::id)
                .thenReturn(1L);

        Mockito.doReturn(customer).when(customerService).findById(notNull());

        Mockito.when(suggestionService.findById(notNull()))
                .thenReturn(suggestion);

        CommentInDto commentInDto = new CommentInDto(1, "description");

        Mockito.when(commentService.save(notNull()))
                .thenReturn(new EntityOutDto(1L));

        assertEquals(1L, customerService.comment(1L, commentInDto).getId());
    }
//    @Test
//    void test_search_customers_throws_exception() {
//        UserSearchInDto userSearchInDto = UserSearchInDto.builder()
//                .type("customer")
//                .build();
//        try {
//            assertThrows(EntityNotFoundException.class,
//                    () -> customerService.search(userSearchInDto, Pageable.ofSize(10)));
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();

//        }
//    }
//    @Test
//    void test_search_customers_is_ok() {
//        UserInDto customer = getValidCustomer();
//
//        UserSearchInDto userSearchInDto = UserSearchInDto.builder()
//                .type("customer")
//                .build();
//        try {
//            customerService.save(customer);
//            assertEquals(1,
//                    customerService.search(userSearchInDto, Pageable.ofSize(10)).getData().size());
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();

//        }

//    }
//    @Transactional
//    public EntityOutDto comment(Long suggestionId, CommentInDto commentDto) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
//        UserCustomer customer = findById(LoggedInUser.id());
//        Suggestion suggestion = suggestionService.findById(suggestionId);
//        UserExpert expert = suggestion.getOwner();
//        MyOrder order = suggestion.getMyOrder();
//        MyComment comment = MyComment.builder()
//                .myCustomer(customer)
//                .myExpert(expert)
//                .myOrder(order)
//                .points(commentDto.getPoints())
//                .description(commentDto.getDescription())
//                .build();
//        return commentService.save(comment);

//    }
//    @Test
//    void test_get_orders_of_customer_is_ok() throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
//        CustomPage<OrderOfUserOutDto> result = CustomPage.<OrderOfUserOutDto>builder()
//                .data(List.of(new OrderOfUserOutDto(),
//                        new OrderOfUserOutDto(),
//                        new OrderOfUserOutDto()))
//                .build();
//
//        Mockito.when(orderService.getOrders(Pageable.ofSize(10)))
//                .thenReturn(result);
//
//        try {
//            customerService.save(getValidCustomer());
//            assertEquals(3,
//                    orderService.getOrders(Pageable.ofSize(10)).getData().size());
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();
//        }

//    }
//    @Test
//    void test_get_order_of_customer_is_ok() {
//        OrderOutDto result = OrderOutDto.builder().id(1L).build();
//
//        try {
//            Mockito.when(orderService.findByIdAndCustomerId(1L))
//                    .thenReturn(result);
//
//            customerService.save(getValidCustomer());
//            assertEquals(result.getId(),
//                    orderService.getById(1L));
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();
//        }
//    }

//
//    @Test
//    void test_get_suggestion_is_ok() {
//        SuggestionOutDto result = new SuggestionOutDto();
//
//        try {
//            Mockito.when(suggestionService.getById(1L))
//                    .thenReturn(result);
//            customerService.save(getValidCustomer());
//            assertEquals(result,
//                    suggestionService.getById(1L));
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();
//        }

//    }
//    @Test
//    void test_get_suggestions_is_ok() {
//        CustomPage<SuggestionOutDto> result = CustomPage.<SuggestionOutDto>builder()
//                .data(List.of(new SuggestionOutDto())).build();
//
//        try {
//            Mockito.when(suggestionService.findAllByCustomerId(Pageable.ofSize(10)))
//                    .thenReturn(result);
//            customerService.save(getValidCustomer());
//            assertEquals(1,
//                    suggestionService.findAllByCustomerId(Pageable.ofSize(10)).getData().size());
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();
//        }

//    }
//    @Test
//    void test_remove_order_is_ok() {
//        EntityOutDto result = new EntityOutDto(1L);
//
//        try {
//            Mockito.when(orderService.removeOrderByIdAndOwnerId(1L))
//                    .thenReturn(result);
//            customerService.save(getValidCustomer());
//            assertEquals(result,
//                    orderService.removeOrderByIdAndOwnerId(1L));
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();
//        }

//    }
//    @Test
//    void test_accept_suggestion_is_ok() {
//        EntityOutDto result = new EntityOutDto(1L);
//
//        try {
//            Mockito.when(orderService.acceptSuggestion(1L))
//                    .thenReturn(result);
//            customerService.save(getValidCustomer());
//            assertEquals(result,
//                    orderService.acceptSuggestion(1L));
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();
//        }

//    }

}