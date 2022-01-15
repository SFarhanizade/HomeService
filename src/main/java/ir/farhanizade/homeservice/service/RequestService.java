package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.RequestInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.order.message.RequestRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus.BUSY;
import static ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus.CANCELLED;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository repository;
    private final SubServiceService subService;

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto save(Customer owner, RequestInDto request) throws NullFieldException, BadEntryException, NameNotValidException, EmailNotValidException, PasswordNotValidException, EntityNotFoundException {
        Request entity = convert2Request(owner, request);
        isValid(entity);
        Request saved = repository.save(entity);
        return new EntityOutDto(saved.getId());
    }

    private Request convert2Request(Customer owner, RequestInDto request) throws EntityNotFoundException {
        Long serviceId = request.getServiceId();
        BigDecimal price = new BigDecimal(request.getPrice());
        Date suggestedDateTime = request.getSuggestedDateTime();
        String details = request.getDetails();
        String address = request.getAddress();
        SubService service = subService.loadById(serviceId);
        Request result = Request.builder()
                .owner(owner)
                .order(
                        Order.builder()
                                .service(service)
                                .build())
                .price(price)
                .suggestedDateTime(suggestedDateTime)
                .details(details)
                .address(address)
                .build();
        return result;
    }

    @Transactional(readOnly = true)
    public List<Request> loadAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Request> loadWaitingRequests() {
        return repository.findByStatus(BaseMessageStatus.WAITING);
    }

    private boolean isValid(Request request) throws NullFieldException, BadEntryException, NameNotValidException, EmailNotValidException, PasswordNotValidException {
        return Validation.isValid(request);
    }
}
