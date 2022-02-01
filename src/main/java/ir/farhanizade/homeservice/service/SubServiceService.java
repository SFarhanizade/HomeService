package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.ServiceInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.ServiceOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.exception.DuplicateEntityException;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.repository.service.MainServiceRepository;
import ir.farhanizade.homeservice.repository.service.SubServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubServiceService {
    private final SubServiceRepository repository;
    private final MainServiceRepository parentRepository;

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto save(ServiceInDto service, Long parentId) throws DuplicateEntityException, EntityNotFoundException {
        SubService entity = service.convert2SubService();
        MainService parent = parentRepository.getById(parentId);
        if (parent == null)
            throw new EntityNotFoundException("Parent Is Not Found!");
        entity.setParent(parent);
        List<SubService> siblings = parent.getSubServices();
        boolean noneMatch = true;
        if (siblings.size() > 0) {
            noneMatch = siblings.stream()
                    .noneMatch(s -> s.getName().equals(entity.getName()));
        }
        if (noneMatch) {
            SubService saved = repository.save(entity);
            return new EntityOutDto(saved.getId());
        } else {
            throw new DuplicateEntityException("SubService exists!");
        }
    }

    @Transactional(readOnly = true)
    public CustomPage<ServiceOutDto> loadAll(Pageable pageable) {
        Page<SubService> page = repository.findAll(pageable);
        List<ServiceOutDto> data = page.getContent().stream()
                .map(s -> new ServiceOutDto(s.getId(), s.getName(), s.getBasePrice())).toList();

        CustomPage<ServiceOutDto> result = new CustomPage<>();
        result.setData(data);
        return result.convert(page);
    }

    @Transactional(readOnly = true)
    public SubService loadById(Long serviceId) throws EntityNotFoundException {
        Optional<SubService> byId = repository.findById(serviceId);
        return byId.orElseThrow(() -> new EntityNotFoundException("Service doesn't exist!"));
    }

    @Transactional(readOnly = true)
    public ServiceOutDto findById(Long serviceId) throws EntityNotFoundException {
        Optional<SubService> byId = repository.findById(serviceId);
        SubService subService = byId.orElseThrow(() -> new EntityNotFoundException("Service doesn't exist!"));
        return new ServiceOutDto(serviceId, subService.getName(), subService.getBasePrice());
    }
}

