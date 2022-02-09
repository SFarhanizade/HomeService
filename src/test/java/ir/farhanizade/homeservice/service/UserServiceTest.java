package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserIncreaseCreditInDto;
import ir.farhanizade.homeservice.dto.in.UserPasswordInDto;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.user.*;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.UserRepository;
import ir.farhanizade.homeservice.security.user.LoggedInUser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.notNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static MockedStatic<LoggedInUser> loggedInUserMockedStatic;

    @SpyBean
    private ExpertService expertService;

    @SpyBean
    private CustomerService customerService;

    @MockBean
    private AdminService adminService;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private CommentService commentService;

    @BeforeAll()
    public static void init() {
        loggedInUserMockedStatic = Mockito.mockStatic(LoggedInUser.class);
    }

    @AfterAll()
    public static void close() {
        loggedInUserMockedStatic.close();
    }

    private UserInDto getValidUserDto() {
        return UserInDto.builder()
                .type("customer")
                .firstname("User")
                .lastname("User")
                .email("User@User.ir")
                .password("abcd1234")
                .build();
    }

    private MyUser getValidUser() {
        return getValidUserDto().convert2Expert();
    }

    @Test
    void test_save_user_is_ok() {
        UserInDto user = getValidUserDto();


        try {

            Mockito.doReturn(1L).when(expertService).save(user);

            Mockito.doReturn(1L).when(customerService).save(user);

            user.setType("expert");
            assertTrue(userService.save(user) instanceof UUIDOutDto);

            user.setType("customer");
            assertTrue(userService.save(user) instanceof UUIDOutDto);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    void test_changePassword_same_password_throws_exception() {
        UserPasswordInDto userPasswordInDto = new UserPasswordInDto(
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
                "abcd1235",
                "abcd1236");

        try {
            repository.save(getValidUser());
            loggedInUserMockedStatic.when(LoggedInUser::id).thenReturn(1L);
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
                "abcd1234",
                "abcd12345");
        EntityOutDto result = new EntityOutDto(1L);

        try {
            MyUser user = getValidUser();
            String password = user.getPassword();
            user.setPassword(passwordEncoder.encode(password));
            repository.save(user);
            loggedInUserMockedStatic.when(LoggedInUser::id).thenReturn(1L);
            assertEquals(result, userService.changePassword(userPasswordInDto));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_get_transaction_is_ok() {
        TransactionOutDto result = TransactionOutDto.builder().id(1L).build();

        try {
            repository.save(getValidUser());
            Mockito.when(transactionService.findById(1L))
                    .thenReturn(result);
            assertEquals(result, userService.getTransaction(1L));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_get_comments_is_ok() throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, AccountIsLockedException {
        CustomPage<CommentOutDto> result = CustomPage.<CommentOutDto>builder()
                .data(List.of(new CommentOutDto(),
                        new CommentOutDto(),
                        new CommentOutDto()))
                .build();

        repository.save(getValidUser());
        loggedInUserMockedStatic.when(LoggedInUser::id).thenReturn(1L);
        Mockito.when(commentService.findAllByUserId(notNull()))
                .thenReturn(result);

        assertEquals(3,
                userService.getComments(Pageable.ofSize(10)).getData().size());

    }

    @Test
    void test_load_credit_by_id_is_ok() {

        try {
            repository.save(getValidUser());
            loggedInUserMockedStatic.when(LoggedInUser::id).thenReturn(1L);
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
            loggedInUserMockedStatic.when(LoggedInUser::id).thenReturn(1L);
            assertEquals("5000.00", userService.increaseCredit(userIncreaseCreditInDto).getBalance().toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_verify_email_is_ok() throws DuplicateEntityException, NameNotValidException, UnsupportedEncodingException, EmailNotValidException, PasswordNotValidException, UserNotValidException, NullFieldException, NoSuchAlgorithmException, UUIDNotFoundException, UserNotLoggedInException, BadEntryException, EntityNotFoundException {
        UUIDOutDto uuid = userService.save(getValidUserDto());

        EntityOutDto result = userService.verifyEmail(uuid.getUuid());

        assertEquals(1L, result.getId());
    }
}