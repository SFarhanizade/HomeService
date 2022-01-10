package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.out.ExpertAddSuggestionOutDto;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.order.message.SuggestionRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SuggestionService {
    private final SuggestionRepository repository;

    @Transactional
    public ExpertAddSuggestionOutDto save(Suggestion suggestion) throws NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, BusyOrderException {
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
    public void acceptSuggestion(Suggestion suggestion) throws BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException {
        Validation.isValid(suggestion);
        Order order = suggestion.getOrder();
        if (order.getSuggestion() != null)
            throw new BusyOrderException("The order has already had an accepted suggestion!");
        order.acceptSuggestion(suggestion);
        repository.save(suggestion);
    }

    public List<Suggestion> loadAll() {
        return repository.findAll();
    }
}
