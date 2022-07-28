package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.ExpertAddServiceInDto;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.out.UserOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.service.MyService;
import ir.farhanizade.homeservice.entity.user.UserExpert;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.ExpertRepository;
import ir.farhanizade.homeservice.security.user.LoggedInUser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.notNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ExpertServiceTest {

    private static MockedStatic<LoggedInUser> loggedInUserMockedStatic;

    @SpyBean
    private ExpertService expertService;

    @SpyBean
    private ExpertRepository repository;

    @MockBean
    private ServiceService serviceManager;

    @BeforeAll()
    public static void init() {
        loggedInUserMockedStatic = Mockito.mockStatic(LoggedInUser.class);
    }

    @AfterAll()
    public static void close() {
        loggedInUserMockedStatic.close();
    }


    private UserInDto getValidUserDto() {
        return UserInDto.builder()
                .type("expert")
                .firstname("User")
                .lastname("User")
                .email("User@User.ir")
                .password("abcd1234")
                .build();
    }

    private UserExpert getValidUser() {
        return getValidUserDto().convert2Expert();
    }

//    @Test
//    void test_suggest_is_ok() {
//        ExpertAddSuggestionInDto request = ExpertAddSuggestionInDto.builder()
//                .price(5000L)
//                .dateTime(new Date(System.currentTimeMillis()))
//                .details("details")
//                .duration(2.5D)
//                .build();
//        repository.save(getValidUser());
//
//        try {
//            Mockito.when(orderService.findById(1L))
//                    .thenReturn(new MyOrder());
//            Mockito.when(suggestionService.save(notNull()))
//                    .thenReturn(new ExpertAddSuggestionOutDto());
//            assertEquals(new ExpertAddSuggestionOutDto(),
//                    expertService.suggest(1L, request));
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();
//        }
//    }

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
                () -> expertService.save(customer));
    }

    @Test
    void test_save_expert_with_not_valid_firstname_throws_exception() {
        UserInDto customer = getValidUserDto();
        customer.setFirstname("a");
        assertThrows(NameNotValidException.class,
                () -> expertService.save(customer));
    }

    @Test
    void test_save_expert_with_not_valid_lastname_throws_exception() {
        UserInDto customer = getValidUserDto();
        customer.setLastname("a");
        assertThrows(NameNotValidException.class,
                () -> expertService.save(customer));
    }

    @Test
    void test_save_expert_with_not_valid_email_throws_exception() {
        UserInDto customer = getValidUserDto();
        customer.setEmail("123456789");
        assertThrows(EmailNotValidException.class,
                () -> expertService.save(customer));
    }

    @Test
    void test_save_expert_with_not_valid_password_throws_exception() {
        UserInDto customer = getValidUserDto();
        customer.setPassword("123");
        assertThrows(PasswordNotValidException.class,
                () -> expertService.save(customer));
    }

    @Test
    void test_save_duplicate_customer_throws_exception() {
        UserInDto customer = getValidUserDto();
        try {
            expertService.save(customer);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        assertThrows(DuplicateEntityException.class, () -> expertService.save(customer));
    }

    @Test
    void test_save_expert_is_ok() {
        UserInDto customer = getValidUserDto();
        try {
            assertEquals(2L,
                    expertService.save(customer));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_find_expert_by_not_valid_email_throws_exception() {
        assertThrows(EmailNotValidException.class,
                () -> expertService.findByEmail("123456789"));
    }

    @Test
    void test_find_expert_by_non_existing_email_throws_exception() {
        assertThrows(EntityNotFoundException.class,
                () -> expertService.findByEmail("123@123.ir"));
    }

    @Test
    void test_find_expert_by_email_is_ok() {
        UserInDto customer = getValidUserDto();
        try {
            expertService.save(customer);
            assertEquals(customer.getEmail(),
                    expertService.findByEmail(customer.getEmail()).getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_find_experts_by_credit_is_ok() throws EntityNotFoundException {
        CustomPage<UserOutDto> result = expertService.findByCredit(new BigDecimal(0), Pageable.ofSize(10));
        assertTrue(result.getPageSize() == 10);
    }

    @Test
    void test_find_experts_by_status_is_ok() throws EntityNotFoundException {
        CustomPage<UserOutDto> result = expertService.findByStatus(UserStatus.ACCEPTED, Pageable.ofSize(10));
        assertTrue(result.getPageSize() == 10);
    }

    @Test
    void test_find_experts_by_expertises_is_ok() throws EntityNotFoundException {
        UserExpert expert = UserExpert.builder().id(1L).build();
        Page<UserExpert> userExperts = new PageImpl<>(List.of(expert));
        Mockito.doReturn(userExperts).when(repository).findByExpertise(1L, Pageable.ofSize(10));
        CustomPage<UserOutDto> result = expertService.findByExpertise(1L, Pageable.ofSize(10));
        assertEquals(expert.getId(), result.getData().get(0).getId());
    }

    @Test
    void test_add_service_for_not_accepted_expert_throws_exception() throws EntityNotFoundException {
        ExpertAddServiceInDto request = new ExpertAddServiceInDto(1L);
        loggedInUserMockedStatic.when(LoggedInUser::id).thenReturn(1L);
        UserExpert expert = UserExpert.builder()
                .id(1L)
                .status(UserStatus.PENDING).build();
        Mockito.doReturn(expert).when(expertService).findById(notNull());
        MyService service = MyService.builder()
                .id(1L)
                .name("service1")
                .build();
        try {
            Mockito.doReturn(expert).when(expertService).findById(notNull());
            Mockito.when(serviceManager.getByID(1L))
                    .thenReturn(service);
            Mockito.when(serviceManager.getByID(1L))
                    .thenReturn(service);
            assertThrows(ExpertNotAcceptedException.class,
                    () -> expertService.addService(request));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }


    }

    @Test
    void test_add_duplicate_service_throws_exception() throws EntityNotFoundException {
        ExpertAddServiceInDto request = new ExpertAddServiceInDto(1L);
        loggedInUserMockedStatic.when(LoggedInUser::id).thenReturn(1L);
        UserExpert expert = UserExpert.builder()
                .id(1L)
                .status(UserStatus.ACCEPTED).build();
        Mockito.doReturn(expert).when(expertService).findById(notNull());
        MyService service = MyService.builder()
                .id(1L)
                .name("service1")
                .build();
        try {
            Mockito.doReturn(expert).when(repository).save(notNull());
            Mockito.when(serviceManager.getByID(1L))
                    .thenReturn(service);
            expertService.addService(request);
            assertThrows(DuplicateEntityException.class,
                    () -> expertService.addService(request));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }


    }

    @Test
    void test_add_service_is_ok() throws EntityNotFoundException {
        ExpertAddServiceInDto request = new ExpertAddServiceInDto(1L);
        loggedInUserMockedStatic.when(LoggedInUser::id).thenReturn(1L);
        UserExpert expert = UserExpert.builder()
                .id(1L)
                .status(UserStatus.ACCEPTED).build();
        Mockito.doReturn(expert).when(expertService).findById(notNull());
        MyService service = MyService.builder()
                .id(1L)
                .name("service1")
                .build();
        try {
            Mockito.doReturn(expert).when(repository).save(notNull());
            Mockito.when(serviceManager.getByID(1L))
                    .thenReturn(service);
            assertEquals(1L,
                    expertService.addService(request).getExpertId());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_accept_expert_is_ok() throws EntityNotFoundException {
        UserExpert expert = UserExpert.builder().id(1L).build();
        Mockito.doReturn(expert).when(expertService).findById(1L);

        Mockito.doReturn(null).when(repository).save(expert);

        assertEquals(1L, expertService.acceptExpert(1L).getId());
    }

//    @Test
//    void test_load_available_orders_throws_exception() {
//
//        repository.save(getValidUser());
//
//        CustomPage<OrderOutDto> result = CustomPage.<OrderOutDto>builder()
//                .data(List.of(new OrderOutDto(),
//                        new OrderOutDto(),
//                        new OrderOutDto()))
//                .build();
//
//        try {
//            Mockito.when(orderService.loadByExpertises(notNull(), notNull()))
//                    .thenReturn(result);
//            assertThrows(EntityNotFoundException.class,
//                    () -> expertService.loadAvailableOrders(1L, Pageable.ofSize(10)));
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();
//        }
//    }

//    @Test
//    void test_load_available_orders_is_ok() {
//
//        Expert expert = repository.save(getValidUser());
//
//        SubService service = SubService.builder()
//                .id(1L)
//                .name("service1")
//                .basePrice(new BigDecimal(1000))
//                .parent(MainService.builder()
//                        .id(1L)
//                        .name("main")
//                        .build())
//                .build();
//
//        expert.addService(service);
//        repository.save(expert);
//
//        CustomPage<OrderOutDto> result = CustomPage.<OrderOutDto>builder()
//                .data(List.of(new OrderOutDto(),
//                        new OrderOutDto(),
//                        new OrderOutDto()))
//                .build();
//
//        try {
//            Mockito.when(orderService.loadByExpertises(notNull(), notNull()))
//                    .thenReturn(result);
//            assertEquals(3,
//                    expertService.loadAvailableOrders(1L, Pageable.ofSize(10)).getData().size());
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();
//        }
//
//    }
}