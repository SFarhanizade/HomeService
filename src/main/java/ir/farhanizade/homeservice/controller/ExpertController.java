package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.ExpertAddServiceInDto;
import ir.farhanizade.homeservice.dto.in.ExpertAddSuggestionInDto;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.ExpertAddServiceOutDto;
import ir.farhanizade.homeservice.dto.out.ExpertAddSuggestionOutDto;
import ir.farhanizade.homeservice.dto.out.ExpertSuggestionOutDto;
import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.ExpertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/experts")
@RequiredArgsConstructor
public class ExpertController {
    private final ExpertService expertService;
    private final UserController userController;

    @PostMapping
    public ResponseEntity<ResponseResult<EntityOutDto>>
    create(@RequestBody UserInDto user) throws Exception {
        return userController.create(user, Expert.class);
    }

    @PostMapping("/addService")
    public ResponseEntity<ResponseResult<ExpertAddServiceOutDto>>
    addService(@RequestBody ExpertAddServiceInDto request)
            throws EntityNotFoundException, DuplicateEntityException {
        HttpStatus status = HttpStatus.ACCEPTED;
        ExpertAddServiceOutDto result = expertService.addService(request);
        ResponseResult<ExpertAddServiceOutDto> response = ResponseResult.<ExpertAddServiceOutDto>builder()
                .code(1)
                .message("Service added to the expert.")
                .build();
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{id}/suggestions")
    public ResponseEntity<ResponseResult<ExpertAddSuggestionOutDto>>
    suggest(@PathVariable Long id, @RequestBody ExpertAddSuggestionInDto request) throws Exception {
        ExpertAddSuggestionOutDto result = expertService.suggest(id, request);
        ResponseResult<ExpertAddSuggestionOutDto> response = ResponseResult.<ExpertAddSuggestionOutDto>builder()
                .code(1)
                .message("Suggestion added successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}/suggestions/{status}")
    public ResponseEntity<ResponseResult<List<ExpertSuggestionOutDto>>>
    getSuggestions(@PathVariable Long id, @PathVariable String status)
            throws EntityNotFoundException, BadEntryException {
        List<ExpertSuggestionOutDto> result;
        switch (status) {
            case "accepted" -> result = expertService.getSuggestions(id, SuggestionStatus.ACCEPTED);
            case "pending" -> result = expertService.getSuggestions(id, SuggestionStatus.PENDING);
            case "rejected" -> result = expertService.getSuggestions(id, SuggestionStatus.REJECTED);
            case "all" -> result = expertService.getSuggestions(id, SuggestionStatus.ACCEPTED,
                    SuggestionStatus.PENDING, SuggestionStatus.REJECTED);
            default -> throw new BadEntryException("Status is Wrong!");
        }
        ResponseResult<List<ExpertSuggestionOutDto>> response =
                ResponseResult.<List<ExpertSuggestionOutDto>>builder()
                        .code(1)
                        .message("Suggestions loaded successfully.")
                        .data(result)
                        .build();
        HttpStatus httpStatus = HttpStatus.CREATED;
        return ResponseEntity.status(httpStatus).body(response);
    }

}
