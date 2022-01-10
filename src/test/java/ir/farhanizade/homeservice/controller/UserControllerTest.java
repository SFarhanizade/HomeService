//package ir.farhanizade.homeservice.controller;
//
//import ir.farhanizade.homeservice.dto.in.UserInDto;
//import ir.farhanizade.homeservice.dto.out.EntityOutDto;
//import ir.farhanizade.homeservice.service.ExpertService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//@WebMvcTest(UserController.class)
//class UserControllerTest extends AbstractRestControllerTest {
//
//    @MockBean
//    private ExpertService userService;
//
//    @Test
//    void create() throws Exception {
//        UserInDto user = UserInDto.builder()
//                .type("expert")
//                .firstname("ali")
//                .lastname("alavi")
//                .email("123456@123.ir")
//                .password("abcd1234")
//                .build();
//
//        EntityOutDto result = new EntityOutDto(1L);
//        Mockito.when(userService.save(user)).thenReturn(result);
//
//        mvc.perform(post("/show", user)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
//    }
//
//    @Test
//    void changePassword() {
//    }
//}