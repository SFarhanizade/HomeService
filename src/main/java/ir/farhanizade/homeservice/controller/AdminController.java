package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.entity.user.Admin;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.AdminService;
import lombok.RequiredArgsConstructor;
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
        return userController.create(user, Admin.class);
    }

    @GetMapping("/{id}/experts/{expertId}/accept")
    public ResponseEntity<ResponseResult<EntityOutDto>> acceptExpert(@PathVariable Long id, @PathVariable Long expertId) throws UserNotValidException, EntityNotFoundException {
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("User accepted successfully!")
                .build();
        HttpStatus status = HttpStatus.CREATED;
        EntityOutDto result = adminService.acceptExpert(id, expertId);
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }


}
