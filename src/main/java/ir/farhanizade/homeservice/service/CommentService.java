package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.out.CommentOutDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.order.Comment;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.exception.AccountIsLockedException;
import ir.farhanizade.homeservice.exception.BadEntryException;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.exception.UserNotLoggedInException;
import ir.farhanizade.homeservice.repository.order.CommentRepository;
import ir.farhanizade.homeservice.security.ApplicationUserRole;
import ir.farhanizade.homeservice.security.user.LoggedInUser;
import ir.farhanizade.homeservice.security.user.UserTypeAndId;
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

    public CustomPage<CommentOutDto> findAllByUserId(Pageable pageable) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, AccountIsLockedException {
        Long id = LoggedInUser.id();
        Page<Comment> page = repository.findAllByUserId(id, pageable);
        return convert2Dto(page);
    }

    public CommentOutDto getCommentById(Long id) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, AccountIsLockedException {
        UserTypeAndId typeAndId = LoggedInUser.getTypeAndId();
        Long userId = typeAndId.getId();
        ApplicationUserRole role = typeAndId.getRole();
        Comment comment;
        switch (role) {
            case CUSTOMER -> comment = repository.findByIdAndCustomerId(id, userId);
            case EXPERT -> comment = repository.findByIdAndExpertId(id, userId);
            default -> throw new BadEntryException("User not allowed!");
        }
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
