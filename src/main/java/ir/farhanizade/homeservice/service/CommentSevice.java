package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.order.Comment;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.repository.order.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentSevice {

    private final CommentRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public void save(Comment comment) {
        Order order = comment.getOrder();
        Expert recipient = comment.getRecipient();
        recipient.addPoints(comment.getPoints());
        order.setComment(comment);
        repository.save(comment);
    }
}
