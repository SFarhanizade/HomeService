//package ir.farhanizade.homeservice.controller;
//
//import ir.farhanizade.homeservice.dto.in.ServiceInDto;
//import ir.farhanizade.homeservice.entity.service.MainService;
//import ir.farhanizade.homeservice.exception.EntityNotFoundException;
//import ir.farhanizade.homeservice.service.MainServiceService;
//import ir.farhanizade.homeservice.service.SubServiceService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit4.SpringRunner;
//import java.util.List;
//import static org.hamcrest.Matchers.hasSize;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(ServiceController.class)
//public class ServiceControllerTest extends AbstractRestControllerTest {
//
//    @MockBean
//    private MainServiceService service;
//
//    @MockBean
//    private SubServiceService subService;
//
//    @Test
//    public void test_save_main_service_isOk() throws Exception {
//        ServiceInDto serviceInDto = new ServiceInDto(0L, "main", "description", 1500L);
//        mvc.perform(get("/services/")
//                        .content(toJson(serviceInDto))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.code").value("1"));
//    }
//
//    @Test
//    public void test_save_sub_service_isOk() throws Exception {
//        ServiceInDto subServiceInDto = new ServiceInDto(1L, "service1", "description", 1500L);
//        mvc.perform(get("/services/")
//                        .content(toJson(subServiceInDto))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.code").value("1"));
//    }
//
//    @Test
//    public void test_load_main_service_isOk() throws Exception {
//        List<MainService> mainServices = List.of(new MainService());
//        Mockito.when(service.loadAll()).thenReturn(mainServices);
//
//        mvc.perform(post("/services/")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data", hasSize(1)));
//    }
//
//    @Test
//    public void test_load_main_service_throwException() throws Exception {
//        String message = "Not Found!";
//        Mockito.when(service.loadAll()).thenThrow(new EntityNotFoundException(message));
//
//        mvc.perform(post("/services/"))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value(message));
//    }
//
//}