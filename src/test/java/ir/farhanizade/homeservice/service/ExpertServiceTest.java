package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.ExpertAddServiceInDto;
import ir.farhanizade.homeservice.dto.in.ExpertAddSuggestionInDto;
import ir.farhanizade.homeservice.dto.in.ExpertInDto;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.out.ExpertAddSuggestionOutDto;
import ir.farhanizade.homeservice.dto.out.OrderOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.User;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.ExpertRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.notNull;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ExpertServiceTest {

    @TestConfiguration
    @EnableAspectJAutoProxy
    @ComponentScan("ir.farhanizade.homeservice")
    public static class CustomerServiceTestConfig {
    }

    @Autowired
    private ExpertService expertService;

    @Autowired
    private ExpertRepository repository;

    @MockBean
    private SubServiceService serviceManager;

    @MockBean
    private OrderService orderService;

    @MockBean
    private SuggestionService suggestionService;

    @MockBean
    private CommentService commentService;

    private UserInDto getValidUserDto() {
        return UserInDto.builder()
                .firstname("User")
                .lastname("User")
                .email("User@User.ir")
                .password("abcd1234")
                .build();
    }

    private Expert getValidUser() {
        return getValidUserDto().convert2Expert();
    }

    @Test
    void test_suggest_is_ok() {
        ExpertAddSuggestionInDto request = ExpertAddSuggestionInDto.builder()
                .price(5000L)
                .dateTime(new Date(System.currentTimeMillis()))
                .details("details")
                .duration(2.5D)
                .orderId(1L)
                .build();
        repository.save(getValidUser());

        try {
            Mockito.when(orderService.findById(1L))
                    .thenReturn(new Order());
            Mockito.when(suggestionService.save(notNull()))
                    .thenReturn(new ExpertAddSuggestionOutDto());
            assertEquals(new ExpertAddSuggestionOutDto(),
                    expertService.suggest(1L, request));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_add_service_for_not_accepted_expert_throws_exception() {
        ExpertAddServiceInDto request = new ExpertAddServiceInDto(1L, 1L);
        Expert expert = getValidUser();
        expert.setStatus(UserStatus.NEW);

        SubService service = SubService.builder()
                .id(1L)
                .name("service1")
                .basePrice(new BigDecimal(1000))
                .parent(MainService.builder()
                        .id(1L)
                        .name("main")
                        .build())
                .build();
        repository.save(expert);

        try {
            Mockito.when(serviceManager.loadById(1L))
                    .thenReturn(service);
            assertThrows(ExpertNotAcceptedException.class,
                    () -> expertService.addService(request));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }


    }

    @Test
    void test_add_duplicate_service_throws_exception() {
        ExpertAddServiceInDto request = new ExpertAddServiceInDto(1L, 1L);
        Expert expert = getValidUser();
        expert.setStatus(UserStatus.ACCEPTED);

        repository.save(expert);
        try {
            Expert byId = expertService.findById(1L);
            SubService service = SubService.builder()
                    .id(1L)
                    .name("service1")
                    .basePrice(new BigDecimal(1000))
                    .parent(MainService.builder()
                            .id(1L)
                            .name("main")
                            .build())
                    .build();

            byId.addService(service);
            repository.save(byId);
            Mockito.when(serviceManager.loadById(1L))
                    .thenReturn(service);
            assertThrows(DuplicateEntityException.class,
                    () -> expertService.addService(request));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }


    }

    @Test
    void test_add_service_is_ok() {
        ExpertAddServiceInDto request = new ExpertAddServiceInDto(1L, 1L);
        Expert expert = getValidUser();
        expert.setStatus(UserStatus.ACCEPTED);
        repository.save(expert);
        try {
            Mockito.when(serviceManager.loadById(1L))
                    .thenReturn(SubService.builder()
                            .id(1L)
                            .name("service1")
                            .basePrice(new BigDecimal(1000))
                            .parent(MainService.builder()
                                    .name("main")
                                    .build())
                            .build());
            assertEquals(1,
                    expertService.addService(request).getServices().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }


    }

    @Test
    void test_load_available_orders_throws_exception() {

        repository.save(getValidUser());

        CustomPage<OrderOutDto> result = CustomPage.<OrderOutDto>builder()
                .data(List.of(new OrderOutDto(),
                        new OrderOutDto(),
                        new OrderOutDto()))
                .build();

        try {
            Mockito.when(orderService.loadByExpertises(notNull(), notNull()))
                    .thenReturn(result);
            assertThrows(EntityNotFoundException.class,
                    () -> expertService.loadAvailableOrders(1L, Pageable.ofSize(10)));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_load_available_orders_is_ok() {

        Expert expert = repository.save(getValidUser());

        SubService service = SubService.builder()
                .id(1L)
                .name("service1")
                .basePrice(new BigDecimal(1000))
                .parent(MainService.builder()
                        .id(1L)
                        .name("main")
                        .build())
                .build();

        expert.addService(service);
        repository.save(expert);

        CustomPage<OrderOutDto> result = CustomPage.<OrderOutDto>builder()
                .data(List.of(new OrderOutDto(),
                        new OrderOutDto(),
                        new OrderOutDto()))
                .build();

        try {
            Mockito.when(orderService.loadByExpertises(notNull(), notNull()))
                    .thenReturn(result);
            assertEquals(3,
                    expertService.loadAvailableOrders(1L, Pageable.ofSize(10)).getData().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }
}