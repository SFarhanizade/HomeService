package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.ExpertInDto;
import ir.farhanizade.homeservice.dto.in.TimeRangeInDto;
import ir.farhanizade.homeservice.dto.out.OrderOfUserOutDto;
import ir.farhanizade.homeservice.dto.out.OrderOutDto;
import ir.farhanizade.homeservice.dto.out.RequestAndSuggestionReportOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final ExpertService expertService;
    private final OrderService orderService;

    @GetMapping
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
    public ResponseEntity<ResponseResult<CustomPage<OrderOfUserOutDto>>> getOrders(@RequestBody TimeRangeInDto timeRange, Pageable pageable) throws EntityNotFoundException {
        ResponseResult<CustomPage<OrderOfUserOutDto>> response = ResponseResult.<CustomPage<OrderOfUserOutDto>>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        CustomPage<OrderOfUserOutDto> data = orderService.getOrdersByRangeOfTime(timeRange.getStartTime(), timeRange.getEndTime(), pageable);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ResponseResult<CustomPage<OrderOfUserOutDto>>> getOrders(@PathVariable Integer status, Pageable pageable) throws EntityNotFoundException {
        ResponseResult<CustomPage<OrderOfUserOutDto>> response = ResponseResult.<CustomPage<OrderOfUserOutDto>>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        CustomPage<OrderOfUserOutDto> data = orderService.getOrdersByStatus(status, pageable);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mainService/{id}")
    public ResponseEntity<ResponseResult<CustomPage<OrderOfUserOutDto>>> getOrders(@PathVariable Long id, Pageable pageable) throws EntityNotFoundException {
        ResponseResult<CustomPage<OrderOfUserOutDto>> response = ResponseResult.<CustomPage<OrderOfUserOutDto>>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        CustomPage<OrderOfUserOutDto> data = orderService.getOrdersByMainService(id, pageable);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subService/{id}")
    public ResponseEntity<ResponseResult<CustomPage<OrderOfUserOutDto>>> getOrdersBySubService(@PathVariable Long id, Pageable pageable) throws EntityNotFoundException {
        ResponseResult<CustomPage<OrderOfUserOutDto>> response = ResponseResult.<CustomPage<OrderOfUserOutDto>>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        CustomPage<OrderOfUserOutDto> data = orderService.getOrdersBySubService(id, pageable);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report/messages")
    public ResponseEntity<ResponseResult<RequestAndSuggestionReportOutDto>> getNumberOfRequestsAndSuggestions() {
        ResponseResult<RequestAndSuggestionReportOutDto> response = ResponseResult.<RequestAndSuggestionReportOutDto>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        RequestAndSuggestionReportOutDto data = orderService.getNumberOfRequestsAndSuggestions();
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report/done")
    public ResponseEntity<ResponseResult<Long>> getNumberOfDoneOrders() {
        ResponseResult<Long> response = ResponseResult.<Long>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        Long data = orderService.getNumberOfDoneOrders();
        response.setData(data);
        return ResponseEntity.ok(response);
    }

}
