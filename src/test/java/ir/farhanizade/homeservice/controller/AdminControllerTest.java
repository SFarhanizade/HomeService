package ir.farhanizade.homeservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.UserSearchOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.user.Admin;
import ir.farhanizade.homeservice.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.notNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AdminController.class)
class AdminControllerTest extends AbstractRestControllerTest {

    @MockBean
    private UserController userController;

    @MockBean
    private AdminService adminService;

    @Test
    void test_create_isOk() throws Exception {
        UserInDto admin = UserInDto.builder()
                .firstname("admin")
                .lastname("adminPour")
                .email("admin@admin.ir")
                .password("abcd1234")
                .build();

        ResponseResult<EntityOutDto> response = ResponseResult.<EntityOutDto>builder()
                .code(1)
                .data(new EntityOutDto(1L))
                .message("User saved successfully!")
                .build();

        ResponseEntity<ResponseResult<EntityOutDto>> returnValue = ResponseEntity.status(HttpStatus.CREATED).body(response);

        Mockito.when(userController.create(admin, Admin.class))
                .thenReturn(returnValue);

        mvc.perform(post("/admins")
                        .content(toJson(admin))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").value(new EntityOutDto(1L)));

    }

    @Test
    void test_acceptExpert_isOk() throws Exception {
        EntityOutDto result = new EntityOutDto(1L);

        Mockito.when(adminService.acceptExpert(1L, 1L))
                .thenReturn(result);

        mvc.perform(get("/admins/1/experts/1/accept"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void test_search_is_ok() throws Exception {
        UserSearchInDto request = new UserSearchInDto();

        CustomPage<UserSearchOutDto> result = CustomPage.<UserSearchOutDto>builder()
                .data(List.of(new UserSearchOutDto()))
                .build();

        Mockito.when(adminService.search(notNull(), notNull(), notNull()))
                .thenReturn(result);

        mvc.perform(post("/admins/1/search")
                        .content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.data", hasSize(1)));
    }
}