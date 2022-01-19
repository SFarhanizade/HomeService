package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserPasswordInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.OrderOfUserOutDto;
import ir.farhanizade.homeservice.dto.out.UserSearchOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public ResponseEntity<ResponseResult<EntityOutDto>> create(UserInDto user, Class<?> type) throws DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, NullFieldException {
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("User saved successfully!")
                .build();
        HttpStatus status = HttpStatus.CREATED;
        EntityOutDto result = userService.save(user, type);
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<ResponseResult<EntityOutDto>> changePassword(@RequestBody UserPasswordInDto user) throws PasswordNotValidException, WrongPasswordException, EntityNotFoundException {
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Password changed successfully!")
                .build();
        HttpStatus status = HttpStatus.ACCEPTED;
        EntityOutDto result = userService.changePassword(user);
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping
    public ResponseEntity<ResponseResult<CustomPage<UserSearchOutDto>>> search(@RequestBody UserSearchInDto user, Pageable pageable) throws EntityNotFoundException {
        ResponseResult<CustomPage<UserSearchOutDto>> response = ResponseResult.<CustomPage<UserSearchOutDto>>builder()
                .code(1)
                .message("Done!")
                .build();
        CustomPage<UserSearchOutDto> result = userService.search(user, pageable);

        response.setData(result);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<ResponseResult<CustomPage<OrderOfUserOutDto>>> getOrders(@PathVariable Long id, Pageable pageable) throws EntityNotFoundException {
        ResponseResult<CustomPage<OrderOfUserOutDto>> response = ResponseResult.<CustomPage<OrderOfUserOutDto>>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        CustomPage<OrderOfUserOutDto> data = userService.getOrders(id, pageable);
        response.setData(data);
        return ResponseEntity.ok(response);
    }
}


