package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserPasswordInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.UserSearchOutDto;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.ExpertService;
import ir.farhanizade.homeservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    //private final CustomerService customerService;
    private final ExpertService expertService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseResult<EntityOutDto>> create(@RequestBody UserInDto user) throws DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, NullFieldException {
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("User saved successfully!")
                .build();
        HttpStatus status = HttpStatus.CREATED;
        EntityOutDto result = userService.save(user);
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<ResponseResult<EntityOutDto>> changePassword(@RequestBody UserPasswordInDto user) throws PasswordNotValidException, WrongPasswordException {
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
    public ResponseEntity<ResponseResult<List<UserSearchOutDto>>> search(@RequestBody UserSearchInDto user) {
        ResponseResult<List<UserSearchOutDto>> response = ResponseResult.<List<UserSearchOutDto>>builder()
                .code(1)
                .message("Done!")
                .build();
        List<UserSearchOutDto> result = new ArrayList<>();
            result = userService.search(user);

        response.setData(result);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}


