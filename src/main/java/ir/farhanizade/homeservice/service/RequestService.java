package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.order.ServiceOrder;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository repository;

    @Transactional
    public void save(Request request) throws NullFieldException, BadEntryException, NameNotValidException, EmailNotValidException, PasswordNotValidException {
        isValid(request);
        ServiceOrder order = request.getOrder();
        Customer owner = request.getOwner();
        order.setRequest(request);
        owner.getOrders().add(order);
        repository.save(request);
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
