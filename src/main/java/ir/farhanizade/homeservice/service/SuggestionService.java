package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.repository.order.message.SuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SuggestionService {
    private final SuggestionRepository repository;
    public void save(Suggestion suggestion) {
        repository.save(suggestion);
    }

    public List<Suggestion> loadAll(){
        return repository.findAll();
    }
}
