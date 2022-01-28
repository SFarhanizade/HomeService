package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.out.CommentOutDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.order.Comment;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.exception.BadEntryException;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.exception.UserNotLoggedInException;
import ir.farhanizade.homeservice.repository.order.CommentRepository;
import ir.farhanizade.homeservice.security.user.LoggedInUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository repository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public EntityOutDto save(Comment comment) {
        Expert recipient = comment.getExpert();
        recipient.addPoints(comment.getPoints());
        Comment saved = repository.save(comment);
        return new EntityOutDto(saved.getId());
    }

    public CustomPage<CommentOutDto> findAllByUserId(Pageable pageable) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException {
        Long id = LoggedInUser.id();
        Page<Comment> page = repository.findAllByUserId(id, pageable);
        return convert2Dto(page);
    }

    public CommentOutDto findByIdAndCustomerId(Long id, Long customerId) {
        Comment comment = repository.findByIdAndCustomerId(id, customerId);
        CommentOutDto result = convert2Dto(comment);
        result.setDescription(comment.getDescription());
        return result;
    }

    public CommentOutDto findByIdAndExpertId(Long id, Long expertId) {
        Comment comment = repository.findByIdAndExpertId(id, expertId);
        CommentOutDto result = convert2Dto(comment);
        return result;
    }

    private CustomPage<CommentOutDto> convert2Dto(Page<Comment> page) {
        List<CommentOutDto> data = page.getContent().stream().map(this::convert2Dto).toList();
        CustomPage<CommentOutDto> result = CustomPage.<CommentOutDto>builder().data(data).build();
        return result.convert(page);
    }

    private CommentOutDto convert2Dto(Comment comment) {
        return CommentOutDto.builder()
                .id(comment.getId())
                .customerId(comment.getCustomer().getId())
                .customerName(comment.getCustomer().getName())
                .expertId(comment.getExpert().getId())
                .expertName(comment.getExpert().getName())
                .points(comment.getPoints())
                .orderId(comment.getOrder().getId())
                .dateTime(comment.getCreatedTime())
                .build();
    }
}
