package ir.farhanizade.homeservice.repository.order;

import ir.farhanizade.homeservice.entity.order.MyComment;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends BaseRepository<MyComment> {

    @Query("From MyComment c where c.myCustomer.id=:id or c.myExpert.id=:id")
    Page<MyComment> findAllByUserId(Long id, Pageable pageable);

    @Query("From MyComment c where c.id=:id and c.myCustomer.id=:customerId")
    MyComment findByIdAndCustomerId(Long id, Long customerId);

    @Query("From MyComment c where c.id=:id and c.myExpert.id=:expertId")
    MyComment findByIdAndExpertId(Long id, Long expertId);
}
