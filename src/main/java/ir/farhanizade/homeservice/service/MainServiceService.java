package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.exception.DuplicateEntityException;
import ir.farhanizade.homeservice.repository.service.MainServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainServiceService {
    private final MainServiceRepository repository;

    @Transactional
    public void save(MainService entity) throws DuplicateEntityException {
        MainService result = repository.findByName(entity.getName());
        if (result != null && entity.getId()==null) throw new DuplicateEntityException("");
        repository.save(entity);
    }

    public List<MainService> findAll(){
        return repository.findAll();
    }
}
