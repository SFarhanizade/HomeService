package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserPasswordInDto;
import ir.farhanizade.homeservice.dto.out.UserOutDto;
import ir.farhanizade.homeservice.entity.user.User;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.CustomerService;
import ir.farhanizade.homeservice.service.ExpertService;
import ir.farhanizade.homeservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final CustomerService customerService;
    private final ExpertService expertService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseResult<UserOutDto>> create(@RequestBody UserInDto user) {
        User result = new User();
        ResponseResult<UserOutDto> response = ResponseResult.<UserOutDto>builder()
                .code(1)
                .message("User saved successfully!")
                .build();
        HttpStatus status = HttpStatus.CREATED;
        try {
            if ("expert".equals(user.getType())) {
                result = expertService.save(user.convert2Expert());
            } else if ("customer".equals(user.getType())) {
                result = customerService.save(user.convert2Customer());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(-1);
            response.setMessage(e.getMessage());
            status = HttpStatus.NOT_ACCEPTABLE;
        }

        UserOutDto userOutDto = new UserOutDto(user.getType(), result.getId());
        response.setData(userOutDto);
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<ResponseResult<UserOutDto>> changePassword(@RequestBody UserPasswordInDto user) {
        ResponseResult<UserOutDto> response = ResponseResult.<UserOutDto>builder()
                .code(1)
                .message("Password changed successfully!")
                .build();
        HttpStatus status = HttpStatus.ACCEPTED;
        try {
            userService.changePassword(user.getId(), user.getCurrentPassword(), user.getNewPassword());
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(-1);
            response.setMessage(e.getMessage());
            status = HttpStatus.NOT_ACCEPTABLE;
        }
        UserOutDto userOutDto = new UserOutDto("User", user.getId());
        response.setData(userOutDto);
        return ResponseEntity.status(status).body(response);
    }
}


