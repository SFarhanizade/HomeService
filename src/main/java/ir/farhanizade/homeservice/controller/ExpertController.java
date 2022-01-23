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

import java.util.List;

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
    public ResponseEntity<ResponseResult<EntityOutDto>>
    create(@RequestBody UserInDto user) throws Exception {
        return userController.create(user, Expert.class);
    }

    @PostMapping("/addService")
    public ResponseEntity<ResponseResult<ExpertAddServiceOutDto>>
    addService(@RequestBody ExpertAddServiceInDto request)
            throws EntityNotFoundException, DuplicateEntityException, ExpertNotAcceptedException {
        HttpStatus status = HttpStatus.ACCEPTED;
        ExpertAddServiceOutDto result = expertService.addService(request);
        ResponseResult<ExpertAddServiceOutDto> response = ResponseResult.<ExpertAddServiceOutDto>builder()
                .code(1)
                .message("Service added to the expert.")
                .build();
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{id}/suggestions")
    public ResponseEntity<ResponseResult<ExpertAddSuggestionOutDto>>
    suggest(@PathVariable Long id, @RequestBody ExpertAddSuggestionInDto request) throws Exception {
        ExpertAddSuggestionOutDto result = expertService.suggest(id, request);
        ResponseResult<ExpertAddSuggestionOutDto> response = ResponseResult.<ExpertAddSuggestionOutDto>builder()
                .code(1)
                .message("Suggestion added successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/suggestions/{status}")
    public ResponseEntity<ResponseResult<CustomPage<ExpertSuggestionOutDto>>>
    getSuggestions(@PathVariable Long id, @PathVariable String status, Pageable pageable)
            throws EntityNotFoundException, BadEntryException {
        CustomPage<ExpertSuggestionOutDto> result;
        switch (status) {
            case "accepted" -> result = expertService.getSuggestions(id, pageable, SuggestionStatus.ACCEPTED);
            case "pending" -> result = expertService.getSuggestions(id, pageable, SuggestionStatus.PENDING);
            case "rejected" -> result = expertService.getSuggestions(id, pageable, SuggestionStatus.REJECTED);
            case "all" -> result = expertService.getSuggestions(id, pageable, SuggestionStatus.values());
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

    @PostMapping("/{id}/suggestions/{suggestionId}/{answer}")
    public ResponseEntity<ResponseResult<SuggestionAnswerOutDto>>
    answerSuggestion(@PathVariable Long id, @PathVariable Long suggestionId, @PathVariable String answer)
            throws EntityNotFoundException, BadEntryException {
        SuggestionAnswerOutDto result;
        switch (answer) {
            case "accept" -> result = expertService.answerSuggestion(id, suggestionId, BUSY);
            case "reject" -> result = expertService.answerSuggestion(id, suggestionId, CANCELLED);
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

    @PostMapping("/{id}/suggestions/{suggestionId}/start")
    public ResponseEntity<ResponseResult<EntityOutDto>> startToWork(@PathVariable Long id, @PathVariable Long suggestionId) throws BusyOrderException, DuplicateEntityException, NameNotValidException, BadEntryException, EmailNotValidException, PasswordNotValidException, NullFieldException, EntityNotFoundException {
        EntityOutDto result = expertService.startToWork(id, suggestionId);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Work started successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }
    
    @PostMapping("/{id}/suggestions/{suggestionId}/done")
    public ResponseEntity<ResponseResult<EntityOutDto>> finishWork(@PathVariable Long id, @PathVariable Long suggestionId) throws BusyOrderException, DuplicateEntityException, NameNotValidException, BadEntryException, EmailNotValidException, PasswordNotValidException, NullFieldException, EntityNotFoundException {
        EntityOutDto result = expertService.finishWork(id, suggestionId);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Work finished successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<ResponseResult<CustomPage<TransactionOutDto>>> showTransactions(@PathVariable Long id, Pageable pageable) throws EntityNotFoundException {
        CustomPage<TransactionOutDto> result = userService.getTransactions(id, pageable);
        ResponseResult<CustomPage<TransactionOutDto>> response = ResponseResult.<CustomPage<TransactionOutDto>>builder()
                .code(1)
                .message("Loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/transactions/{transaction}")
    public ResponseEntity<ResponseResult<TransactionOutDto>> showTransaction(@PathVariable Long id, @PathVariable Long transaction) throws EntityNotFoundException {
        TransactionOutDto result = userService.getTransaction(id, transaction);
        ResponseResult<TransactionOutDto> response = ResponseResult.<TransactionOutDto>builder()
                .code(1)
                .message("Loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<ResponseResult<CustomPage<CommentOutDto>>> showComments(@PathVariable Long id, Pageable pageable) throws EntityNotFoundException {
        CustomPage<CommentOutDto> result = userService.getComments(id, pageable);
        ResponseResult<CustomPage<CommentOutDto>> response = ResponseResult.<CustomPage<CommentOutDto>>builder()
                .code(1)
                .message("Loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/comments/{comment}")
    public ResponseEntity<ResponseResult<CommentOutDto>> showComment(@PathVariable Long id, @PathVariable Long comment) {
        CommentOutDto result = expertService.getComment(id, comment);
        ResponseResult<CommentOutDto> response = ResponseResult.<CommentOutDto>builder()
                .code(1)
                .message("Loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<ResponseResult<CustomPage<OrderFinishOutDto>>> showOrders(@PathVariable Long id, Pageable pageable) {
        CustomPage<OrderFinishOutDto> result = expertService.getOrders(id, pageable);
        ResponseResult<CustomPage<OrderFinishOutDto>> response = ResponseResult.<CustomPage<OrderFinishOutDto>>builder()
                .code(1)
                .message("List of orders loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/credit")
    public ResponseEntity<ResponseResult<UserCreditOutDto>> showCredit(@PathVariable Long id) throws EntityNotFoundException {
        UserCreditOutDto result = userService.loadCreditById(id);
        ResponseResult<UserCreditOutDto> response = ResponseResult.<UserCreditOutDto>builder()
                .code(1)
                .message("Loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{id}/credit")
    public ResponseEntity<ResponseResult<UserIncreaseCreditOutDto>> addCredit(@PathVariable Long id, @RequestBody UserIncreaseCreditInDto request) throws EntityNotFoundException {
        UserIncreaseCreditOutDto result = userService.increaseCredit(id, request);
        ResponseResult<UserIncreaseCreditOutDto> response = ResponseResult.<UserIncreaseCreditOutDto>builder()
                .code(1)
                .message("Credit increased successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }
}
