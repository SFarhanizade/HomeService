package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.*;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final SuggestionService suggestionService;
    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ORDER_WRITE')")
    public ResponseEntity<ResponseResult<EntityOutDto>> request(@RequestBody RequestInDto request) throws NameNotValidException, NullFieldException, BadEntryException, EmailNotValidException, PasswordNotValidException, EntityNotFoundException, UserNotLoggedInException, AccountIsLockedException {
        EntityOutDto result = orderService.request(request);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Order saved successfully!")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{order}/suggestion")
    @PreAuthorize("hasAnyAuthority('SUGGESTION_WRITE')")
    public ResponseEntity<ResponseResult<ExpertAddSuggestionOutDto>>
    suggest(@PathVariable Long order, @RequestBody ExpertAddSuggestionInDto request) throws Exception {
        ExpertAddSuggestionOutDto result = orderService.suggest(order, request);
        ResponseResult<ExpertAddSuggestionOutDto> response = ResponseResult.<ExpertAddSuggestionOutDto>builder()
                .code(1)
                .message("Suggestion added successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ORDER_WRITE')")
    public ResponseEntity<ResponseResult<CustomPage<OrderOfUserOutDto>>> showOrders(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        CustomPage<OrderOfUserOutDto> result = orderService.getOrders(pageable);
        ResponseResult<CustomPage<OrderOfUserOutDto>> response = ResponseResult.<CustomPage<OrderOfUserOutDto>>builder()
                .code(1)
                .message("List of orders loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{order}")
    @PreAuthorize("hasAnyAuthority('ORDER_WRITE')")
    public ResponseEntity<ResponseResult<OrderOutDto>> showOrder(@PathVariable Long order) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        OrderOutDto result = orderService.findByIdAndCustomerId(order);
        ResponseResult<OrderOutDto> response = ResponseResult.<OrderOutDto>builder()
                .code(1)
                .message("Order loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{order}/remove")
    @PreAuthorize("hasAnyAuthority('ORDER_WRITE')")
    public ResponseEntity<ResponseResult<EntityOutDto>> removeOrder(@PathVariable Long order) throws EntityNotFoundException, BadEntryException, UserNotLoggedInException, AccountIsLockedException {
        EntityOutDto result = orderService.removeOrderByIdAndOwnerId(order);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Order removed successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/suggestions")
    @PreAuthorize("hasAnyAuthority('SUGGESTION_READ')")
    public ResponseEntity<ResponseResult<CustomPage<SuggestionOutDto>>> showAllSuggestions(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        CustomPage<SuggestionOutDto> result = suggestionService.findAllByCustomerId(pageable);
        ResponseResult<CustomPage<SuggestionOutDto>> response = ResponseResult.<CustomPage<SuggestionOutDto>>builder()
                .code(1)
                .message("List of suggestions loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{order}/suggestions")
    @PreAuthorize("hasAnyAuthority('ORDER_WRITE')")
    public ResponseEntity<ResponseResult<CustomPage<SuggestionOutDto>>> showSuggestionsByOrder(@PathVariable Long order, Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        CustomPage<SuggestionOutDto> result = orderService.getSuggestionsByOrder(order, pageable);
        ResponseResult<CustomPage<SuggestionOutDto>> response = ResponseResult.<CustomPage<SuggestionOutDto>>builder()
                .code(1)
                .message("List of suggestions loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/suggestions/{suggestion}")
    @PreAuthorize("hasAnyAuthority('SUGGESTION_READ')")
    public ResponseEntity<ResponseResult<SuggestionOutDto>> showSuggestion(@PathVariable Long suggestion) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        SuggestionOutDto result = suggestionService.getById(suggestion);
        ResponseResult<SuggestionOutDto> response = ResponseResult.<SuggestionOutDto>builder()
                .code(1)
                .message("List of suggestions loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/suggestions/{suggestion}/accept")
    @PreAuthorize("hasAnyAuthority('ORDER_WRITE')")
    public ResponseEntity<ResponseResult<EntityOutDto>> acceptSuggestion(@PathVariable Long suggestion) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, UserNotLoggedInException, AccountIsLockedException {
        EntityOutDto result = orderService.acceptSuggestion(suggestion);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Suggestion accepted successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/suggestions/{suggestion}/pay/{method}")
    @PreAuthorize("hasAnyAuthority('ORDER_WRITE')")
    public ResponseEntity<ResponseResult<EntityOutDto>> pay(@PathVariable Long suggestion, @PathVariable String method) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, NotEnoughMoneyException, UserNotLoggedInException, AccountIsLockedException {
        EntityOutDto result = customerService.pay(suggestion, method);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Paid successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/suggestions/{suggestion}/comment")
    @PreAuthorize("hasAnyAuthority('ORDER_WRITE')")
    public ResponseEntity<ResponseResult<EntityOutDto>> comment(@PathVariable Long suggestion, @RequestBody CommentInDto comment) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, NotEnoughMoneyException, UserNotLoggedInException, AccountIsLockedException {
        EntityOutDto result = customerService.comment(suggestion, comment);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Commented successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("experts")
    @PreAuthorize("hasAnyAuthority('SUGGESTION_WRITE')")
    public ResponseEntity<ResponseResult<CustomPage<OrderOutDto>>> showList(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        CustomPage<OrderOutDto> result = null;
        result = orderService.loadAvailableOrders(pageable);
        ResponseResult<CustomPage<OrderOutDto>> response = ResponseResult.<CustomPage<OrderOutDto>>builder()
                .code(1)
                .message("List of orders loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

}
