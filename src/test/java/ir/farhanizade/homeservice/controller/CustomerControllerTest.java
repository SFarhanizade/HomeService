package ir.farhanizade.homeservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.CommentInDto;
import ir.farhanizade.homeservice.dto.in.RequestInDto;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserIncreaseCreditInDto;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.CustomerService;
import ir.farhanizade.homeservice.service.RequestService;
import ir.farhanizade.homeservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomerController.class)
class CustomerControllerTest extends AbstractRestControllerTest {

    @MockBean
    private UserController userController;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private UserService userService;

    @MockBean
    private RequestService requestService;

    @Test
    void test_create_isOk() throws Exception {
        UserInDto customer = UserInDto.builder()
                .firstname("customer")
                .lastname("customerPour")
                .email("customer@customer.ir")
                .password("abcd1234")
                .build();

        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .data(new EntityOutDto(1L))
                .message("User saved successfully!")
                .build();

        ResponseEntity<ResponseResult<EntityOutDto>> returnValue = ResponseEntity.status(HttpStatus.CREATED).body(response);

        Mockito.when(userController.create(customer, Customer.class))
                .thenReturn(returnValue);

        mvc.perform(post("/customers")
                        .content(toJson(customer))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(new EntityOutDto(1L)));

    }

    @Test
    void test_request_isOk() throws Exception {
        RequestInDto requestInDto =
                new RequestInDto(1L,
                        2500L,
                        new Date(15151413121110L),
                        "details",
                        "address");

        Mockito.when(customerService.request(1L, requestInDto))
                .thenReturn(new EntityOutDto(1L));

        mvc.perform(post("/customers/1/orders/")
                        .content(toJson(requestInDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(new EntityOutDto(1L)));
    }

    @Test
    void test_showOrders_isOk() throws Exception {
        CustomPage<OrderOutDto> result = CustomPage.<OrderOutDto>builder()
                .pageNumber(0)
                .data(List.of(OrderOutDto.builder().build()))
                .lastPage(1)
                .totalElements(1L)
                .pageSize(20)
                .build();

        Mockito.when(customerService.getOrders(1L, Pageable.ofSize(20)))
                .thenReturn(result);

        mvc.perform(get("/customers/1/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalElements").value(result.getTotalElements()));
    }

    @Test
    void test_showOrder_isOk() throws Exception {
        OrderOutDto result = OrderOutDto.builder().id(1L).build();

        Mockito.when(customerService.getOrder(1L, 1L))
                .thenReturn(result);
        mvc.perform(get("/customers/1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L));

    }

    @Test
    void test_removeOrder_isOk() throws Exception {
        EntityOutDto result = new EntityOutDto(1L);

        Mockito.when(customerService.removeOrder(1L, 1L))
                .thenReturn(result);
        mvc.perform(get("/customers/1/orders/1/remove"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L));
    }

    @Test
    void test_showSuggestionsByOrder_isOk() throws Exception {
        CustomPage<SuggestionOutDto> result = CustomPage.<SuggestionOutDto>builder()
                .totalElements(3L)
                .build();

        Mockito.when(customerService.getSuggestionsByOrder(1L, 1L, Pageable.ofSize(20)))
                .thenReturn(result);

        mvc.perform(get("/customers/1/orders/1/suggestions"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalElements").value(3L));
    }

    @Test
    void test_showAllSuggestions_isOk() throws Exception {
        CustomPage<SuggestionOutDto> result = CustomPage.<SuggestionOutDto>builder()
                .totalElements(3L)
                .build();

        Mockito.when(customerService.getSuggestions(1L, Pageable.ofSize(20)))
                .thenReturn(result);

        mvc.perform(get("/customers/1/suggestions"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalElements").value(3L));
    }

    @Test
    void test_showSuggestion_isOk() throws Exception {
        SuggestionOutDto result = SuggestionOutDto.builder().ownerId(2L).build();

        Mockito.when(customerService.getSuggestion(1L, 1L))
                .thenReturn(result);

        mvc.perform(get("/customers/1/suggestions/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ownerId").value(2L));
    }

    @Test
    void test_acceptSuggestion_isOk() throws Exception {
        EntityOutDto result = new EntityOutDto(1L);

        Mockito.when(customerService.acceptSuggestion(1L, 1L))
                .thenReturn(result);

        mvc.perform(get("/customers/1/suggestions/1/accept"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L));
    }

    @Test
    void test_pay_isOk() throws Exception {
        EntityOutDto result = new EntityOutDto(1L);

        Mockito.when(customerService.pay(1L, 1L))
                .thenReturn(result);

        mvc.perform(post("/customers/1/suggestions/1/pay"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L));
    }

    @Test
    void test_showTransactions_isOk() throws Exception {
        CustomPage<TransactionOutDto> result = CustomPage.<TransactionOutDto>builder().pageSize(20).build();

        Mockito.when(userService.getTransactions(1L, Pageable.ofSize(20)))
                .thenReturn(result);

        mvc.perform(get("/customers/1/transactions"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.pageSize").value(20));

    }

    @Test
    void test_showTransaction_isOk() throws Exception {
        TransactionOutDto result = TransactionOutDto.builder().id(1L).build();

        Mockito.when(userService.getTransaction(1L, 1L))
                .thenReturn(result);

        mvc.perform(get("/customers/1/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L));

    }

    @Test
    void test_comment_isOk() throws Exception {
        CommentInDto commentInDto = new CommentInDto();
        EntityOutDto result = new EntityOutDto(1L);

        Mockito.when(customerService.comment(1L, 1L, commentInDto))
                .thenReturn(result);

        mvc.perform(post("/customers/1/suggestions/1/comment")
                        .content(toJson(commentInDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L));
    }

    @Test
    void test_showComments_isOk() throws Exception {
        CustomPage<CommentOutDto> result = CustomPage.<CommentOutDto>builder().pageSize(3).build();

        Mockito.when(userService.getComments(1L, Pageable.ofSize(20)))
                .thenReturn(result);

        mvc.perform(get("/customers/1/comments"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.pageSize").value(3));
    }

    @Test
    void test_showComment_isOk() throws Exception {
        CommentOutDto result = CommentOutDto.builder().id(1L).build();

        Mockito.when(customerService.getComment(1L, 1L))
                .thenReturn(result);

        mvc.perform(get("/customers/1/comments/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L));
    }

    @Test
    void test_showCredit_isOk() throws Exception {
        UserCreditOutDto result = new UserCreditOutDto(1L, new BigDecimal(3500));

        Mockito.when(userService.loadCreditById(1L))
                .thenReturn(result);

        mvc.perform(get("/customers/1/credit"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.credit").value(result.getCredit()));
    }

    @Test
    void addCredit() throws Exception {
        UserIncreaseCreditInDto request = new UserIncreaseCreditInDto(1500L);

        UserIncreaseCreditOutDto result = UserIncreaseCreditOutDto.builder()
                .id(1L)
                .amount(request.getAmount())
                .balance(new BigDecimal(request.getAmount()))
                .build();

        Mockito.when(userService.increaseCredit(1L, request))
                .thenReturn(result);

        mvc.perform(post("/customers/1/credit")
                        .content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.amount").value(request.getAmount()));
    }
}