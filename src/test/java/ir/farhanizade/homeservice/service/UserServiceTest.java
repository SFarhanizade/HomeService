package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserIncreaseCreditInDto;
import ir.farhanizade.homeservice.dto.in.UserPasswordInDto;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.user.Admin;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.User;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.notNull;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {

    @TestConfiguration
    @EnableAspectJAutoProxy
    @ComponentScan("ir.farhanizade.homeservice")
    public static class UserServiceTestConfig {
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository repository;

    @MockBean
    private ExpertService expertService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private AdminService adminService;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private CommentService commentService;


    private UserInDto getValidUserDto() {
        return UserInDto.builder()
                .firstname("User")
                .lastname("User")
                .email("User@User.ir")
                .password("abcd1234")
                .build();
    }

    private User getValidUser() {
        return getValidUserDto().convert2Expert();
    }

    @Test
    void test_save_user_is_ok() {
        UserInDto user = getValidUserDto();
        EntityOutDto result = new EntityOutDto(1L);

        try {
            Mockito.when(adminService.save(user))
                    .thenReturn(result);
            Mockito.when(expertService.save(user))
                    .thenReturn(result);
            Mockito.when(customerService.save(user))
                    .thenReturn(result);


            assertEquals(result,
                    userService.save(user, Admin.class));
            assertEquals(result,
                    userService.save(user, Expert.class));
            assertEquals(result,
                    userService.save(user, Customer.class));

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    void test_changePassword_same_password_throws_exception() {
        UserPasswordInDto userPasswordInDto = new UserPasswordInDto(
                1L,
                "abcd1234",
                "abcd1234");

        try {
            repository.save(getValidUser());
            assertThrows(PasswordNotValidException.class,
                    () -> userService.changePassword(userPasswordInDto));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_changePassword_invalid_password_throws_exception() {
        UserPasswordInDto userPasswordInDto = new UserPasswordInDto(
                1L,
                "abcd1234",
                "1234");

        try {
            repository.save(getValidUser());
            assertThrows(PasswordNotValidException.class,
                    () -> userService.changePassword(userPasswordInDto));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_changePassword_wrong_password_throws_exception() {
        UserPasswordInDto userPasswordInDto = new UserPasswordInDto(
                1L,
                "abcd1235",
                "abcd1236");

        try {
            repository.save(getValidUser());
            assertThrows(WrongPasswordException.class,
                    () -> userService.changePassword(userPasswordInDto));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_changePassword_is_ok() {
        UserPasswordInDto userPasswordInDto = new UserPasswordInDto(
                1L,
                "abcd1234",
                "abcd12345");
        EntityOutDto result = new EntityOutDto(1L);

        try {
            repository.save(getValidUser());
            assertEquals(result, userService.changePassword(userPasswordInDto));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

//    @Test
//    void test_search_user_is_ok() {
//        UserSearchInDto customerSearchInDto = UserSearchInDto.builder()
//                .type("customer")
//                .build();
//
//        UserSearchInDto expertSearchInDto = UserSearchInDto.builder()
//                .type("expert")
//                .build();
//
//
//        UserSearchOutDto customer = UserSearchOutDto.builder()
//                .type("customer").build();
//
//        UserSearchOutDto expert = UserSearchOutDto.builder()
//                .type("expert").build();
//
//        CustomPage<UserSearchOutDto> resultCustomer = CustomPage.<UserSearchOutDto>builder()
//                .data(List.of(customer)).build();
//
//        CustomPage<UserSearchOutDto> resultExpert = CustomPage.<UserSearchOutDto>builder()
//                .data(List.of(expert)).build();
//
//        try {
//            Mockito.when(expertService.search(notNull(), notNull()))
//                    .thenReturn(resultExpert);
//
//            Mockito.when(customerService.search(notNull(), notNull()))
//                    .thenReturn(resultCustomer);
//
//            assertEquals(resultExpert,
//                    userService.search(expertSearchInDto, Pageable.ofSize(10)));
//
//            assertEquals(resultCustomer,
//                    userService.search(customerSearchInDto, Pageable.ofSize(10)));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();
//        }
//    }

    @Test
    void test_get_transaction_is_ok() {
        TransactionOutDto result = TransactionOutDto.builder().id(1L).build();

        try {
            repository.save(getValidUser());
            Mockito.when(transactionService.findById(1L))
                    .thenReturn(result);
            assertEquals(result, userService.getTransaction(1L));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            fail();
        } catch (UserNotLoggedInException e) {
            e.printStackTrace();
        } catch (BadEntryException e) {
            e.printStackTrace();
        }

    }

    @Test
    void test_get_comments_is_ok() throws UserNotLoggedInException, BadEntryException, EntityNotFoundException {
        CustomPage<CommentOutDto> result = CustomPage.<CommentOutDto>builder()
                .data(List.of(new CommentOutDto(),
                        new CommentOutDto(),
                        new CommentOutDto()))
                .build();

        repository.save(getValidUser());
        Mockito.when(commentService.findAllByUserId(notNull()))
                .thenReturn(result);

        assertEquals(3,
                userService.getComments(Pageable.ofSize(10)).getData().size());

    }

    @Test
    void test_load_credit_by_id_is_ok() {

        try {
            repository.save(getValidUser());
            assertEquals(1L, userService.loadCredit().getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    void test_increase_credit_is_ok() {
        UserIncreaseCreditInDto userIncreaseCreditInDto =
                new UserIncreaseCreditInDto(5000L);

        try {
            repository.save(getValidUser());
            assertEquals(new BigDecimal(5000), userService.increaseCredit(userIncreaseCreditInDto).getBalance());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }


    }
}