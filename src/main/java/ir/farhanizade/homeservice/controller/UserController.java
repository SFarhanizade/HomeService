package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserPasswordInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.UserSearchOutDto;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.User;
import ir.farhanizade.homeservice.service.CustomerService;
import ir.farhanizade.homeservice.service.ExpertService;
import ir.farhanizade.homeservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final CustomerService customerService;
    private final ExpertService expertService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseResult<EntityOutDto>> create(@RequestBody UserInDto user) {
        User result = new User();
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("User saved successfully!")
                .build();
        HttpStatus status = HttpStatus.CREATED;
        try {
            if ("expert".equals(user.getType())) {
                result = expertService.save(user);
            } else if ("customer".equals(user.getType())) {
                result = customerService.save(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(-1);
            response.setMessage(e.getMessage());
            status = HttpStatus.NOT_ACCEPTABLE;
        }

        EntityOutDto entityOutDto = new EntityOutDto(result.getId());
        response.setData(entityOutDto);
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<ResponseResult<EntityOutDto>> changePassword(@RequestBody UserPasswordInDto user) {
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Password changed successfully!")
                .build();
        HttpStatus status = HttpStatus.ACCEPTED;
        try {
            userService.changePassword(user);
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(-1);
            response.setMessage(e.getMessage());
            status = HttpStatus.NOT_ACCEPTABLE;
        }
        EntityOutDto entityOutDto = new EntityOutDto(user.getId());
        response.setData(entityOutDto);
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping
    public ResponseEntity<ResponseResult<List<UserSearchOutDto>>> search(@RequestBody UserSearchInDto user){
        ResponseResult<List<UserSearchOutDto>> response = ResponseResult.<List<UserSearchOutDto>>builder()
                .code(1)
                .message("Done!")
                .build();
        List<Expert> resultList = new ArrayList<>();
        if("expert".equals(user.getType())){
            resultList = expertService.search(user);
        }
        List<UserSearchOutDto> result = resultList.stream()
                .map(e -> new UserSearchOutDto().convert2Dto(e))
                .collect(Collectors.toList());
        response.setData(result);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}


