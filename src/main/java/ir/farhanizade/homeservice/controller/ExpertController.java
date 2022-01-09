package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.ExpertAddServiceInDto;
import ir.farhanizade.homeservice.dto.out.ExpertAddServiceOutDto;
import ir.farhanizade.homeservice.exception.DuplicateEntityException;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.service.ExpertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/experts")
@RequiredArgsConstructor
public class ExpertController {
    private final ExpertService expertService;



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
}
