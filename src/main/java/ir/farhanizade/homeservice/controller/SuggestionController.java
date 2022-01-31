package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.ExpertSuggestionOutDto;
import ir.farhanizade.homeservice.dto.out.SuggestionAnswerOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.SuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus.BUSY;
import static ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus.CANCELLED;

@RestController
@RequestMapping("/suggestions")
@RequiredArgsConstructor
public class SuggestionController {
    private final SuggestionService suggestionService;

    @GetMapping("/status/{status}")
    public ResponseEntity<ResponseResult<CustomPage<ExpertSuggestionOutDto>>>
    getSuggestions(@PathVariable String status, Pageable pageable)
            throws EntityNotFoundException, BadEntryException, UserNotLoggedInException, AccountIsLockedException {
        CustomPage<ExpertSuggestionOutDto> result;
        switch (status) {
            case "accepted" -> result = suggestionService.findAllByOwnerIdAndStatus(new SuggestionStatus[]{SuggestionStatus.ACCEPTED}, pageable);
            case "pending" -> result = suggestionService.findAllByOwnerIdAndStatus(new SuggestionStatus[]{SuggestionStatus.PENDING}, pageable);
            case "rejected" -> result = suggestionService.findAllByOwnerIdAndStatus(new SuggestionStatus[]{SuggestionStatus.REJECTED}, pageable);
            case "all" -> result = suggestionService.findAllByOwnerIdAndStatus(SuggestionStatus.values(), pageable);
            default -> throw new BadEntryException("Status is Wrong!");
        }
        ResponseResult<CustomPage<ExpertSuggestionOutDto>> response =
                ResponseResult.<CustomPage<ExpertSuggestionOutDto>>builder()
                        .code(1)
                        .message("Suggestions loaded successfully.")
                        .data(result)
                        .build();
        HttpStatus httpStatus = HttpStatus.CREATED;
        return ResponseEntity.status(httpStatus).body(response);
    }

    @PostMapping("/{suggestionId}/{answer}")
    public ResponseEntity<ResponseResult<SuggestionAnswerOutDto>>
    answerSuggestion(@PathVariable Long suggestionId, @PathVariable String answer)
            throws EntityNotFoundException, BadEntryException, UserNotLoggedInException, AccountIsLockedException {
        SuggestionAnswerOutDto result;
        switch (answer) {
            case "accept" -> result = suggestionService.answer(suggestionId, BUSY);
            case "reject" -> result = suggestionService.answer(suggestionId, CANCELLED);
            default -> throw new BadEntryException("Status is Wrong!");
        }
        ResponseResult<SuggestionAnswerOutDto> response =
                ResponseResult.<SuggestionAnswerOutDto>builder()
                        .code(1)
                        .message("Suggestions answered successfully.")
                        .data(result)
                        .build();
        HttpStatus httpStatus = HttpStatus.CREATED;
        return ResponseEntity.status(httpStatus).body(response);
    }

    @PostMapping("/suggestions/{suggestionId}/start")
    public ResponseEntity<ResponseResult<EntityOutDto>> startToWork(@PathVariable Long suggestionId) throws BusyOrderException, DuplicateEntityException, NameNotValidException, BadEntryException, EmailNotValidException, PasswordNotValidException, NullFieldException, EntityNotFoundException, UserNotLoggedInException, AccountIsLockedException {
        EntityOutDto result = suggestionService.startToWork(suggestionId);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Work started successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/suggestions/{suggestionId}/done")
    public ResponseEntity<ResponseResult<EntityOutDto>> finishWork(@PathVariable Long suggestionId) throws BusyOrderException, DuplicateEntityException, NameNotValidException, BadEntryException, EmailNotValidException, PasswordNotValidException, NullFieldException, EntityNotFoundException, UserNotLoggedInException, AccountIsLockedException {
        EntityOutDto result = suggestionService.finishWork(suggestionId);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("Work finished successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }


}
