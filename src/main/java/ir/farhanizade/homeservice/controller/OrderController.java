package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.ExpertAddSuggestionInDto;
import ir.farhanizade.homeservice.dto.in.ExpertInDto;
import ir.farhanizade.homeservice.dto.in.RequestInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.ExpertAddSuggestionOutDto;
import ir.farhanizade.homeservice.dto.out.OrderOutDto;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.ExpertService;
import ir.farhanizade.homeservice.service.OrderService;
import ir.farhanizade.homeservice.service.RequestService;
import ir.farhanizade.homeservice.service.SuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final RequestService requestService;
    private final SuggestionService suggestionService;
    private final ExpertService expertService;

    @PostMapping("/add")
    public ResponseEntity<ResponseResult<EntityOutDto>> create(@RequestBody RequestInDto request) throws NameNotValidException, NullFieldException, BadEntryException, EmailNotValidException, PasswordNotValidException, EntityNotFoundException {
        EntityOutDto result = requestService.save(request);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Order saved successfully!")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping
    public ResponseEntity<ResponseResult<List<OrderOutDto>>> showList(@RequestBody ExpertInDto request) throws EntityNotFoundException {
        List<OrderOutDto> result = expertService.loadAvailableOrders(request);
        ResponseResult<List<OrderOutDto>> response = ResponseResult.<List<OrderOutDto>>builder()
                .code(1)
                .message("List of orders loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/suggest")
    public ResponseEntity<ResponseResult<ExpertAddSuggestionOutDto>> suggest(@RequestBody ExpertAddSuggestionInDto request) throws BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, EntityNotFoundException, DuplicateEntityException {
        ExpertAddSuggestionOutDto result = expertService.suggest(request);
        ResponseResult<ExpertAddSuggestionOutDto> response = ResponseResult.<ExpertAddSuggestionOutDto>builder()
                .code(1)
                .message("Suggestion added successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }
}
