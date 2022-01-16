package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.entity.order.Comment;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.repository.order.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentSevice {

    private final CommentRepository repository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public EntityOutDto save(Comment comment) {
        Expert recipient = comment.getExpert();
        recipient.addPoints(comment.getPoints());
        Comment saved = repository.save(comment);
        return new EntityOutDto(saved.getId());
    }
}
