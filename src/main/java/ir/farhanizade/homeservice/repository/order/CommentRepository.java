package ir.farhanizade.homeservice.repository.order;

import ir.farhanizade.homeservice.dto.out.CommentOutDto;
import ir.farhanizade.homeservice.entity.order.Comment;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends BaseRepository<Comment> {

    @Query("From Comment c where c.customer.id=:id or c.expert.id=:id")
    Page<Comment> findAllByUserId(Long id, Pageable pageable);

    @Query("From Comment c where c.id=:id and c.customer.id=:customerId")
    Comment findByIdAndCustomerId(Long id, Long customerId);

    @Query("From Comment c where c.id=:id and c.expert.id=:expertId")
    Comment findByIdAndExpertId(Long id, Long expertId);
}
