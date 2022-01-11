package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.ExpertAddServiceInDto;
import ir.farhanizade.homeservice.dto.in.ExpertAddSuggestionInDto;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.ExpertAddServiceOutDto;
import ir.farhanizade.homeservice.dto.out.ExpertAddSuggestionOutDto;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.ExpertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/experts")
@RequiredArgsConstructor
public class ExpertController {
    private final ExpertService expertService;
    private final UserController userController;

    @PostMapping
    public ResponseEntity<ResponseResult<EntityOutDto>> create(@RequestBody UserInDto user) throws DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, NullFieldException {
        return userController.create(user, Expert.class);
    }

    @PostMapping("/addService")
    public ResponseEntity<ResponseResult<ExpertAddServiceOutDto>> addService(@RequestBody ExpertAddServiceInDto request) throws EntityNotFoundException, DuplicateEntityException {
        HttpStatus status = HttpStatus.ACCEPTED;
        ExpertAddServiceOutDto result = expertService.addService(request);
        ResponseResult<ExpertAddServiceOutDto> response = ResponseResult.<ExpertAddServiceOutDto>builder()
                .code(1)
                .message("Service added to the expert.")
                .build();
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{id}/suggest")
    public ResponseEntity<ResponseResult<ExpertAddSuggestionOutDto>> suggest(@PathVariable Long id, @RequestBody ExpertAddSuggestionInDto request) throws BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, EntityNotFoundException, DuplicateEntityException {
        ExpertAddSuggestionOutDto result = expertService.suggest(id,request);
        ResponseResult<ExpertAddSuggestionOutDto> response = ResponseResult.<ExpertAddSuggestionOutDto>builder()
                .code(1)
                .message("Suggestion added successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }
}
