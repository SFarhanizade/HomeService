package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.ServiceInDto;
import ir.farhanizade.homeservice.dto.in.TimeRangeInDto;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/management")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;
    private final AdminService adminService;
    private final OrderService orderService;
    private final MainServiceService mainService;
    private final SubServiceService subService;

    @PostMapping
    public ResponseEntity<ResponseResult<EntityOutDto>> create(@RequestBody UserInDto user) throws DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, NullFieldException {
        EntityOutDto data = adminService.save(user);
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("User added successfully.")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/services")
    public ResponseEntity<ResponseResult<EntityOutDto>> addService(@RequestBody ServiceInDto service) throws DuplicateEntityException, EntityNotFoundException {
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

    @GetMapping("/services")
    public ResponseEntity<ResponseResult<CustomPage<MainServiceOutDto>>> showServices(Pageable pageable) throws EntityNotFoundException {
        ResponseResult<CustomPage<MainServiceOutDto>> response = ResponseResult.<CustomPage<MainServiceOutDto>>builder()
                .code(1)
                .message("Done!")
                .build();
        HttpStatus status = HttpStatus.OK;
        CustomPage<MainServiceOutDto> result = mainService.loadAll(pageable);
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/services/{mainId}")
    public ResponseEntity<ResponseResult<MainServiceOutDto>> showService(@PathVariable Long mainId) throws EntityNotFoundException {
        ResponseResult<MainServiceOutDto> response = ResponseResult.<MainServiceOutDto>builder()
                .code(1)
                .message("Done!")
                .build();
        HttpStatus status = HttpStatus.OK;
        MainServiceOutDto result = mainService.findById(mainId);
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/services/sub-services")
    public ResponseEntity<ResponseResult<CustomPage<ServiceOutDto>>> showSubServices(Pageable pageable) throws EntityNotFoundException {
        ResponseResult<CustomPage<ServiceOutDto>> response = ResponseResult.<CustomPage<ServiceOutDto>>builder()
                .code(1)
                .message("Done!")
                .build();
        HttpStatus status = HttpStatus.OK;
        CustomPage<ServiceOutDto> result = subService.loadAll(pageable);
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/services/sub-services/{id}")
    public ResponseEntity<ResponseResult<ServiceOutDto>> showSubServices(@PathVariable Long id) throws EntityNotFoundException {
        ResponseResult<ServiceOutDto> response = ResponseResult.<ServiceOutDto>builder()
                .code(1)
                .message("Done!")
                .build();
        HttpStatus status = HttpStatus.OK;
        ServiceOutDto result = subService.findById(id);
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/experts/{expertId}/accept")
    public ResponseEntity<ResponseResult<EntityOutDto>> acceptExpert(@PathVariable Long expertId) throws UserNotValidException, EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .message("User accepted successfully!")
                .build();
        HttpStatus status = HttpStatus.CREATED;
        EntityOutDto result = adminService.acceptExpert(expertId);
        response.setData(result);
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/users/search")
    public ResponseEntity<ResponseResult<CustomPage<UserSearchOutDto>>> search(@RequestBody UserSearchInDto param, Pageable pageable) throws EntityNotFoundException, UserNotValidException {
        CustomPage<UserSearchOutDto> result = adminService.search(param, pageable);

        ResponseResult<CustomPage<UserSearchOutDto>> response = ResponseResult.<CustomPage<UserSearchOutDto>>builder()
                .data(result)
                .code(1)
                .message("Loaded successfully!")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResponseResult<UserSearchOutDto>> loadUserById(@PathVariable Long id) throws EntityNotFoundException, UserNotValidException {
        UserSearchOutDto result = userService.getUserById(id);

        ResponseResult<UserSearchOutDto> response = ResponseResult.<UserSearchOutDto>builder()
                .data(result)
                .code(1)
                .message("Loaded successfully!")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/report/registerTime")
    public ResponseEntity<ResponseResult<ReportRegisterTimeUsersOutDto>> getNumberOfUsersByRegisterTime(@RequestBody TimeRangeInDto timeRange) {
        ResponseResult<ReportRegisterTimeUsersOutDto> response = ResponseResult.<ReportRegisterTimeUsersOutDto>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        ReportRegisterTimeUsersOutDto data = userService.getNumberOfUsersByRegisterTime(timeRange);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<ResponseResult<CustomPage<OrderOfUserOutDto>>> loadAllOrders(Pageable pageable) throws EntityNotFoundException, UserNotValidException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        CustomPage<OrderOfUserOutDto> result = orderService.getOrders(pageable);

        ResponseResult<CustomPage<OrderOfUserOutDto>> response = ResponseResult.<CustomPage<OrderOfUserOutDto>>builder()
                .data(result)
                .code(1)
                .message("Loaded successfully!")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ResponseResult<OrderOfUserOutDto>> loadAllOrders(@PathVariable Long id) throws EntityNotFoundException, UserNotValidException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        OrderOfUserOutDto result = orderService.getById(id);

        ResponseResult<OrderOfUserOutDto> response = ResponseResult.<OrderOfUserOutDto>builder()
                .data(result)
                .code(1)
                .message("Loaded successfully!")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/main-service/{id}")
    public ResponseEntity<ResponseResult<CustomPage<OrderOfUserOutDto>>> loadAllOrdersOfMainService(@PathVariable Long id, Pageable pageable) throws EntityNotFoundException, UserNotValidException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        CustomPage<OrderOfUserOutDto> result = orderService.getOrdersByMainService(id, pageable);

        ResponseResult<CustomPage<OrderOfUserOutDto>> response = ResponseResult.<CustomPage<OrderOfUserOutDto>>builder()
                .data(result)
                .code(1)
                .message("Loaded successfully!")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/sub-service/{id}")
    public ResponseEntity<ResponseResult<CustomPage<OrderOfUserOutDto>>> loadAllOrdersOfSubService(@PathVariable Long id, Pageable pageable) throws EntityNotFoundException, UserNotValidException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        CustomPage<OrderOfUserOutDto> result = orderService.getOrdersBySubService(id, pageable);

        ResponseResult<CustomPage<OrderOfUserOutDto>> response = ResponseResult.<CustomPage<OrderOfUserOutDto>>builder()
                .data(result)
                .code(1)
                .message("Loaded successfully!")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/time")
    public ResponseEntity<ResponseResult<CustomPage<OrderOfUserOutDto>>> loadAllOrdersInTimeRange(@RequestBody TimeRangeInDto timeRange, Pageable pageable) throws EntityNotFoundException {
        ResponseResult<CustomPage<OrderOfUserOutDto>> response = ResponseResult.<CustomPage<OrderOfUserOutDto>>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        CustomPage<OrderOfUserOutDto> data = orderService.getOrdersByRangeOfTime(timeRange.getStartTime(), timeRange.getEndTime(), pageable);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/status/{status}")
    public ResponseEntity<ResponseResult<CustomPage<OrderOfUserOutDto>>> loadAllOrdersByStatus(@PathVariable Integer status, Pageable pageable) throws EntityNotFoundException {
        ResponseResult<CustomPage<OrderOfUserOutDto>> response = ResponseResult.<CustomPage<OrderOfUserOutDto>>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        CustomPage<OrderOfUserOutDto> data = orderService.getOrdersByStatus(status, pageable);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/report/count")
    public ResponseEntity<ResponseResult<RequestAndSuggestionReportOutDto>> getNumberOfRequestsAndSuggestions() {
        ResponseResult<RequestAndSuggestionReportOutDto> response = ResponseResult.<RequestAndSuggestionReportOutDto>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        RequestAndSuggestionReportOutDto data = orderService.getNumberOfRequestsAndSuggestions();
        response.setData(data);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/orders/report/count/done")
    public ResponseEntity<ResponseResult<Long>> getNumberOfDoneOrders() {
        ResponseResult<Long> response = ResponseResult.<Long>builder()
                .code(1)
                .message("Loaded successfully!")
                .build();
        Long data = orderService.getNumberOfDoneOrders();
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/suggestions/all")
    public ResponseEntity<ResponseResult<CustomPage<SuggestionOutDto>>> loadAllSuggestions(Pageable pageable) throws EntityNotFoundException, UserNotValidException {
        CustomPage<SuggestionOutDto> result = adminService.loadAllSuggestions(pageable);

        ResponseResult<CustomPage<SuggestionOutDto>> response = ResponseResult.<CustomPage<SuggestionOutDto>>builder()
                .data(result)
                .code(1)
                .message("Loaded successfully!")
                .build();

        return ResponseEntity.ok(response);
    }

}
