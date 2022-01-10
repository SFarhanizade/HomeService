package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.OrderOutDto;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.CustomerService;
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
}
