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

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final UserController userController;
    private final CustomerService customerService;
    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<ResponseResult<UUIDOutDto>> create(@RequestBody UserInDto user) throws DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, NullFieldException, UnsupportedEncodingException, NoSuchAlgorithmException {
        return userController.create(user, Customer.class);
    }

    @GetMapping("/orders")
    public ResponseEntity<ResponseResult<CustomPage<OrderOutDto>>> showOrders(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        CustomPage<OrderOutDto> result = customerService.getOrders(pageable);
        ResponseResult<CustomPage<OrderOutDto>> response = ResponseResult.<CustomPage<OrderOutDto>>builder()
                .code(1)
                .message("List of orders loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/orders")
    public ResponseEntity<ResponseResult<EntityOutDto>> request(@RequestBody RequestInDto request) throws NameNotValidException, NullFieldException, BadEntryException, EmailNotValidException, PasswordNotValidException, EntityNotFoundException, UserNotLoggedInException {
        EntityOutDto result = customerService.request(request);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Order saved successfully!")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/orders/{order}")
    public ResponseEntity<ResponseResult<OrderOutDto>> showOrder(@PathVariable Long order) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        OrderOutDto result = customerService.getOrder(order);
        ResponseResult<OrderOutDto> response = ResponseResult.<OrderOutDto>builder()
                .code(1)
                .message("Order loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/orders/{order}/remove")
    public ResponseEntity<ResponseResult<EntityOutDto>> removeOrder(@PathVariable Long order) throws EntityNotFoundException, BadEntryException, UserNotLoggedInException {
        EntityOutDto result = customerService.removeOrder(order);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Order removed successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/orders/{order}/suggestions")
    public ResponseEntity<ResponseResult<CustomPage<SuggestionOutDto>>> showSuggestionsByOrder(@PathVariable Long order, Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        CustomPage<SuggestionOutDto> result = customerService.getSuggestionsByOrder(order, pageable);
        ResponseResult<CustomPage<SuggestionOutDto>> response = ResponseResult.<CustomPage<SuggestionOutDto>>builder()
                .code(1)
                .message("List of suggestions loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<ResponseResult<CustomPage<SuggestionOutDto>>> showAllSuggestions(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        CustomPage<SuggestionOutDto> result = customerService.getSuggestions(pageable);
        ResponseResult<CustomPage<SuggestionOutDto>> response = ResponseResult.<CustomPage<SuggestionOutDto>>builder()
                .code(1)
                .message("List of suggestions loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/suggestions/{suggestion}")
    public ResponseEntity<ResponseResult<SuggestionOutDto>> showSuggestion(@PathVariable Long suggestion) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        SuggestionOutDto result = customerService.getSuggestion(suggestion);
        ResponseResult<SuggestionOutDto> response = ResponseResult.<SuggestionOutDto>builder()
                .code(1)
                .message("List of suggestions loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/suggestions/{suggestion}/accept")
    public ResponseEntity<ResponseResult<EntityOutDto>> acceptSuggestion(@PathVariable Long suggestion) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, UserNotLoggedInException {
        EntityOutDto result = customerService.acceptSuggestion(suggestion);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("List of suggestions loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/suggestions/{suggestion}/pay/{method}")
    public ResponseEntity<ResponseResult<EntityOutDto>> pay(@PathVariable Long suggestion, @PathVariable String method) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, NotEnoughMoneyException, UserNotLoggedInException {
        EntityOutDto result = customerService.pay(suggestion, method);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Paid successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<ResponseResult<CustomPage<TransactionOutDto>>> showTransactions(Pageable pageable) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, NotEnoughMoneyException, UserNotLoggedInException {
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
    public ResponseEntity<ResponseResult<TransactionOutDto>> showTransaction(@PathVariable Long transaction) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, NotEnoughMoneyException, UserNotLoggedInException {
        TransactionOutDto result = userService.getTransaction(transaction);
        ResponseResult<TransactionOutDto> response = ResponseResult.<TransactionOutDto>builder()
                .code(1)
                .message("Loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/suggestions/{suggestion}/comment")
    public ResponseEntity<ResponseResult<EntityOutDto>> comment(@PathVariable Long suggestion, @RequestBody CommentInDto comment) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, NotEnoughMoneyException, UserNotLoggedInException {
        EntityOutDto result = customerService.comment(suggestion, comment);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Commented successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/comments")
    public ResponseEntity<ResponseResult<CustomPage<CommentOutDto>>> showComments(Pageable pageable) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException {
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
    public ResponseEntity<ResponseResult<CommentOutDto>> showComment(@PathVariable Long comment) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, NotEnoughMoneyException, UserNotLoggedInException {
        CommentOutDto result = customerService.getComment(comment);
        ResponseResult<CommentOutDto> response = ResponseResult.<CommentOutDto>builder()
                .code(1)
                .message("Loaded successfully.")
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
