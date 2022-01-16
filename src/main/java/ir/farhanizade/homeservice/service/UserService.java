package ir.farhanizade.homeservice.service;


import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserPasswordInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.CommentOutDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.TransactionOutDto;
import ir.farhanizade.homeservice.dto.out.UserSearchOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.User;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.UserRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final ExpertService expertService;
    private final CustomerService customerService;
    private final AdminService adminService;
    private final TransactionService transactionService;
    private final CommentService commentService;

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto changePassword(UserPasswordInDto user) throws PasswordNotValidException, WrongPasswordException {
        String currentPassword = user.getCurrentPassword();
        String newPassword = user.getNewPassword();
        Long id = user.getId();
        Optional<User> byId = repository.findById(id);
        User entity = byId.get();
        if (currentPassword.equals(newPassword)) {
            throw new PasswordNotValidException("The new password is the same as the current password!");
        }
        if (!Validation.passwordIsValid(newPassword)) {
            throw new PasswordNotValidException("The new password is not valid!");
        }

        if (currentPassword.equals(entity.getPassword())) {
            throw new WrongPasswordException("The current password is not correct!");
        }
        entity.setPassword(newPassword);
        repository.save(entity);
        return new EntityOutDto(id);
    }

    public EntityOutDto save(UserInDto user, Class<?> type) throws DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, NullFieldException {
        EntityOutDto result;
        if (type == Expert.class) {
            result = expertService.save(user);

        } else if (type == Customer.class) {
            result = customerService.save(user);
        } else{
            result = adminService.save(user);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public CustomPage<UserSearchOutDto> search(UserSearchInDto user, Pageable pageable) {
        CustomPage<UserSearchOutDto> result;
        if ("expert".equals(user.getType())) {
            result = expertService.search(user, pageable);
        } else if ("customer".equals(user.getType())) {
            result = customerService.search(user, pageable);
        } else {
            result = searchUser(user, pageable);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public CustomPage<TransactionOutDto> getTransactions(Long id, Pageable pageable) throws EntityNotFoundException {
        exists(id);
        return transactionService.findByUserId(id, pageable);
    }

    @Transactional(readOnly = true)
    CustomPage<UserSearchOutDto> searchUser(UserSearchInDto user, Pageable pageable) {
        CustomPage<User> searchResult = repository.search(user,pageable);
        return convert2Dto(searchResult);
    }

    private CustomPage<UserSearchOutDto> convert2Dto(CustomPage<User> list){
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

    public boolean exists(Long id){
        return repository.existsById(id);
    }

    public TransactionOutDto getTransaction(Long id, Long transaction) throws EntityNotFoundException {
        exists(id);
        return transactionService.findById(transaction);
    }

    @Transactional(readOnly = true)
    public CustomPage<CommentOutDto> getComments(Long id, Pageable pageable) throws EntityNotFoundException {
        exists(id);
        return commentService.findAllByUserId(id, pageable);
    }
}
