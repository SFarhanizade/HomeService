package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.order.ServiceOrder;
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
    public void save(Suggestion suggestion) throws NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, BusyOrderException {
        Validation.isValid(suggestion);
        ServiceOrder order = suggestion.getOrder();
        order.suggest(suggestion);
        repository.save(suggestion);
    }

    @Transactional
    public void acceptSuggestion(Suggestion suggestion) throws BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException {
        Validation.isValid(suggestion);
        ServiceOrder order = suggestion.getOrder();
        if (order.getSuggestion() != null)
            throw new BusyOrderException("The order has already had an accepted suggestion!");
        order.acceptSuggestion(suggestion);
        repository.save(suggestion);
    }

    public List<Suggestion> loadAll() {
        return repository.findAll();
    }
}
