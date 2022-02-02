package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.dto.in.TimeRangeInDto;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserPasswordInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.OrderOfUserOutDto;
import ir.farhanizade.homeservice.dto.out.ReportRegisterTimeUsersOutDto;
import ir.farhanizade.homeservice.dto.out.UserSearchOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.exception.PasswordNotValidException;
import ir.farhanizade.homeservice.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.notNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest extends AbstractRestControllerTest {

    @MockBean
    private UserService userService;

    @Test
    public void test_change_password_isAccepted() throws Exception {

        UserPasswordInDto userPasswordInDto = new UserPasswordInDto(1L, "abcd1234", "abcd12345");

        Mockito.when(userService.changePassword(userPasswordInDto))
                .thenReturn(new EntityOutDto(1L));

        mvc.perform(post("/users/changePassword")
                        .content(toJson(userPasswordInDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    public void test_change_password_isNotValid() throws Exception {
        UserPasswordInDto userPasswordInDto = new UserPasswordInDto(1L, "abcd1234", "abcd12345");

        Mockito.when(userService.changePassword(userPasswordInDto))
                .thenThrow(new PasswordNotValidException(""));

        mvc.perform(post("/users/changePassword")
                        .content(toJson(userPasswordInDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.code").value(-1));
    }

    @Test
    public void test_search_isOk() throws Exception {
        UserSearchInDto userSearchInDto = new UserSearchInDto();
        CustomPage<UserSearchOutDto> result = CustomPage.<UserSearchOutDto>builder()
                .pageSize(10)
                .totalElements(10L)
                .lastPage(1)
                .pageNumber(0)
                .data(List.of(new UserSearchOutDto()))
                .build();

        Mockito.when(userService.search(userSearchInDto, Pageable.ofSize(20)))
                .thenReturn(result);

        mvc.perform(get("/users")
                        .content(toJson(userSearchInDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.data.pageSize").value(10));
    }

    @Test
    public void test_get_orders_is_ok() throws Exception {
        CustomPage<OrderOfUserOutDto> result = CustomPage.<OrderOfUserOutDto>builder()
                .data(List.of(new OrderOfUserOutDto())).build();

        Mockito.when(userService.getOrders(notNull()))
                .thenReturn(result);

        mvc.perform(get("/users/1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data", hasSize(1)));
    }

    @Test
    public void test_get_number_of_users_by_register_time_is_ok() throws Exception {
        TimeRangeInDto request = new TimeRangeInDto(new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()));

        Mockito.when(userService.getNumberOfUsersByRegisterTime(notNull()))
                .thenReturn(new ReportRegisterTimeUsersOutDto(5l, 5l));

        mvc.perform(get("/users/report/registerTime")
                        .content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.experts").value(5L));
    }
}