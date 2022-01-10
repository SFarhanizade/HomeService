package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.RequestInDto;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.OrderOutDto;
import ir.farhanizade.homeservice.dto.out.SuggestionOutDto;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.CustomerService;
import ir.farhanizade.homeservice.service.RequestService;
import ir.farhanizade.homeservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final UserController userController;
    private final CustomerService customerService;
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<ResponseResult<EntityOutDto>> create(@RequestBody UserInDto user) throws DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, NullFieldException {
        return userController.create(user, Customer.class);
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<ResponseResult<List<OrderOutDto>>> showOrders(@PathVariable Long id) throws EntityNotFoundException {
        List<OrderOutDto> result = customerService.getOrders(id);
        ResponseResult<List<OrderOutDto>> response = ResponseResult.<List<OrderOutDto>>builder()
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

    @GetMapping("/{id}/orders/{order}/suggestions")
    public ResponseEntity<ResponseResult<List<SuggestionOutDto>>> showSuggestionsByOrder(@PathVariable Long id, @PathVariable Long order) throws EntityNotFoundException {
        List<SuggestionOutDto> result = customerService.getSuggestionsByOrder(id, order);
        ResponseResult<List<SuggestionOutDto>> response = ResponseResult.<List<SuggestionOutDto>>builder()
                .code(1)
                .message("List of suggestions loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/suggestions")
    public ResponseEntity<ResponseResult<List<SuggestionOutDto>>> showAllSuggestions(@PathVariable Long id) throws EntityNotFoundException {
        List<SuggestionOutDto> result = customerService.getSuggestions(id);
        ResponseResult<List<SuggestionOutDto>> response = ResponseResult.<List<SuggestionOutDto>>builder()
                .code(1)
                .message("List of suggestions loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/suggestions/{suggestion}")
    public ResponseEntity<ResponseResult<SuggestionOutDto>> showSuggestion(@PathVariable Long id, @PathVariable Long suggestion) throws EntityNotFoundException {
        SuggestionOutDto result = customerService.getSuggestion(id,suggestion);
        ResponseResult<SuggestionOutDto> response = ResponseResult.<SuggestionOutDto>builder()
                .code(1)
                .message("List of suggestions loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }
}
