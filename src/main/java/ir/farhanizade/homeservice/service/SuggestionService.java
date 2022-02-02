package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.order.MyOrder;
import ir.farhanizade.homeservice.entity.order.OrderStatus;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.order.message.SuggestionRepository;
import ir.farhanizade.homeservice.security.user.LoggedInUser;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static ir.farhanizade.homeservice.entity.order.OrderStatus.WAITING_FOR_EXPERT;
import static ir.farhanizade.homeservice.entity.order.OrderStatus.WAITING_FOR_SELECTION;
import static ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus.BUSY;
import static ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus.DONE;
import static ir.farhanizade.homeservice.entity.order.message.SuggestionStatus.REJECTED;

@Service
@RequiredArgsConstructor
public class SuggestionService {
    private final SuggestionRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public ExpertAddSuggestionOutDto save(Suggestion suggestion) throws NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, BusyOrderException, DuplicateEntityException {
        Validation.isValid(suggestion);
        MyOrder order = suggestion.getMyOrder();
        if (suggestion.getId() == null) {
            order.suggest(suggestion);
        }
        Suggestion saved = repository.save(suggestion);
        return ExpertAddSuggestionOutDto.builder()
                .expertId(suggestion.getOwner().getId())
                .orderId(order.getId())
                .suggestionId(saved.getId())
                .build();
    }


    @Transactional(readOnly = true)
    public CustomPage<SuggestionOutDto> loadAll(Pageable pageable) {
        Page<Suggestion> all = repository.getAll(pageable);
        return convert2Dto(all);
    }

    @Transactional(readOnly = true)
    public CustomPage<SuggestionOutDto> findAllByOrderId(Long order, Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        Long id = LoggedInUser.id();
        Page<Suggestion> suggestions = repository.findAllByOrderId(id, order, pageable);
        return convert2Dto(suggestions);
    }

    @Transactional(readOnly = true)
    public Suggestion findById(Long id) throws EntityNotFoundException {
        Optional<Suggestion> byId = repository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        throw new EntityNotFoundException("Suggestion Not Found!");
    }

    @Transactional(readOnly = true)
    public CustomPage<SuggestionOutDto> findAllByOwnerId(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        Long id = LoggedInUser.id();
        Page<Suggestion> page = repository.findAllByOwnerId(id, pageable);
        if (page.getContent().size() == 0) throw new EntityNotFoundException("No Suggestions Found!");
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    public CustomPage<SuggestionOutDto> findAllByCustomerId(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        Long id = LoggedInUser.id();
        Page<Suggestion> page = repository.findAllByCustomerId(id, pageable);
        if (page.getContent().size() == 0) throw new EntityNotFoundException("No Suggestions Found!");
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    public CustomPage<ExpertSuggestionOutDto> findAllByOwnerIdAndStatus(SuggestionStatus[] status, Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        Long ownerId = LoggedInUser.id();
        Page<Suggestion> page = repository.findAllByOwnerIdAndStatus(ownerId, status, pageable);
        if (page.getContent().isEmpty()) throw new EntityNotFoundException("No Suggestion Found!");
        return convert2DtoList(page);
    }

    private CustomPage<ExpertSuggestionOutDto> convert2DtoList(Page<Suggestion> page) {
        List<ExpertSuggestionOutDto> data = page.getContent().stream().map(s ->
                ExpertSuggestionOutDto.builder()
                        .id(s.getId())
                        .orderId(s.getMyOrder().getId())
                        .service(s.getMyOrder().getService().getName())
                        .price(s.getPrice())
                        .suggestedDateTime(s.getSuggestedDateTime())
                        .status(s.getSuggestionStatus())
                        .build()).toList();
        return CustomPage.<ExpertSuggestionOutDto>builder()
                .data(data)
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .lastPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }

    public SuggestionOutDto convert2Dto(Suggestion suggestion) {
        return SuggestionOutDto.builder()
                .id(suggestion.getId())
                .ownerId(suggestion.getOwner().getId())
                .ownerName(suggestion.getOwner().getName())
                .ownerPoints(suggestion.getOwner().getPoints())
                .createdDateTime(suggestion.getCreatedTime())
                .details(suggestion.getDetails())
                .duration(suggestion.getDuration())
                .price(suggestion.getPrice())
                .suggestedDateTime(suggestion.getSuggestedDateTime())
                .build();
    }

    public CustomPage<SuggestionOutDto> convert2Dto(Page<Suggestion> suggestions) {
        List<Suggestion> content = suggestions.getContent();
        List<SuggestionOutDto> suggestionOutDtos = content.stream()
                .map(this::convert2Dto).toList();
        return CustomPage.<SuggestionOutDto>builder()
                .pageNumber(suggestions.getNumber())
                .lastPage(suggestions.getTotalPages())
                .pageSize(suggestions.getSize())
                .totalElements(suggestions.getTotalElements())
                .data(suggestionOutDtos)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public SuggestionAnswerOutDto answer(Long suggestionId, BaseMessageStatus status) throws EntityNotFoundException, BadEntryException, UserNotLoggedInException, AccountIsLockedException {
        Long ownerId = LoggedInUser.id();
        Suggestion suggestion = findById(suggestionId);
        if (suggestion.getOwner().getId() != ownerId) throw new BadEntryException("This Suggestion is not yours!");
        if (!suggestion.getStatus().equals(BaseMessageStatus.WAITING))
            throw new BadEntryException("This suggestion is done!");
        MyOrder order = suggestion.getMyOrder();
        Request request = order.getRequest();
        if (status.equals(BUSY)) {
            suggestion.setStatus(status);
            order.setStatus(WAITING_FOR_EXPERT);
            request.setStatus(status);
        } else {
            suggestion.setStatus(status);
            suggestion.setSuggestionStatus(REJECTED);
            order.setStatus(WAITING_FOR_SELECTION);
        }
        repository.save(suggestion);
        return SuggestionAnswerOutDto.builder()
                .suggestion(suggestionId)
                .orderId(order.getId())
                .answer(status)
                .build();
    }

    public SuggestionOutDto getById(Long id) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        Long customerId = LoggedInUser.id();
        Suggestion suggestion = findByIdAndCustomerId(id, customerId);
        return SuggestionOutDto.builder()
                .id(suggestion.getId())
                .ownerName(suggestion.getOwner().getName())
                .ownerId(suggestion.getOwner().getId())
                .ownerPoints(suggestion.getOwner().getPoints())
                .createdDateTime(suggestion.getCreatedTime())
                .details(suggestion.getDetails())
                .duration(suggestion.getDuration())
                .suggestedDateTime(suggestion.getSuggestedDateTime())
                .price(suggestion.getPrice())
                .build();
    }

    public Suggestion findByIdAndOwnerId(Long id) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        Long ownerId = LoggedInUser.id();
        findById(id);
        Optional<Suggestion> byIdAndOwnerId = repository.findByIdAndOwnerId(id, ownerId);
        return byIdAndOwnerId.orElseThrow(() -> new EntityNotFoundException("Suggestion Not Found!"));
    }

    public Suggestion findByStatusAndOrderId(SuggestionStatus accepted, Long id) {
        Optional<Suggestion> optional = repository.findByStatusAndOrderId(accepted, id);
        Suggestion suggestion = optional.orElseGet(null);
        return suggestion;
    }

    public Suggestion findAcceptedByOrderId(Long id) {
        Optional<Suggestion> suggestion = repository.findAcceptedByOrderId(id);
        return suggestion.orElseGet(Suggestion::new);
    }

    public Long countNumberOfSuggestions() {
        return repository.countNumberOfSuggestions();
    }

    public Suggestion findByIdAndCustomerId(Long id, Long customerId) throws EntityNotFoundException {
        return repository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion Not Found!"));
    }

    public EntityOutDto startToWork(Long suggestionId) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, AccountIsLockedException, BusyOrderException, DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException {
        Suggestion suggestion = findByIdAndOwnerId(suggestionId);
        if (suggestion.getSuggestionStatus().equals(SuggestionStatus.ACCEPTED)) {
            MyOrder order = suggestion.getMyOrder();
            order.setStatus(OrderStatus.STARTED);
            repository.save(suggestion);
            return new EntityOutDto(suggestionId);
        } else throw new BadEntryException("This order is not yours!");
    }

    public EntityOutDto finishWork(Long suggestionId) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, AccountIsLockedException {
        Suggestion suggestion = findByIdAndOwnerId(suggestionId);
        if (suggestion.getSuggestionStatus().equals(SuggestionStatus.ACCEPTED) &&
                suggestion.getStatus().equals(BUSY)) {
            MyOrder order = suggestion.getMyOrder();
            order.setStatus(OrderStatus.DONE);
            order.getRequest().setStatus(DONE);
            suggestion.setStatus(DONE);
            order.setFinishDateTime(new Date(System.currentTimeMillis()));
            repository.save(suggestion);
            return new EntityOutDto(suggestionId);
        } else throw new BadEntryException("This order is not yours!");
    }
}
