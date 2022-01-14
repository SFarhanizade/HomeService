package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.order.message.SuggestionRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static ir.farhanizade.homeservice.entity.order.OrderStatus.WAITING_FOR_EXPERT;
import static ir.farhanizade.homeservice.entity.order.OrderStatus.WAITING_FOR_SELECTION;
import static ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus.BUSY;
import static ir.farhanizade.homeservice.entity.order.message.SuggestionStatus.ACCEPTED;
import static ir.farhanizade.homeservice.entity.order.message.SuggestionStatus.REJECTED;

@Service
@RequiredArgsConstructor
public class SuggestionService {
    private final SuggestionRepository repository;

    @Transactional
    public ExpertAddSuggestionOutDto save(Suggestion suggestion) throws NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, BusyOrderException, DuplicateEntityException {
        Validation.isValid(suggestion);
        Order order = suggestion.getOrder();
        order.suggest(suggestion);
        Suggestion saved = repository.save(suggestion);
        return ExpertAddSuggestionOutDto.builder()
                .expertId(suggestion.getOwner().getId())
                .orderId(order.getId())
                .suggestionId(saved.getId())
                .build();
    }


    @Transactional(readOnly = true)
    public List<Suggestion> loadAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Suggestion> findAllByOrderId(Long order) throws EntityNotFoundException {
        List<Suggestion> suggestions = repository.findAllByOrderId(order);
        if (suggestions.size() == 0)
            throw new EntityNotFoundException("No Suggestions Found For This Order!");
        return suggestions;
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
    public List<SuggestionOutDto> findAllByOwnerId(Long id) throws EntityNotFoundException {
        List<Suggestion> suggestions = repository.findAllByOwnerId(id);
        if (suggestions.size() == 0) throw new EntityNotFoundException("No Suggestions Found!");
        return convert2Dto(suggestions);
    }

    @Transactional(readOnly = true)
    public List<SuggestionOutDto> findAllByCustomerId(Long id) throws EntityNotFoundException {
        List<Suggestion> suggestions = repository.findAllByCustomerId(id);
        if (suggestions.size() == 0) throw new EntityNotFoundException("No Suggestions Found!");
        return convert2Dto(suggestions);
    }

    @Transactional(readOnly = true)
    public List<ExpertSuggestionOutDto> findAllByOwnerIdAndStatus(Long ownerId, SuggestionStatus[] status) throws EntityNotFoundException {
        List<Suggestion> allByOwnerIdAndStatus = repository.findAllByOwnerIdAndStatus(ownerId, status);
        if (allByOwnerIdAndStatus.isEmpty()) throw new EntityNotFoundException("No Suggestion Found!");
        return convert2DtoList(allByOwnerIdAndStatus);
    }

    private List<ExpertSuggestionOutDto> convert2DtoList(List<Suggestion> allByOwnerIdAndStatus) {
        return allByOwnerIdAndStatus.stream().map(s ->
                ExpertSuggestionOutDto.builder()
                        .id(s.getId())
                        .service(s.getOrder().getService().getName())
                        .price(s.getPrice())
                        .suggestedDateTime(s.getSuggestedDateTime())
                        .status(s.getSuggestionStatus())
                        .build()).toList();
    }

    public SuggestionOutDto convert2Dto(Suggestion suggestion) {
        return SuggestionOutDto.builder()
                .id(suggestion.getId())
                .ownerId(suggestion.getOwner().getId())
                .ownerName(suggestion.getOwner().getFName() + " " + suggestion.getOwner().getLName())
                .ownerPoints(suggestion.getOwner().getPoints())
                .createdDateTime(suggestion.getDateTime())
                .details(suggestion.getDetails())
                .duration(suggestion.getDuration())
                .price(suggestion.getPrice())
                .suggestedDateTime(suggestion.getSuggestedDateTime())
                .build();
    }

    public List<SuggestionOutDto> convert2Dto(List<Suggestion> suggestions) {
        return suggestions.stream()
                .map(this::convert2Dto).toList();
    }

    @Transactional
    public SuggestionAnswerOutDto answer(Long ownerId, Long suggestionId, BaseMessageStatus status) throws EntityNotFoundException, BadEntryException {
        Suggestion suggestion = findById(suggestionId);
        if (suggestion.getOwner().getId() != ownerId) throw new BadEntryException("This Suggestion is not yours!");
        Order order = suggestion.getOrder();
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

    public SuggestionOutDto getById(Long id) throws EntityNotFoundException {
        Suggestion suggestion = findById(id);
        return SuggestionOutDto.builder()
                .id(suggestion.getId())
                .createdDateTime(suggestion.getCreatedTime())
                .details(suggestion.getDetails())
                .duration(suggestion.getDuration())
                .suggestedDateTime(suggestion.getSuggestedDateTime())
                .price(suggestion.getPrice())
                .build();
    }

    public Suggestion findByIdAndOwnerId(Long id, Long ownerId) throws EntityNotFoundException {
        findById(id);
        Optional<Suggestion> byIdAndOwnerId = repository.findByIdAndOwnerId(id, ownerId);
        return byIdAndOwnerId.orElseThrow(() -> new EntityNotFoundException("Suggestion Not Found!"));
    }
}
