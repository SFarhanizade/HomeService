package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.ServiceInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.MainServiceOutDto;
import ir.farhanizade.homeservice.dto.out.ServiceOutDto;
import ir.farhanizade.homeservice.entity.core.BaseEntity;
import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.service.MainServiceService;
import ir.farhanizade.homeservice.service.SubServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final MainServiceService mainService;
    private final SubServiceService subService;

    @PostMapping
    public ResponseEntity<ResponseResult<List<MainServiceOutDto>>> show() {
        ResponseResult<List<MainServiceOutDto>> response = ResponseResult.<List<MainServiceOutDto>>builder()
                .code(1)
                .message("Done!")
                .build();
        HttpStatus status = HttpStatus.OK;
        List<MainService> mainServices = null;
        try {
            mainServices = mainService.loadAll();
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            status = HttpStatus.NOT_FOUND;
            response.setMessage(e.getMessage());
        }
        List<MainServiceOutDto> dtos = mainServices.stream()
                .map(m -> MainServiceOutDto.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .subServices(m.getSubServices().stream()
                                .map(s -> new ServiceOutDto(s.getId(), s.getName()))
                                .collect(Collectors.toList())
                        )
                        .build()).toList();
        response.setData(dtos);
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping
    public ResponseEntity<ResponseResult<EntityOutDto>> save(@RequestBody ServiceInDto service) {
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("add successfully!")
                .build();
        HttpStatus status = HttpStatus.CREATED;
        BaseEntity result = new BaseEntity();

            try {
                if (service.getParent() == 0) {
                    result = mainService.save(service.convert2MainService());
                    response.setMessage("MainService "+response.getMessage());
                } else {
                    result = subService.save(service.convert2SubService(), service.getParent());
                    response.setMessage("SubService "+response.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setMessage(e.getMessage());
                response.setCode(-1);
            }
        response.setData(new EntityOutDto(result.getId()));
            return ResponseEntity.status(status).body(response);
    }
}
