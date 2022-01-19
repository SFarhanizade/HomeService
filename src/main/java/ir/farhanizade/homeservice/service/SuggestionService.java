package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.order.message.SuggestionRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static ir.farhanizade.homeservice.entity.order.OrderStatus.WAITING_FOR_EXPERT;
import static ir.farhanizade.homeservice.entity.order.OrderStatus.WAITING_FOR_SELECTION;
import static ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus.BUSY;
import static ir.farhanizade.homeservice.entity.order.message.SuggestionStatus.REJECTED;

@Service
@RequiredArgsConstructor
public class SuggestionService {
    private final SuggestionRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public ExpertAddSuggestionOutDto save(Suggestion suggestion) throws NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, BusyOrderException, DuplicateEntityException {
        Validation.isValid(suggestion);
        Order order = suggestion.getOrder();
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
    public List<Suggestion> loadAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public CustomPage<SuggestionOutDto> findAllByOrderId(Long order,Pageable pageable) throws EntityNotFoundException {
        Page<Suggestion> suggestions = repository.findAllByOrderId(order, pageable);
//        if (suggestions.size() == 0)
//            throw new EntityNotFoundException("No Suggestions Found For This Order!");
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
    public CustomPage<SuggestionOutDto> findAllByOwnerId(Long id, Pageable pageable) throws EntityNotFoundException {
        Page<Suggestion> page = repository.findAllByOwnerId(id, pageable);
        if (page.getContent().size() == 0) throw new EntityNotFoundException("No Suggestions Found!");
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    public CustomPage<SuggestionOutDto> findAllByCustomerId(Long id, Pageable pageable) throws EntityNotFoundException {
        Page<Suggestion> page = repository.findAllByCustomerId(id, pageable);
        if (page.getContent().size() == 0) throw new EntityNotFoundException("No Suggestions Found!");
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    public CustomPage<ExpertSuggestionOutDto> findAllByOwnerIdAndStatus(Long ownerId, SuggestionStatus[] status, Pageable pageable) throws EntityNotFoundException {
        Page<Suggestion> page = repository.findAllByOwnerIdAndStatus(ownerId, status, pageable);
        if (page.getContent().isEmpty()) throw new EntityNotFoundException("No Suggestion Found!");
        return convert2DtoList(page);
    }

    private CustomPage<ExpertSuggestionOutDto> convert2DtoList(Page<Suggestion> page) {
        List<ExpertSuggestionOutDto> data = page.getContent().stream().map(s ->
                ExpertSuggestionOutDto.builder()
                        .id(s.getId())
                        .service(s.getOrder().getService().getName())
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
                .ownerName(suggestion.getOwner().getFName() + " " + suggestion.getOwner().getLName())
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
    public SuggestionAnswerOutDto answer(Long ownerId, Long suggestionId, BaseMessageStatus status) throws EntityNotFoundException, BadEntryException {
        Suggestion suggestion = findById(suggestionId);
        if (suggestion.getOwner().getId() != ownerId) throw new BadEntryException("This Suggestion is not yours!");
        if(!suggestion.getStatus().equals(BaseMessageStatus.WAITING)) throw new BadEntryException("This suggestion is done!");
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
                .ownerName(suggestion.getOwner().getFName() + " " + suggestion.getOwner().getLName())
                .ownerId(suggestion.getOwner().getId())
                .ownerPoints(suggestion.getOwner().getPoints())
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

    public Suggestion findByStatusAndOrderId(SuggestionStatus accepted, Long id) {
        Optional<Suggestion> optional =repository.findByStatusAndOrderId(accepted,id);
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
}
