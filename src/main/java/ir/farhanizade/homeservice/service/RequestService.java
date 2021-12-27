package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.repository.order.message.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository repository;

    public void save(Request request){
        repository.save(request);
    }

    public List<Request> loadAll(){
        return repository.findAll();
    }

    public List<Request> loadWaitingRequests(){
        return repository.findByStatus(BaseMessageStatus.WAITING);
    }
}
