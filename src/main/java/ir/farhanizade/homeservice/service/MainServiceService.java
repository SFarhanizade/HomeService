package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.ServiceInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.exception.DuplicateEntityException;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.exception.NullFieldException;
import ir.farhanizade.homeservice.repository.service.MainServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainServiceService {
    private final MainServiceRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto save(ServiceInDto service) throws DuplicateEntityException {
        MainService entity = service.convert2MainService();
        MainService result = repository.findByName(entity.getName());
        if (result != null && entity.getId() == null) throw new DuplicateEntityException("MainService exists!");
        MainService saved = repository.save(entity);
        return new EntityOutDto(saved.getId());
    }

    @Transactional(readOnly = true)
    public List<MainService> loadAll() throws EntityNotFoundException {
        List<MainService> result = repository.findAll();
        if (result.size() == 0)
            throw new EntityNotFoundException("No Main Service Found!");
        return result;
    }
}
