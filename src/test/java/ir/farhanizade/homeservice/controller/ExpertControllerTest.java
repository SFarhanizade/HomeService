//package ir.farhanizade.homeservice.controller;
//
//import ir.farhanizade.homeservice.controller.api.ResponseResult;
//import ir.farhanizade.homeservice.dto.in.ExpertAddServiceInDto;
//import ir.farhanizade.homeservice.dto.in.ExpertAddSuggestionInDto;
//import ir.farhanizade.homeservice.dto.in.UserInDto;
//import ir.farhanizade.homeservice.dto.in.UserIncreaseCreditInDto;
//import ir.farhanizade.homeservice.dto.out.*;
//import ir.farhanizade.homeservice.entity.CustomPage;
//import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
//import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
//import ir.farhanizade.homeservice.entity.user.Expert;
//import ir.farhanizade.homeservice.service.ExpertService;
//import ir.farhanizade.homeservice.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.Date;
//import java.util.HashSet;
//import java.util.List;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(ExpertController.class)
//class ExpertControllerTest extends AbstractRestControllerTest {
//
//    @MockBean
//    private UserController userController;
//    @MockBean
//    private UserService userService;
//
//    @MockBean
//    private ExpertService expertService;
//
//    @Test
//    void test_create_isOk() throws Exception {
//        UserInDto expert = UserInDto.builder()
//                .firstname("expert")
//                .lastname("expertPour")
//                .email("expert@expert.ir")
//                .password("abcd1234")
//                .build();
//
//        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
//                .code(1)
//                .data(new EntityOutDto(1L))
//                .message("User saved successfully!")
//                .build();
//
//        ResponseEntity<ResponseResult<EntityOutDto>> returnValue = ResponseEntity.status(HttpStatus.CREATED).body(response);
//
//        Mockito.when(userController.create(expert, Expert.class))
//                .thenReturn(returnValue);
//
//        mvc.perform(post("/experts")
//                        .content(toJson(expert))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data").value(new EntityOutDto(1L)));
//    }
//
//    @Test
//    void test_addService_isOk() throws Exception {
//        ExpertAddServiceInDto request = new ExpertAddServiceInDto(1L, 1L);
//
//        ExpertAddServiceOutDto result = ExpertAddServiceOutDto.builder()
//                .expertId(1L)
//                .services(new HashSet<>(List.of(new EntityOutDto(1L))))
//                .build();
//
//        Mockito.when(expertService.addService(request))
//                .thenReturn(result);
//
//        mvc.perform(post("/experts/addService")
//                        .content(toJson(request))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isAccepted())
//                .andExpect(jsonPath("$.data.services", hasSize(1)));
//    }
//
//    @Test
//    void test_suggest_isOk() throws Exception {
//        ExpertAddSuggestionInDto request = ExpertAddSuggestionInDto.builder()
//                .dateTime(new Date(151617181920L))
//                .duration(2.5D)
//                .details("details")
//                .orderId(1L)
//                .price(1500L)
//                .build();
//
//        ExpertAddSuggestionOutDto result = ExpertAddSuggestionOutDto.builder()
//                .expertId(1L)
//                .suggestionId(1L)
//                .orderId(1L)
//                .build();
//
//        Mockito.when(expertService.suggest(1L, request))
//                .thenReturn(result);
//
//        mvc.perform(post("/experts/1/suggestions")
//                        .content(toJson(request))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data.suggestionId").value(1L));
//    }
//
//    @Test
//    void test_getSuggestions_isOk() throws Exception {
//        for (SuggestionStatus status : SuggestionStatus.values()) {
//
//            ExpertSuggestionOutDto data = ExpertSuggestionOutDto.builder()
//                    .status(status)
//                    .build();
//
//            CustomPage<ExpertSuggestionOutDto> result = CustomPage.<ExpertSuggestionOutDto>builder()
//                    .data(List.of(data)).build();
//
//            Mockito.when(expertService.getSuggestions(1L, Pageable.ofSize(20), status))
//                    .thenReturn(result);
//
//            String statusStr = status.toString();
//            mvc.perform(get("/experts/1/suggestions/" + statusStr.toLowerCase()))
//                    .andExpect(status().isCreated())
//                    .andExpect(jsonPath("$.data.data[0].status").value(statusStr));
//        }
//
//        ExpertSuggestionOutDto data = ExpertSuggestionOutDto.builder().build();
//
//        CustomPage<ExpertSuggestionOutDto> result = CustomPage.<ExpertSuggestionOutDto>builder()
//                .data(List.of(data)).build();
//
//        Mockito.when(expertService.getSuggestions(1L, Pageable.ofSize(20), SuggestionStatus.values()))
//                .thenReturn(result);
//
//        mvc.perform(get("/experts/1/suggestions/all"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data.data", hasSize(1)));
//
//    }
//
//    @Test
//    void test_answerSuggestion_isOk() throws Exception {
//        SuggestionAnswerOutDto resultAccept = SuggestionAnswerOutDto.builder()
//                .answer(BaseMessageStatus.BUSY).build();
//
//        SuggestionAnswerOutDto resultReject = SuggestionAnswerOutDto.builder()
//                .answer(BaseMessageStatus.CANCELLED).build();
//
//        Mockito.when(expertService.answerSuggestion(1L, 1L, BaseMessageStatus.BUSY))
//                .thenReturn(resultAccept);
//
//        Mockito.when(expertService.answerSuggestion(1L, 1L, BaseMessageStatus.CANCELLED))
//                .thenReturn(resultReject);
//
//        mvc.perform(get("/experts/1/suggestions/1/accept"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data.answer").value(BaseMessageStatus.BUSY.toString()));
//
//        mvc.perform(get("/experts/1/suggestions/1/reject"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data.answer").value(BaseMessageStatus.CANCELLED.toString()));
//    }
//
//    @Test
//    void test_startToWork_isOk() throws Exception {
//        EntityOutDto result = new EntityOutDto(1L);
//
//        Mockito.when(expertService.startToWork(1L, 1L))
//                .thenReturn(result);
//
//        mvc.perform(get("/experts/1/suggestions/1/start"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data").value(result));
//    }
//
//    @Test
//    void test_finishWork_isOk() throws Exception {
//        EntityOutDto result = new EntityOutDto(1L);
//
//        Mockito.when(expertService.finishWork(1L, 1L))
//                .thenReturn(result);
//
//        mvc.perform(get("/experts/1/suggestions/1/done"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data").value(result));
//    }
//
//    @Test
//    void test_showTransactions_isOk() throws Exception {
//        CustomPage<TransactionOutDto> result = CustomPage.<TransactionOutDto>builder()
//                .data(List.of(new TransactionOutDto(),
//                        new TransactionOutDto(),
//                        new TransactionOutDto()))
//                .build();
//
//        Mockito.when(userService.getTransactions(Pageable.ofSize(20)))
//                .thenReturn(result);
//
//        mvc.perform(get("/experts/1/transactions"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.data", hasSize(3)));
//    }
//
//    @Test
//    void test_showTransaction_isOk() throws Exception {
//        TransactionOutDto result = new TransactionOutDto();
//
//        Mockito.when(userService.getTransaction(1L))
//                .thenReturn(result);
//
//        mvc.perform(get("/experts/1/transactions/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data").value(result));
//    }
//
//    @Test
//    void test_showComments_isOk() throws Exception {
//        CustomPage<CommentOutDto> result = CustomPage.<CommentOutDto>builder()
//                .data(List.of(new CommentOutDto(),
//                        new CommentOutDto(),
//                        new CommentOutDto())).build();
//
//        Mockito.when(userService.getComments(Pageable.ofSize(20)))
//                .thenReturn(result);
//
//        mvc.perform(get("/experts/comments"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.data", hasSize(3)));
//    }
//
//    @Test
//    void showComment() throws Exception {
//        CommentOutDto result = new CommentOutDto();
//
//        Mockito.when(expertService.getComment(1L, 1L))
//                .thenReturn(result);
//
//        mvc.perform(get("/experts/1/comments/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data").value(result));
//    }
//
//    @Test
//    void showOrders() throws Exception {
//        CustomPage<OrderFinishOutDto> result = CustomPage.<OrderFinishOutDto>builder()
//                .data(List.of(new OrderFinishOutDto(),
//                        new OrderFinishOutDto(),
//                        new OrderFinishOutDto())).build();
//
//        Mockito.when(expertService.getOrders(1L, Pageable.ofSize(20)))
//                .thenReturn(result);
//
//        mvc.perform(get("/experts/1/orders"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.data", hasSize(3)));
//    }
//
//    @Test
//    void showCredit() throws Exception {
//        UserCreditOutDto result = new UserCreditOutDto();
//
//        Mockito.when(userService.loadCredit())
//                .thenReturn(result);
//
//        mvc.perform(get("/experts/credit"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data").value(result));
//    }
//
//    @Test
//    void addCredit() throws Exception {
//        UserIncreaseCreditInDto request = new UserIncreaseCreditInDto(1500L);
//
//        UserIncreaseCreditOutDto result = UserIncreaseCreditOutDto.builder()
//                .amount(1500L).build();
//
//        Mockito.when(userService.increaseCredit(request))
//                .thenReturn(result);
//
//        mvc.perform(post("/experts/1/credit")
//                        .content(toJson(request))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.amount").value(result.getAmount()));
//    }
//}