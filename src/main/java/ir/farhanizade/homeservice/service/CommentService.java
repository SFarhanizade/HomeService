package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.out.CommentOutDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.order.MyComment;
import ir.farhanizade.homeservice.entity.user.UserExpert;
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
    public EntityOutDto save(MyComment comment) {
        UserExpert recipient = comment.getMyExpert();
        recipient.addPoints(comment.getPoints());
        MyComment saved = repository.save(comment);
        return new EntityOutDto(saved.getId());
    }

    public CustomPage<CommentOutDto> findAllByUserId(Pageable pageable) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, AccountIsLockedException {
        Long id = LoggedInUser.id();
        Page<MyComment> page = repository.findAllByUserId(id, pageable);
        return convert2Dto(page);
    }

    public CommentOutDto getCommentById(Long id) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, AccountIsLockedException {
        UserTypeAndId typeAndId = LoggedInUser.getTypeAndId();
        Long userId = typeAndId.getId();
        ApplicationUserRole role = typeAndId.getRole();
        MyComment comment;
        switch (role) {
            case CUSTOMER -> comment = repository.findByIdAndCustomerId(id, userId);
            case EXPERT -> comment = repository.findByIdAndExpertId(id, userId);
            default -> throw new BadEntryException("User not allowed!");
        }
        CommentOutDto result = convert2Dto(comment);
        return result;
    }

    private CustomPage<CommentOutDto> convert2Dto(Page<MyComment> page) {
        List<CommentOutDto> data = page.getContent().stream().map(this::convert2Dto).toList();
        CustomPage<CommentOutDto> result = CustomPage.<CommentOutDto>builder().data(data).build();
        return result.convert(page);
    }

    private CommentOutDto convert2Dto(MyComment comment) {
        return CommentOutDto.builder()
                .id(comment.getId())
                .customerId(comment.getMyCustomer().getId())
                .customerName(comment.getMyCustomer().getName())
                .expertId(comment.getMyExpert().getId())
                .expertName(comment.getMyExpert().getName())
                .points(comment.getPoints())
                .orderId(comment.getMyOrder().getId())
                .dateTime(comment.getCreatedTime())
                .build();
    }
}
