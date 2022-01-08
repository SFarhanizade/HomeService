package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.out.UserOutDto;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.CustomerService;
import ir.farhanizade.homeservice.service.ExpertService;
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

    @PostMapping
    public ResponseEntity<UserOutDto> create(@RequestBody UserInDto user) {
        if ("expert".equals(user.getType())) {
            try {
                expertService.save(user.convert2Expert());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("customer".equals(user.getType())) {
            try {
                customerService.save(user.convert2Customer());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        UserOutDto userOutDto = new UserOutDto(user.getType(), 0L);
        return ResponseEntity.status(HttpStatus.CREATED).body(userOutDto);
    }
}


