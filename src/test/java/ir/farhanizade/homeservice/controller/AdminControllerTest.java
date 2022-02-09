//package ir.farhanizade.homeservice.controller;
//
//import ir.farhanizade.homeservice.dto.in.UserInDto;
//import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
//import ir.farhanizade.homeservice.dto.out.EntityOutDto;
//import ir.farhanizade.homeservice.dto.out.UserSearchOutDto;
//import ir.farhanizade.homeservice.entity.CustomPage;
//import ir.farhanizade.homeservice.service.*;
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.List;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.mockito.ArgumentMatchers.notNull;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(AdminController.class)
////@RequiredArgsConstructor
//class AdminControllerTest extends AbstractRestControllerTest {
//
//
////    @LocalServerPort
////    int serverPort;
//
//    @MockBean
//    private UserService userService;
//    @MockBean
//    private OrderService orderService;
//    @MockBean
//    private MainServiceService mainService;
//    @MockBean
//    private SubServiceService subService;
//
//    //private final PasswordEncoder passwordEncoder;
//
//    @MockBean
//    private AdminService adminService;
//
//    @Test
//    @WithMockUser
//    void test_create_isOk() throws Exception {
//        UserInDto admin = UserInDto.builder()
//                .type("admin")
//                .firstname("admin")
//                .lastname("adminPour")
//                .email("admin@admin.ir")
//                .password("abcd1234")
//                .build();
//
//        Mockito.when(adminService.save(admin))
//                .thenReturn(new EntityOutDto(1L));
//
//        mvc.perform(post(createUrl(""))
//                        .content(toJson(admin))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data").value(new EntityOutDto(1L)));
//
//    }
//
//    @Test
//    void test_acceptExpert_isOk() throws Exception {
//        EntityOutDto result = new EntityOutDto(1L);
//
//        Mockito.when(adminService.acceptExpert(1L))
//                .thenReturn(result);
//
//        mvc.perform(get("/admins/1/experts/1/accept"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data.id").value(1L));
//    }
//
//    @Test
//    void test_search_is_ok() throws Exception {
//        UserSearchInDto request = new UserSearchInDto();
//
//        CustomPage<UserSearchOutDto> result = CustomPage.<UserSearchOutDto>builder()
//                .data(List.of(new UserSearchOutDto()))
//                .build();
//
//        Mockito.when(adminService.search(notNull(), notNull()))
//                .thenReturn(result);
//
//        mvc.perform(post("/admins/1/search")
//                        .content(toJson(request))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.data.data", hasSize(1)));
//    }
//
//    private String createUrl(String path) {
//        return "http://localhost:" + 8080 + "/api/management" + path;
//    }
//}