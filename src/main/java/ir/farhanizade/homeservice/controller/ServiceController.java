package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.ServiceInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.MainServiceOutDto;
import ir.farhanizade.homeservice.dto.out.ServiceOutDto;
import ir.farhanizade.homeservice.entity.core.BaseEntity;
import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.exception.DuplicateEntityException;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.service.MainServiceService;
import ir.farhanizade.homeservice.service.SubServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ServiceController {

    private final MainServiceService mainService;
    private final SubServiceService subService;

    @PostMapping
    public ResponseEntity<ResponseResult<EntityOutDto>> save(@RequestBody ServiceInDto service) throws DuplicateEntityException, EntityNotFoundException {
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("add successfully!")
                .build();
        HttpStatus status = HttpStatus.CREATED;
        EntityOutDto result;
            if (service.getParent() == 0) {
                result = mainService.save(service);
                response.setMessage("MainService " + response.getMessage());
            } else {
                result = subService.save(service, service.getParent());
                response.setMessage("SubService " + response.getMessage());
            }
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }
}
