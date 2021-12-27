package ir.farhanizade.homeservice.repository.order.message;

import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.repository.BaseRepository;

import java.util.List;

public interface RequestRepository extends BaseRepository<Request> {
    List<Request> findByStatus(BaseMessageStatus waiting);
}
