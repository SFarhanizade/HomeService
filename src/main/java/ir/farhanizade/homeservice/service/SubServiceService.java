package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.exception.DuplicateEntityException;
import ir.farhanizade.homeservice.repository.service.MainServiceRepository;
import ir.farhanizade.homeservice.repository.service.SubServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubServiceService {
    private final SubServiceRepository repository;
    private final MainServiceRepository parentRepository;

    @Transactional
    public SubService save(SubService entity, Long parentId) throws DuplicateEntityException {
        MainService parent = parentRepository.getById(parentId);
        if (parent == null)
            throw new IllegalStateException();
        entity.setParent(parent);
        List<SubService> siblings = parent.getSubServices();
        boolean noneMatch = true;
        if (siblings.size() > 0) {
            noneMatch = siblings.stream()
                    .noneMatch(s -> s.getName().equals(entity.getName()));
        }
        if (noneMatch) {
            return repository.save(entity);
        } else {
            throw new DuplicateEntityException("");
        }
    }

    @Transactional(readOnly = true)
    public List<SubService> loadAll() {
        return repository.findAll();
    }
}

