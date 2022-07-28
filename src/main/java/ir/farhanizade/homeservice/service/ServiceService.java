package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.ServiceInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.MainServiceOutDto;
import ir.farhanizade.homeservice.dto.out.ServiceOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.service.MyService;
import ir.farhanizade.homeservice.exception.DuplicateEntityException;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.repository.service.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceService {
    private final ServiceRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto save(ServiceInDto service) throws DuplicateEntityException {
        MyService entity = service.convert2Service();
        MyService result = repository.findByName(entity.getName());
        if (result != null && entity.getId() == null) throw new DuplicateEntityException("MainService exists!");
        Long parent = service.getParent();
        if (parent != null) {
            Optional<MyService> optional = repository.findById(parent);
            MyService serviceParent = optional
                    .orElseThrow(() -> new EntityNotFoundException("Parent Service Not Found!"));
            entity.setParent(serviceParent);
        }

        MyService saved = repository.save(entity);
        return new EntityOutDto(saved.getId());
    }

    @Transactional(readOnly = true)
    public CustomPage<MainServiceOutDto> loadAllMain(Pageable pageable) throws EntityNotFoundException {
        Page<MyService> mainServices = repository.findAllMain(pageable);
        if (mainServices.getContent().size() == 0)
            throw new EntityNotFoundException("No Main Service Found!");
        List<MainServiceOutDto> data = mainServices.getContent().stream()
                .map(m -> MainServiceOutDto.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .subServices(m.getSubMyServices().stream().map(
                                s -> new ServiceOutDto(s.getId(), s.getName(), s.getBasePrice())).toList())
                        .build()).toList();
        CustomPage<MainServiceOutDto> result = new CustomPage<>();
        result.setData(data);
        return result.convert(mainServices);
    }

    public MyService getByID(Long id) throws EntityNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service Not Found!"));
    }

    public MainServiceOutDto getDTOByID(Long id) throws EntityNotFoundException {
        MyService mainService = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service Not Found!"));
        return new MainServiceOutDto(mainService);
    }

    public CustomPage<ServiceOutDto> loadAllSub(Pageable pageable) {
        Page<MyService> mainServices = repository.findAllSub(pageable);
        if (mainServices.getContent().size() == 0)
            throw new EntityNotFoundException("No SubService Found!");
        List<ServiceOutDto> data = mainServices.getContent().stream()
                .map(m -> ServiceOutDto.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .build()).toList();
        CustomPage<ServiceOutDto> result = new CustomPage<>();
        result.setData(data);
        return result.convert(mainServices);
    }
}
