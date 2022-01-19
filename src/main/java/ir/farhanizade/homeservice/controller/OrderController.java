package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.ExpertInDto;
import ir.farhanizade.homeservice.dto.out.OrderOfUserOutDto;
import ir.farhanizade.homeservice.dto.out.OrderOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final ExpertService expertService;
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ResponseResult<CustomPage<OrderOutDto>>> showList(@RequestBody ExpertInDto request, Pageable pageable) throws EntityNotFoundException {
        CustomPage<OrderOutDto> result = expertService.loadAvailableOrders(request, pageable);
        ResponseResult<CustomPage<OrderOutDto>> response = ResponseResult.<CustomPage<OrderOutDto>>builder()
                .code(1)
                .message("List of orders loaded successfully.")
                .data(result)
                .build();
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/time")
    public ResponseEntity<ResponseResult<CustomPage<OrderOfUserOutDto>>> getOrders(@PathVariable Long id, @Param(value = "time1") Date time1, @Param(value = "time2") Date time2, Pageable pageable) throws EntityNotFoundException {
        ResponseResult<CustomPage<OrderOfUserOutDto>> response = ResponseResult.<CustomPage<OrderOfUserOutDto>>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        CustomPage<OrderOfUserOutDto> data = orderService.getOrdersByRangeOfTime(time1, time2, pageable);
        response.setData(data);
        return ResponseEntity.ok(response);
    }


}
