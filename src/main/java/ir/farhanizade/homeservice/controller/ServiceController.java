package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.ServiceInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.exception.DuplicateEntityException;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ServiceController {

    private final ServiceService mainService;

    @PostMapping
    public ResponseEntity<ResponseResult<EntityOutDto>> save(@RequestBody ServiceInDto service) throws DuplicateEntityException, EntityNotFoundException {
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("added successfully!")
                .build();
        HttpStatus status = HttpStatus.CREATED;
        EntityOutDto result;
        result = mainService.save(service);
        response.setMessage("Service " + response.getMessage());
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }
}
