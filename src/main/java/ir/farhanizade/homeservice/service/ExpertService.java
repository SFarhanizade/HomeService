package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.controller.api.filter.UserSpecification;
import ir.farhanizade.homeservice.dto.in.*;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
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
import ir.farhanizade.homeservice.security.ApplicationUserRole;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus.BUSY;
import static ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus.DONE;
import static ir.farhanizade.homeservice.entity.user.UserStatus.ACCEPTED;

@Service
@RequiredArgsConstructor
public class ExpertService {
    private final ExpertRepository repository;
    private final SubServiceService serviceManager;
    private final OrderService orderService;
    private final SuggestionService suggestionService;
    private final CommentService commentService;
    private final PasswordEncoder passwordEncoder;


    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto save(UserInDto user) throws NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, DuplicateEntityException, NullFieldException {
        Expert expert = user.convert2Expert();
        if (!Validation.isValid(expert))
            throw new UserNotValidException("User is not valid!");
        if (finalCheck(expert))
            throw new DuplicateEntityException("User exists!");
        expert.setRoles(Set.of(ApplicationUserRole.EXPERT));
        expert.setPassword(passwordEncoder.encode(expert.getPassword()));
        Validation.enableUser(expert);
        Expert result = repository.save(expert);
        return new EntityOutDto(result.getId());
    }

    @Transactional(rollbackFor = Exception.class)
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

    @Transactional(rollbackFor = Exception.class)
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
    public CustomPage<OrderOutDto> loadAvailableOrders(Long id, Pageable pageable) throws EntityNotFoundException {
        Expert entity = findById(id);
        Set<SubService> expertises = entity.getExpertises();
        if (expertises.size() == 0) throw new EntityNotFoundException("No Expertises Found For This User!");
//        Pageable pageable = Pageable.unpaged();
//        return orderService.findByExpertise(expertises,page);
        return orderService.loadByExpertises(expertises, pageable);
//        List<OrderOutDto> resultList = availableOrders.stream()
//                .map(o ->
//                        OrderOutDto.builder()
//                                .id(o.getId())
//                                .service(o.getService().getName())
//                                .price(o.getRequest().getPrice())
//                                .suggestedDateTime(o.getRequest().getSuggestedDateTime())
//                                .createdDateTime(o.getRequest().getDateTime())
//                                .status(o.getStatus())
//                                .build()).toList();
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
    public CustomPage<UserOutDto> findByCredit(BigDecimal credit, Pageable pageable) throws EntityNotFoundException {
        Page<Expert> page = repository.findByCredit(credit, pageable);
        //if (byCredit.isEmpty()) throw new EntityNotFoundException("No Users Found!");
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    public CustomPage<UserOutDto> findByStatus(UserStatus status, Pageable pageable) throws EntityNotFoundException {
        Page<Expert> page = repository.findByStatus(status, pageable);
        //if (byStatus.isEmpty()) throw new EntityNotFoundException("No Users Found!");
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    public CustomPage<UserOutDto> findByExpertise(SubService service, Pageable pageable) throws EntityNotFoundException {
        Page<Expert> page = repository.findByExpertise(service.getId(), pageable);
        //if (byExpertise.isEmpty()) throw new EntityNotFoundException("No Users Found!");
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    boolean finalCheck(Expert expert) {
        String email = expert.getEmail();
        Expert byEmail = repository.findByEmail(email);
        return byEmail != null && expert.getId() == null;
    }

    @Transactional(readOnly = true)
    public CustomPage<UserSearchOutDto> search(UserSearchInDto user, Pageable pageable) {
        UserSpecification<Expert> specification = new UserSpecification<>();
        Specification<Expert> filter = specification.getUsers(user);
        Page<Expert> all = repository.findAll(filter, pageable);
        CustomPage<Expert> result = new CustomPage<>();
        result.setPageSize(all.getSize());
        result.setLastPage(all.getTotalPages());
        result.setPageNumber(all.getNumber());
        result.setTotalElements(all.getTotalElements());
        result.setData(all.getContent());
        return convert2Dto(result);
    }

    private CustomPage<UserSearchOutDto> convert2Dto(CustomPage<Expert> list) {
        List<UserSearchOutDto> data = list.getData().stream()
                .map(c -> new UserSearchOutDto().convert2Dto(c)).toList();
        return CustomPage.<UserSearchOutDto>builder()
                .pageSize(list.getPageSize())
                .totalElements(list.getTotalElements())
                .lastPage(list.getLastPage())
                .pageNumber(list.getPageNumber())
                .data(data)
                .build();
    }

    public CustomPage<ExpertSuggestionOutDto> getSuggestions(Long id, Pageable pageable, SuggestionStatus... status) throws EntityNotFoundException {
        findById(id);
        return suggestionService.findAllByOwnerIdAndStatus(id, status, pageable);
    }

    private UserOutDto convert2Dto(Expert expert) {
        return UserOutDto.builder()
                .id(expert.getId())
                .name(expert.getName())
                .email(expert.getEmail())
                .credit(expert.getCredit())
                .build();
    }

    private CustomPage<UserOutDto> convert2Dto(Page<Expert> page) {
        List<UserOutDto> data = page.getContent().stream().map(this::convert2Dto).toList();
        return CustomPage.<UserOutDto>builder()
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .lastPage(page.getTotalPages())
                .pageNumber(page.getNumber())
                .data(data)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
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

    @Transactional(rollbackFor = Exception.class)
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

    public EntityOutDto finishWork(Long expertId, Long suggestionId) throws EntityNotFoundException, BusyOrderException, DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException {
        findById(expertId);
        Suggestion suggestion = suggestionService.findByIdAndOwnerId(suggestionId, expertId);
        if (suggestion.getSuggestionStatus().equals(SuggestionStatus.ACCEPTED) &&
                suggestion.getStatus().equals(BUSY)) {
            Order order = suggestion.getOrder();
            order.setStatus(OrderStatus.DONE);
            order.getRequest().setStatus(DONE);
            suggestion.setStatus(DONE);
            order.setFinishDateTime(new Date(System.currentTimeMillis()));
            suggestionService.save(suggestion);
            return new EntityOutDto(suggestionId);
        } else throw new BadEntryException("This order is not yours!");
    }

    @Transactional(readOnly = true)
    public boolean exists(Long id) {
        return repository.existsById(id);
    }

    public CommentOutDto getComment(Long id, Long comment) {
        exists(id);
        return commentService.findByIdAndExpertId(comment, id);
    }

    public CustomPage<OrderFinishOutDto> getOrders(Long id, Pageable pageable) {
        exists(id);
        return orderService.findAllByExpertId(id, pageable);
    }
}
