package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.order.Comment;
import ir.farhanizade.homeservice.repository.order.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentSevice {
    private final CommentRepository repository;
    public void save(Comment comment){
        repository.save(comment);
    }
}
