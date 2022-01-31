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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final ExpertService expertService;
    private final OrderService orderService;
    private final SuggestionService suggestionService;
    private final CustomerService customerService;
    private final CommentService commentService;

    @PostMapping
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
    public ResponseEntity<ResponseResult<CustomPage<OrderOutDto>>> showOrders(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        CustomPage<OrderOutDto> result = orderService.findAllByCustomerId(pageable);
        ResponseResult<CustomPage<OrderOutDto>> response = ResponseResult.<CustomPage<OrderOutDto>>builder()
                .code(1)
                .message("List of orders loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{order}")
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

    @GetMapping("/time")
    public ResponseEntity<ResponseResult<CustomPage<OrderOfUserOutDto>>> getOrders(@RequestBody TimeRangeInDto timeRange, Pageable pageable) throws EntityNotFoundException {
        ResponseResult<CustomPage<OrderOfUserOutDto>> response = ResponseResult.<CustomPage<OrderOfUserOutDto>>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        CustomPage<OrderOfUserOutDto> data = orderService.getOrdersByRangeOfTime(timeRange.getStartTime(), timeRange.getEndTime(), pageable);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ResponseResult<CustomPage<OrderOfUserOutDto>>> getOrders(@PathVariable Integer status, Pageable pageable) throws EntityNotFoundException {
        ResponseResult<CustomPage<OrderOfUserOutDto>> response = ResponseResult.<CustomPage<OrderOfUserOutDto>>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        CustomPage<OrderOfUserOutDto> data = orderService.getOrdersByStatus(status, pageable);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mainService/{id}")
    public ResponseEntity<ResponseResult<CustomPage<OrderOfUserOutDto>>> getOrders(@PathVariable Long id, Pageable pageable) throws EntityNotFoundException {
        ResponseResult<CustomPage<OrderOfUserOutDto>> response = ResponseResult.<CustomPage<OrderOfUserOutDto>>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        CustomPage<OrderOfUserOutDto> data = orderService.getOrdersByMainService(id, pageable);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subService/{id}")
    public ResponseEntity<ResponseResult<CustomPage<OrderOfUserOutDto>>> getOrdersBySubService(@PathVariable Long id, Pageable pageable) throws EntityNotFoundException {
        ResponseResult<CustomPage<OrderOfUserOutDto>> response = ResponseResult.<CustomPage<OrderOfUserOutDto>>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        CustomPage<OrderOfUserOutDto> data = orderService.getOrdersBySubService(id, pageable);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report/messages")
    public ResponseEntity<ResponseResult<RequestAndSuggestionReportOutDto>> getNumberOfRequestsAndSuggestions() {
        ResponseResult<RequestAndSuggestionReportOutDto> response = ResponseResult.<RequestAndSuggestionReportOutDto>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        RequestAndSuggestionReportOutDto data = orderService.getNumberOfRequestsAndSuggestions();
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report/done")
    public ResponseEntity<ResponseResult<Long>> getNumberOfDoneOrders() {
        ResponseResult<Long> response = ResponseResult.<Long>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        Long data = orderService.getNumberOfDoneOrders();
        response.setData(data);
        return ResponseEntity.ok(response);
    }

}
