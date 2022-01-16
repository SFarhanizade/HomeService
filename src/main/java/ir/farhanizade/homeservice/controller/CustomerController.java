package ir.farhanizade.homeservice.controller;

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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final UserController userController;
    private final CustomerService customerService;
    private final UserService userService;
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<ResponseResult<EntityOutDto>> create(@RequestBody UserInDto user) throws DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, NullFieldException {
        return userController.create(user, Customer.class);
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<ResponseResult<CustomPage<OrderOutDto>>> showOrders(@PathVariable Long id, Pageable pageable) throws EntityNotFoundException {
        CustomPage<OrderOutDto> result = customerService.getOrders(id, pageable);
        ResponseResult<CustomPage<OrderOutDto>> response = ResponseResult.<CustomPage<OrderOutDto>>builder()
                .code(1)
                .message("List of orders loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{id}/orders")
    public ResponseEntity<ResponseResult<EntityOutDto>> request(@PathVariable Long id, @RequestBody RequestInDto request) throws NameNotValidException, NullFieldException, BadEntryException, EmailNotValidException, PasswordNotValidException, EntityNotFoundException {
        EntityOutDto result = customerService.request(id, request);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Order saved successfully!")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/orders/{order}")
    public ResponseEntity<ResponseResult<OrderOutDto>> showOrder(@PathVariable Long id, @PathVariable Long order) throws EntityNotFoundException {
        OrderOutDto result = customerService.getOrder(id, order);
        ResponseResult<OrderOutDto> response = ResponseResult.<OrderOutDto>builder()
                .code(1)
                .message("Order loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/orders/{order}/remove")
    public ResponseEntity<ResponseResult<EntityOutDto>> removeOrder(@PathVariable Long id, @PathVariable Long order) throws EntityNotFoundException {
        EntityOutDto result = customerService.removeOrder(id, order);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Order removed successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/orders/{order}/suggestions")
    public ResponseEntity<ResponseResult<CustomPage<SuggestionOutDto>>> showSuggestionsByOrder(@PathVariable Long id, @PathVariable Long order) throws EntityNotFoundException {
        CustomPage<SuggestionOutDto> result = customerService.getSuggestionsByOrder(id, order);
        ResponseResult<CustomPage<SuggestionOutDto>> response = ResponseResult.<CustomPage<SuggestionOutDto>>builder()
                .code(1)
                .message("List of suggestions loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/suggestions")
    public ResponseEntity<ResponseResult<CustomPage<SuggestionOutDto>>> showAllSuggestions(@PathVariable Long id, Pageable pageable) throws EntityNotFoundException {
        CustomPage<SuggestionOutDto> result = customerService.getSuggestions(id, pageable);
        ResponseResult<CustomPage<SuggestionOutDto>> response = ResponseResult.<CustomPage<SuggestionOutDto>>builder()
                .code(1)
                .message("List of suggestions loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/suggestions/{suggestion}")
    public ResponseEntity<ResponseResult<SuggestionOutDto>> showSuggestion(@PathVariable Long id, @PathVariable Long suggestion) throws EntityNotFoundException {
        SuggestionOutDto result = customerService.getSuggestion(id, suggestion);
        ResponseResult<SuggestionOutDto> response = ResponseResult.<SuggestionOutDto>builder()
                .code(1)
                .message("List of suggestions loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/suggestions/{suggestion}/accept")
    public ResponseEntity<ResponseResult<EntityOutDto>> acceptSuggestion(@PathVariable Long id, @PathVariable Long suggestion) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException {
        EntityOutDto result = customerService.acceptSuggestion(id, suggestion);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("List of suggestions loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{id}/suggestions/{suggestion}/pay")
    public ResponseEntity<ResponseResult<EntityOutDto>> pay(@PathVariable Long id, @PathVariable Long suggestion) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, NotEnoughMoneyException {
        EntityOutDto result = customerService.pay(id, suggestion);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Paid successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<ResponseResult<CustomPage<TransactionOutDto>>> showTransactions(@PathVariable Long id, Pageable pageable) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, NotEnoughMoneyException {
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
    public ResponseEntity<ResponseResult<TransactionOutDto>> showTransaction(@PathVariable Long id, @PathVariable Long transaction) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, NotEnoughMoneyException {
        TransactionOutDto result = userService.getTransaction(id, transaction);
        ResponseResult<TransactionOutDto> response = ResponseResult.<TransactionOutDto>builder()
                .code(1)
                .message("Loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{id}/suggestions/{suggestion}/comment")
    public ResponseEntity<ResponseResult<EntityOutDto>> comment(@PathVariable Long id, @PathVariable Long suggestion, @RequestBody CommentInDto comment) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, NotEnoughMoneyException {
        EntityOutDto result = customerService.comment(id, suggestion, comment);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Commented successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<ResponseResult<CustomPage<CommentOutDto>>> showComments(@PathVariable Long id, Pageable pageable) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, NotEnoughMoneyException {
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
    public ResponseEntity<ResponseResult<CommentOutDto>> showComment(@PathVariable Long id,@PathVariable Long comment) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, NotEnoughMoneyException {
        CommentOutDto result = customerService.getComment(id,comment);
        ResponseResult<CommentOutDto> response = ResponseResult.<CommentOutDto>builder()
                .code(1)
                .message("Loaded successfully.")
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
