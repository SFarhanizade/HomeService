package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.ServiceInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.MainServiceOutDto;
import ir.farhanizade.homeservice.dto.out.ServiceOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.exception.DuplicateEntityException;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.exception.NullFieldException;
import ir.farhanizade.homeservice.repository.service.MainServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public CustomPage<MainServiceOutDto> loadAll(Pageable pageable) throws EntityNotFoundException {
        Page<MainService> mainServices = repository.findAll(pageable);
        if (mainServices.getContent().size() == 0)
            throw new EntityNotFoundException("No Main Service Found!");
        List<MainServiceOutDto> data = mainServices.getContent().stream()
                .map(m -> MainServiceOutDto.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .subServices(m.getSubServices().stream().map(
                                s -> new ServiceOutDto(s.getId(), s.getName(), s.getBasePrice())).toList())
                        .build()).toList();
        CustomPage<MainServiceOutDto> result = new CustomPage<>();
        result.setData(data);
        return result.convert(mainServices);
    }

    public MainServiceOutDto findById(Long id) throws EntityNotFoundException {
        MainService mainService = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MainService Not Found!"));
        List<SubService> subServices = mainService.getSubServices();
        List<ServiceOutDto> serviceOutDtos = subServices.stream()
                .map(s -> new ServiceOutDto(s.getId(), s.getName(), null)).toList();
        return new MainServiceOutDto(id, mainService.getName(), serviceOutDtos);
    }
}
