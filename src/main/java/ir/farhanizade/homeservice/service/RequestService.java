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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository repository;
    private final CustomerService customerService;
    private final SubServiceService subService;

    @Transactional
    public EntityOutDto save(RequestInDto request) throws NullFieldException, BadEntryException, NameNotValidException, EmailNotValidException, PasswordNotValidException, EntityNotFoundException {
        Request entity = convert2Request(request);
        isValid(entity);
        Order order = entity.getOrder();
        Customer owner = entity.getOwner();
        order.addRequest(entity);
        owner.addOrder(order);
        Request saved = repository.save(entity);
        return new EntityOutDto(saved.getId());
    }

    private Request convert2Request(RequestInDto request) throws EntityNotFoundException {
        Long ownerId = request.getOwnerId();
        Long serviceId = request.getServiceId();
        BigDecimal price = new BigDecimal(request.getPrice());
        Date suggestedDateTime = request.getSuggestedDateTime();
        String details = request.getDetails();
        String address = request.getAddress();
        Customer owner = customerService.loadById(ownerId);
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

    public List<Request> loadAll() {
        return repository.findAll();
    }

    public List<Request> loadWaitingRequests() {
        return repository.findByStatus(BaseMessageStatus.WAITING);
    }

    private boolean isValid(Request request) throws NullFieldException, BadEntryException, NameNotValidException, EmailNotValidException, PasswordNotValidException {
        return Validation.isValid(request);
    }
}
