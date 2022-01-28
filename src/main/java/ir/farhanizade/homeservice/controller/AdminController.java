package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.UserSearchOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.user.Admin;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {
    private final UserController userController;
    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<ResponseResult<EntityOutDto>> create(@RequestBody UserInDto user) throws DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, NullFieldException {
        EntityOutDto data = adminService.save(user);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("User added successfully.")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/experts/{expertId}/accept")
    public ResponseEntity<ResponseResult<EntityOutDto>> acceptExpert(@PathVariable Long expertId) throws UserNotValidException, EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("User accepted successfully!")
                .build();
        HttpStatus status = HttpStatus.CREATED;
        EntityOutDto result = adminService.acceptExpert(expertId);
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseResult<CustomPage<UserSearchOutDto>>> search(@RequestBody UserSearchInDto param, Pageable pageable) throws EntityNotFoundException, UserNotValidException {
        CustomPage<UserSearchOutDto> result = adminService.search(param, pageable);

        ResponseResult<CustomPage<UserSearchOutDto>> response = ResponseResult.<CustomPage<UserSearchOutDto>>builder()
                .data(result)
                .code(1)
                .message("Loaded successfully!")
                .build();

        return ResponseEntity.ok(response);
    }


}
