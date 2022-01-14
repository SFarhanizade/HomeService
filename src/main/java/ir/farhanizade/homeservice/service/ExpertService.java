package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.*;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.OrderStatus;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.ExpertRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus.CANCELLED;
import static ir.farhanizade.homeservice.entity.order.message.SuggestionStatus.PENDING;
import static ir.farhanizade.homeservice.entity.order.message.SuggestionStatus.REJECTED;
import static ir.farhanizade.homeservice.entity.user.UserStatus.ACCEPTED;

@Service
@RequiredArgsConstructor
public class ExpertService {
    private final ExpertRepository repository;
    private final SubServiceService serviceManager;
    private final OrderService orderService;
    private final SuggestionService suggestionService;
    private final RequestService requestService;

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

    @Transactional
    public ExpertAddSuggestionOutDto suggest(Long expertId, ExpertAddSuggestionInDto request) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException {
        Expert expert = findById(expertId);
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

    @Transactional
    public ExpertAddServiceOutDto addService(ExpertAddServiceInDto request) throws EntityNotFoundException, DuplicateEntityException, ExpertNotAcceptedException {
        Expert expert = findById(request.getExpertId());
        if (!expert.getStatus().equals(ACCEPTED)) throw new ExpertNotAcceptedException("User is not allowed!");
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
                        .collect(Collectors.toSet()))
                .build();
        return result;
    }

    @Transactional(readOnly = true)
    public List<OrderOutDto> loadAvailableOrders(ExpertInDto expert) throws EntityNotFoundException {
        Expert entity = findById(expert.getId());
        Set<SubService> expertises = entity.getExpertises();
        if (expertises.size() == 0) throw new EntityNotFoundException("No Expertises Found For This User!");
        List<Order> availableOrders = orderService.loadByExpertises(expertises);
        List<OrderOutDto> resultList = availableOrders.stream()
                .map(o ->
                        OrderOutDto.builder()
                                .id(o.getId())
                                .service(o.getService().getName())
                                .price(o.getRequest().getPrice())
                                .suggestedDateTime(o.getRequest().getSuggestedDateTime())
                                .createdDateTime(o.getRequest().getDateTime())
                                .status(o.getStatus())
                                .build()).toList();
        return resultList;
    }

    @Transactional(readOnly = true)
    public Expert findById(Long id) throws EntityNotFoundException {
        Optional<Expert> byId = repository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        } else throw new EntityNotFoundException("Expert doesn't exist!");
    }

    @Transactional(readOnly = true)
    public UserOutDto findByEmail(String email) throws EntityNotFoundException {
        if (email == null)
            throw new IllegalStateException("Null Email");
        Expert byEmail = repository.findByEmail(email);
        if (byEmail == null) throw new EntityNotFoundException("No User Found!");
        return convert2Dto(byEmail);
    }

    @Transactional(readOnly = true)
    public List<UserOutDto> findByCredit(BigDecimal credit) throws EntityNotFoundException {
        List<Expert> byCredit = repository.findByCredit(credit);
        if (byCredit.isEmpty()) throw new EntityNotFoundException("No Users Found!");
        return convert2Dto(byCredit);
    }

    @Transactional(readOnly = true)
    public List<UserOutDto> findByStatus(UserStatus status) throws EntityNotFoundException {
        List<Expert> byStatus = repository.findByStatus(status);
        if (byStatus.isEmpty()) throw new EntityNotFoundException("No Users Found!");
        return convert2Dto(byStatus);
    }

    @Transactional(readOnly = true)
    public List<UserOutDto> findByExpertise(SubService service) throws EntityNotFoundException {
        List<Expert> byExpertise = repository.findByExpertise(service.getId());
        if (byExpertise.isEmpty()) throw new EntityNotFoundException("No Users Found!");
        return convert2Dto(byExpertise);
    }

    @Transactional(readOnly = true)
    boolean finalCheck(Expert expert) {
        String email = expert.getEmail();
        Expert byEmail = repository.findByEmail(email);
        return byEmail != null && expert.getId() == null;
    }

    @Transactional(readOnly = true)
    public List<UserSearchOutDto> search(UserSearchInDto user) {
        List<Expert> searchResult = repository.search(user);
        List<UserSearchOutDto> result = searchResult.stream()
                .map(e -> new UserSearchOutDto().convert2Dto(e))
                .peek(e -> e.setType("expert"))
                .toList();
        return result;
    }

    public List<ExpertSuggestionOutDto> getSuggestions(Long id, SuggestionStatus... status) throws EntityNotFoundException {
        findById(id);
        return suggestionService.findAllByOwnerIdAndStatus(id, status);
    }

    private UserOutDto convert2Dto(Expert expert) {
        return UserOutDto.builder()
                .id(expert.getId())
                .name(expert.getFName() + " " + expert.getLName())
                .email(expert.getEmail())
                .credit(expert.getCredit())
                .build();
    }

    private List<UserOutDto> convert2Dto(List<Expert> expertList) {
        return expertList.stream()
                .map(this::convert2Dto).toList();
    }

    @Transactional
    public SuggestionAnswerOutDto answerSuggestion(Long ownerId, Long suggestionId, BaseMessageStatus status) throws BadEntryException, EntityNotFoundException {
        return suggestionService.answer(ownerId, suggestionId, status);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public EntityOutDto acceptExpert(Long expertId) throws EntityNotFoundException {
        Expert expert = findById(expertId);
        expert.setStatus(ACCEPTED);
        repository.save(expert);
        return new EntityOutDto(expertId);
    }

    @Transactional
    public EntityOutDto startToWork(Long expertId, Long suggestionId) throws EntityNotFoundException, BadEntryException, BusyOrderException, DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException {
        findById(expertId);
        Suggestion suggestion = suggestionService.findByIdAndOwnerId(suggestionId, expertId);
        if (suggestion.getSuggestionStatus().equals(SuggestionStatus.ACCEPTED)) {
            Order order = suggestion.getOrder();
            order.setStatus(OrderStatus.STARTED);
            suggestionService.save(suggestion);
            return new EntityOutDto(suggestionId);
        } else throw new BadEntryException("This order is not yours!");
    }
}
