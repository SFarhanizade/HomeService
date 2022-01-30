package ir.farhanizade.homeservice.controller.error;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(BadEntryException.class)
    protected ResponseEntity<ResponseResult<Void>> handleBadRequest(BadEntryException ex){
        return buildResponseEntity(ex);
    }

    @ExceptionHandler(BusyOrderException.class)
    protected ResponseEntity<ResponseResult<Void>> handleBadRequest(BusyOrderException ex){
        return buildResponseEntity(ex);
    }

    @ExceptionHandler(DuplicateEntityException.class)
    protected ResponseEntity<ResponseResult<Void>> handleBadRequest(DuplicateEntityException ex){
        return buildResponseEntity(ex);
    }

    @ExceptionHandler(EmailNotValidException.class)
    protected ResponseEntity<ResponseResult<Void>> handleBadRequest(EmailNotValidException ex){
        return buildResponseEntity(ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ResponseResult<Void>> handleBadRequest(EntityNotFoundException ex){
        return buildResponseEntity(ex);
    }

    @ExceptionHandler(ExpertNotAcceptedException.class)
    protected ResponseEntity<ResponseResult<Void>> handleBadRequest(ExpertNotAcceptedException ex){
        return buildResponseEntity(ex);
    }

    @ExceptionHandler(NameNotValidException.class)
    protected ResponseEntity<ResponseResult<Void>> handleBadRequest(NameNotValidException ex){
        return buildResponseEntity(ex);
    }

    @ExceptionHandler(NotEnoughMoneyException.class)
    protected ResponseEntity<ResponseResult<Void>> handleBadRequest(NotEnoughMoneyException ex){
        return buildResponseEntity(ex);
    }

    @ExceptionHandler(NullFieldException.class)
    protected ResponseEntity<ResponseResult<Void>> handleBadRequest(NullFieldException ex){
        return buildResponseEntity(ex);
    }

    @ExceptionHandler(PasswordNotValidException.class)
    protected ResponseEntity<ResponseResult<Void>> handleBadRequest(PasswordNotValidException ex){
        return buildResponseEntity(ex);
    }

    @ExceptionHandler(UserNotLoggedInException.class)
    protected ResponseEntity<ResponseResult<Void>> handleBadRequest(UserNotLoggedInException ex){
        return buildResponseEntity(ex);
    }

    @ExceptionHandler(UserNotValidException.class)
    protected ResponseEntity<ResponseResult<Void>> handleBadRequest(UserNotValidException ex){
        return buildResponseEntity(ex);
    }

    @ExceptionHandler(UUIDNotFoundException.class)
    protected ResponseEntity<ResponseResult<Void>> handleBadRequest(UUIDNotFoundException ex){
        return buildResponseEntity(ex);
    }

    @ExceptionHandler(WrongPasswordException.class)
    protected ResponseEntity<ResponseResult<Void>> handleBadRequest(WrongPasswordException ex){
        return buildResponseEntity(ex);
    }

    private ResponseEntity<ResponseResult<Void>> buildResponseEntity(Exception ex){
        ResponseResult<Void> response = new ResponseResult<>().fail(ex);
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }

}
