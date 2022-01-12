package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.ExpertAddSuggestionOutDto;
import ir.farhanizade.homeservice.dto.out.ExpertSuggestionOutDto;
import ir.farhanizade.homeservice.dto.out.SuggestionOutDto;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.order.message.SuggestionRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus.CANCELLED;

@Service
@RequiredArgsConstructor
public class SuggestionService {
    private final SuggestionRepository repository;
    private final OrderService orderService;

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

    @Transactional
    public EntityOutDto acceptSuggestion(Long id) throws BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, EntityNotFoundException {
        Optional<Suggestion> byId = repository.findById(id);
        if(!byId.isPresent()) throw new EntityNotFoundException("Suggestion Not Found!");
        Suggestion suggestion = byId.get();
        Validation.isValid(suggestion);
        Order order = suggestion.getOrder();
        repository.acceptSuggestion(suggestion.getId(), SuggestionStatus.ACCEPTED);
        repository.rejectOtherSuggestions(suggestion.getId(),order.getId(), SuggestionStatus.REJECTED);
        orderService.acceptSuggestion(order.getId());
        return new EntityOutDto(id);
    }

    @Transactional(readOnly = true)
    public List<Suggestion> loadAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<SuggestionOutDto> findAllByOrderId(Long order) throws EntityNotFoundException {
        List<Suggestion> suggestions = repository.findAllByOrderId(order);
        if (suggestions.size() == 0)
            throw new EntityNotFoundException("No Suggestions Found For This Order!");
        return convert2Dto(suggestions);
    }

    @Transactional(readOnly = true)
    public SuggestionOutDto findById(Long id) throws EntityNotFoundException {
        Optional<Suggestion> byId = repository.findById(id);
        if (byId.isPresent()) {
            Suggestion suggestion = byId.get();
            return convert2Dto(suggestion);
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
    public List<ExpertSuggestionOutDto> findAllByOwnerIdAndStatus(Long ownerId,SuggestionStatus[] status) throws EntityNotFoundException {
        List<Suggestion> allByOwnerIdAndStatus = repository.findAllByOwnerIdAndStatus(ownerId, status);
        if(allByOwnerIdAndStatus.isEmpty()) throw new EntityNotFoundException("No Suggestion Found!");
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

    private SuggestionOutDto convert2Dto(Suggestion suggestion) {
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

    private List<SuggestionOutDto> convert2Dto(List<Suggestion> suggestions) {
        return suggestions.stream()
                .map(this::convert2Dto).toList();
    }


    @Transactional
    public void cancel(Long orderId) {
        repository.cancel(orderId, CANCELLED);
    }
}
