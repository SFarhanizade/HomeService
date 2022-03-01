package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.*;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.CommentService;
import ir.farhanizade.homeservice.service.ExpertService;
import ir.farhanizade.homeservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ExpertService expertService;
    private final CommentService commentService;

    @GetMapping
    public String get(){
        return "test";
    }

    @PostMapping("sign-up")
    public ResponseEntity<ResponseResult<UUIDOutDto>>
    create(@RequestBody UserInDto user) throws DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, NullFieldException, UnsupportedEncodingException, NoSuchAlgorithmException {
        ResponseResult<UUIDOutDto> response = ResponseResult.<UUIDOutDto>builder()
                .code(1)
                .message("User saved successfully!")
                .build();
        HttpStatus status = HttpStatus.CREATED;
        UUIDOutDto result = userService.save(user);
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/verify/{uuid}")
    public ResponseEntity<ResponseResult<EntityOutDto>> verifyEmail(@PathVariable String uuid) throws UUIDNotFoundException, UserNotLoggedInException, BadEntryException, EntityNotFoundException {
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("User verified successfully!")
                .build();
        HttpStatus status = HttpStatus.ACCEPTED;
        EntityOutDto result = userService.verifyEmail(uuid);
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @PostMapping("/changePassword")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EXPERT') or hasRole('CUSTOMER')")
    public ResponseEntity<ResponseResult<EntityOutDto>> changePassword(@RequestBody UserPasswordInDto user) throws PasswordNotValidException, WrongPasswordException, EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Password changed successfully!")
                .build();
        HttpStatus status = HttpStatus.ACCEPTED;
        EntityOutDto result = userService.changePassword(user);
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasAnyAuthority('TRANSACTION_READ')")
    public ResponseEntity<ResponseResult<CustomPage<TransactionOutDto>>> showTransactions(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
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
    @PreAuthorize("hasAnyAuthority('TRANSACTION_READ')")
    public ResponseEntity<ResponseResult<TransactionOutDto>> showTransaction(@PathVariable Long transaction) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
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
    @PreAuthorize("hasAnyAuthority('COMMENT_READ')")
    public ResponseEntity<ResponseResult<CustomPage<CommentOutDto>>> showComments(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
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
    @PreAuthorize("hasAnyAuthority('COMMENT_READ')")
    public ResponseEntity<ResponseResult<CommentOutDto>> showComment(@PathVariable Long comment) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, AccountIsLockedException {
        CommentOutDto result = commentService.getCommentById(comment);
        ResponseResult<CommentOutDto> response = ResponseResult.<CommentOutDto>builder()
                .code(1)
                .message("Loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/credit")
    @PreAuthorize("hasAnyAuthority('CREDIT_READ')")
    public ResponseEntity<ResponseResult<UserCreditOutDto>> showCredit() throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
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
    @PreAuthorize("hasAnyAuthority('COMMENT_WRITE')")
    public ResponseEntity<ResponseResult<UserIncreaseCreditOutDto>> addCredit(@RequestBody UserIncreaseCreditInDto request) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        UserIncreaseCreditOutDto result = userService.increaseCredit(request);
        ResponseResult<UserIncreaseCreditOutDto> response = ResponseResult.<UserIncreaseCreditOutDto>builder()
                .code(1)
                .message("Credit increased successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/addService")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EXPERT')")
    public ResponseEntity<ResponseResult<ExpertAddServiceOutDto>>
    addService(@RequestBody ExpertAddServiceInDto request)
            throws EntityNotFoundException, DuplicateEntityException, ExpertNotAcceptedException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        HttpStatus status = HttpStatus.ACCEPTED;
        ExpertAddServiceOutDto result = expertService.addService(request);
        ResponseResult<ExpertAddServiceOutDto> response = ResponseResult.<ExpertAddServiceOutDto>builder()
                .code(1)
                .message("Service added to the expert.")
                .build();
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }
}


