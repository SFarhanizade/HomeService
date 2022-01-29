package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.ExpertAddServiceInDto;
import ir.farhanizade.homeservice.dto.in.ExpertAddSuggestionInDto;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserIncreaseCreditInDto;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.ExpertService;
import ir.farhanizade.homeservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus.BUSY;
import static ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus.CANCELLED;

@RestController
@RequestMapping("/experts")
@RequiredArgsConstructor
public class ExpertController {
    private final ExpertService expertService;
    private final UserController userController;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseResult<UUIDOutDto>>
    create(@RequestBody UserInDto user) throws Exception {
        return userController.create(user, Expert.class);
    }

    @PostMapping("/addService")
    public ResponseEntity<ResponseResult<ExpertAddServiceOutDto>>
    addService(@RequestBody ExpertAddServiceInDto request)
            throws EntityNotFoundException, DuplicateEntityException, ExpertNotAcceptedException, UserNotLoggedInException, BadEntryException {
        HttpStatus status = HttpStatus.ACCEPTED;
        ExpertAddServiceOutDto result = expertService.addService(request);
        ResponseResult<ExpertAddServiceOutDto> response = ResponseResult.<ExpertAddServiceOutDto>builder()
                .code(1)
                .message("Service added to the expert.")
                .build();
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/suggestions")
    public ResponseEntity<ResponseResult<ExpertAddSuggestionOutDto>>
    suggest(@RequestBody ExpertAddSuggestionInDto request) throws Exception {
        ExpertAddSuggestionOutDto result = expertService.suggest(request);
        ResponseResult<ExpertAddSuggestionOutDto> response = ResponseResult.<ExpertAddSuggestionOutDto>builder()
                .code(1)
                .message("Suggestion added successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/suggestions/{status}")
    public ResponseEntity<ResponseResult<CustomPage<ExpertSuggestionOutDto>>>
    getSuggestions(@PathVariable String status, Pageable pageable)
            throws EntityNotFoundException, BadEntryException, UserNotLoggedInException {
        CustomPage<ExpertSuggestionOutDto> result;
        switch (status) {
            case "accepted" -> result = expertService.getSuggestions(pageable, SuggestionStatus.ACCEPTED);
            case "pending" -> result = expertService.getSuggestions(pageable, SuggestionStatus.PENDING);
            case "rejected" -> result = expertService.getSuggestions(pageable, SuggestionStatus.REJECTED);
            case "all" -> result = expertService.getSuggestions(pageable, SuggestionStatus.values());
            default -> throw new BadEntryException("Status is Wrong!");
        }
        ResponseResult<CustomPage<ExpertSuggestionOutDto>> response =
                ResponseResult.<CustomPage<ExpertSuggestionOutDto>>builder()
                        .code(1)
                        .message("Suggestions loaded successfully.")
                        .data(result)
                        .build();
        HttpStatus httpStatus = HttpStatus.CREATED;
        return ResponseEntity.status(httpStatus).body(response);
    }

    @PostMapping("/suggestions/{suggestionId}/{answer}")
    public ResponseEntity<ResponseResult<SuggestionAnswerOutDto>>
    answerSuggestion(@PathVariable Long suggestionId, @PathVariable String answer)
            throws EntityNotFoundException, BadEntryException, UserNotLoggedInException {
        SuggestionAnswerOutDto result;
        switch (answer) {
            case "accept" -> result = expertService.answerSuggestion(suggestionId, BUSY);
            case "reject" -> result = expertService.answerSuggestion(suggestionId, CANCELLED);
            default -> throw new BadEntryException("Status is Wrong!");
        }
        ResponseResult<SuggestionAnswerOutDto> response =
                ResponseResult.<SuggestionAnswerOutDto>builder()
                        .code(1)
                        .message("Suggestions answered successfully.")
                        .data(result)
                        .build();
        HttpStatus httpStatus = HttpStatus.CREATED;
        return ResponseEntity.status(httpStatus).body(response);
    }

    @PostMapping("/suggestions/{suggestionId}/start")
    public ResponseEntity<ResponseResult<EntityOutDto>> startToWork(@PathVariable Long suggestionId) throws BusyOrderException, DuplicateEntityException, NameNotValidException, BadEntryException, EmailNotValidException, PasswordNotValidException, NullFieldException, EntityNotFoundException, UserNotLoggedInException {
        EntityOutDto result = expertService.startToWork(suggestionId);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Work started successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/suggestions/{suggestionId}/done")
    public ResponseEntity<ResponseResult<EntityOutDto>> finishWork(@PathVariable Long suggestionId) throws BusyOrderException, DuplicateEntityException, NameNotValidException, BadEntryException, EmailNotValidException, PasswordNotValidException, NullFieldException, EntityNotFoundException, UserNotLoggedInException {
        EntityOutDto result = expertService.finishWork(suggestionId);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Work finished successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<ResponseResult<CustomPage<TransactionOutDto>>> showTransactions(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        CustomPage<TransactionOutDto> result = userService.getTransactions(pageable);
        ResponseResult<CustomPage<TransactionOutDto>> response = ResponseResult.<CustomPage<TransactionOutDto>>builder()
                .code(1)
                .message("Loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/transactions/{transaction}")
    public ResponseEntity<ResponseResult<TransactionOutDto>> showTransaction(@PathVariable Long transaction) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        TransactionOutDto result = userService.getTransaction(transaction);
        ResponseResult<TransactionOutDto> response = ResponseResult.<TransactionOutDto>builder()
                .code(1)
                .message("Loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/comments")
    public ResponseEntity<ResponseResult<CustomPage<CommentOutDto>>> showComments(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        CustomPage<CommentOutDto> result = userService.getComments(pageable);
        ResponseResult<CustomPage<CommentOutDto>> response = ResponseResult.<CustomPage<CommentOutDto>>builder()
                .code(1)
                .message("Loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/comments/{comment}")
    public ResponseEntity<ResponseResult<CommentOutDto>> showComment(@PathVariable Long comment) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException {
        CommentOutDto result = expertService.getComment(comment);
        ResponseResult<CommentOutDto> response = ResponseResult.<CommentOutDto>builder()
                .code(1)
                .message("Loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<ResponseResult<CustomPage<OrderFinishOutDto>>> showOrders(Pageable pageable) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException {
        CustomPage<OrderFinishOutDto> result = expertService.getOrders(pageable);
        ResponseResult<CustomPage<OrderFinishOutDto>> response = ResponseResult.<CustomPage<OrderFinishOutDto>>builder()
                .code(1)
                .message("List of orders loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/credit")
    public ResponseEntity<ResponseResult<UserCreditOutDto>> showCredit() throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        UserCreditOutDto result = userService.loadCredit();
        ResponseResult<UserCreditOutDto> response = ResponseResult.<UserCreditOutDto>builder()
                .code(1)
                .message("Loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/credit")
    public ResponseEntity<ResponseResult<UserIncreaseCreditOutDto>> addCredit(@RequestBody UserIncreaseCreditInDto request) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        UserIncreaseCreditOutDto result = userService.increaseCredit(request);
        ResponseResult<UserIncreaseCreditOutDto> response = ResponseResult.<UserIncreaseCreditOutDto>builder()
                .code(1)
                .message("Credit increased successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }
}
