package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.*;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.OrderStatus;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.ExpertRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpertService {
    private final ExpertRepository repository;
    private final SubServiceService serviceManager;
    private final OrderService orderService;
    private final SuggestionService suggestionService;

    @Transactional
    public EntityOutDto save(UserInDto user) throws NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, DuplicateEntityException, NullFieldException {
        Expert expert = user.convert2Expert();
        if (!Validation.isValid(expert))
            throw new UserNotValidException("User is not valid!");
        if (finalCheck(expert))
            throw new DuplicateEntityException("User exists!");
        Expert result = repository.save(expert);
        return new EntityOutDto(result.getId());
    }

    public List<OrderOutDto> loadAvailableOrders(ExpertInDto expert) throws EntityNotFoundException {
        Expert entity = findById(expert.getId());
        Set<SubService> expertises = entity.getExpertises();
        if (expertises.size() == 0) throw new EntityNotFoundException("No Expertises Found For This User!");
        List<Order> availableOrders = orderService.loadByExpertises(expertises, OrderStatus.WAITING_FOR_SUGGESTION);
        List<OrderOutDto> resultList = availableOrders.stream()
                .map(o ->
                        OrderOutDto.builder()
                                .id(o.getId())
                                .service(o.getService().getName())
                                .price(o.getRequest().getPrice())
                                .suggestedDateTime(o.getRequest().getSuggestedDateTime())
                                .createdDateTime(o.getRequest().getDateTime())
                                .build()).toList();
        return resultList;
    }

    public ExpertAddSuggestionOutDto suggest(ExpertAddSuggestionInDto request) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException {
        Expert expert = findById(request.getExpertId());
        Order order = orderService.findById(request.getOrderId());
        Suggestion suggestion = Suggestion.builder()
                .owner(expert)
                .order(order)
                .price(new BigDecimal(request.getPrice()))
                .suggestedDateTime(request.getDateTime())
                .details(request.getDetails())
                .duration(request.getDuration())
                .build();
        ExpertAddSuggestionOutDto result = suggestionService.save(suggestion);
        return result;
    }

    public Expert findById(Long id) throws EntityNotFoundException {
        Optional<Expert> byId = repository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        } else throw new EntityNotFoundException("Expert doesn't exist!");
    }

    @Transactional(readOnly = true)
    public Expert findByEmail(String email) {
        if (email == null)
            throw new IllegalStateException("Null Email");
        return repository.findByEmail(email);
    }

    public List<Expert> findByCredit(BigDecimal credit) {
        return repository.findByCredit(credit);
    }

    public List<Expert> findByStatus(UserStatus status) {
        return repository.findByStatus(status);
    }

    public List<Expert> findByExpertise(SubService service) {
        return repository.findByExpertise(service.getId());
    }

    private boolean finalCheck(Expert expert) {
        String email = expert.getEmail();
        Expert byEmail = repository.findByEmail(email);
        return byEmail != null && expert.getId() == null;
    }

    public List<UserSearchOutDto> search(UserSearchInDto user) {
        List<Expert> searchResult = repository.search(user);
        List<UserSearchOutDto> result = searchResult.stream()
                .map(e -> new UserSearchOutDto().convert2Dto(e))
                .peek(e -> e.setType("expert"))
                .toList();
        return result;
    }

    @Transactional
    public ExpertAddServiceOutDto addService(ExpertAddServiceInDto request) throws EntityNotFoundException, DuplicateEntityException {
        Expert expert;
        Optional<Expert> byId = repository.findById(request.getExpertId());
        if (byId.isPresent()) {
            expert = byId.get();
        } else {
            throw new EntityNotFoundException("User doesn't exist!");
        }
        SubService service = serviceManager.loadById(request.getServiceId());
        boolean serviceExists = !expert.addService(service);
        if (serviceExists) {
            throw new DuplicateEntityException("The service exists for this expert!");
        }
        Expert saved = repository.save(expert);
        ExpertAddServiceOutDto result = ExpertAddServiceOutDto.builder()
                .expertId(saved.getId())
                .services(expert.getExpertises().stream()
                        .map(s -> new EntityOutDto(s.getId()))
                        .collect(Collectors.toSet())
                )
                .build();
        return result;
    }
}
