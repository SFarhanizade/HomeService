package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.dto.in.ExpertInDto;
import ir.farhanizade.homeservice.dto.out.OrderOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.service.ExpertService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
class OrderControllerTest extends AbstractRestControllerTest {

    @MockBean
    private ExpertService expertService;

    @Test
    void showList() throws Exception {
        ExpertInDto request = new ExpertInDto();

        CustomPage<OrderOutDto> result = CustomPage.<OrderOutDto>builder()
                .data(List.of(new OrderOutDto(), new OrderOutDto(), new OrderOutDto())).build();

        Mockito.when(expertService.loadAvailableOrders(request, Pageable.ofSize(20)))
                .thenReturn(result);

        mvc.perform(post("/orders")
                        .content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data", hasSize(3)));
    }
}