package ir.farhanizade.homeservice;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.*;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)

public class HomeServiceIntegrationTest {

    @LocalServerPort
    int serverPort;

    @Test
    public void test() {
        TestRestTemplate restTemplate = new TestRestTemplate();

        //add admin
        UserInDto adminInDto = UserInDto.builder()
                .firstname("admin")
                .lastname("admin")
                .email("admin@admin.ir")
                .password("abcd1234")
                .build();
        ResponseEntity<ResponseResult> response = restTemplate.postForEntity(createUrl("/admins"), adminInDto, ResponseResult.class);
        LinkedHashMap<String, Integer> map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(1, map.get("id"));


        //add expert
        UserInDto expertInDto = UserInDto.builder()
                .firstname("expert")
                .lastname("expert")
                .email("expert@expert.ir")
                .password("abcd1234")
                .build();
        response = restTemplate.postForEntity(createUrl("/experts"), expertInDto, ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(2, map.get("id"));


        //add customer
        UserInDto customerInDto = UserInDto.builder()
                .firstname("customer")
                .lastname("customer")
                .email("customer@customer.ir")
                .password("abcd1234")
                .build();
        response = restTemplate.postForEntity(createUrl("/customers"), customerInDto, ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(3, map.get("id"));

        //add main service
        ServiceInDto serviceInDto = new ServiceInDto(0L, "mainService", "description", 2000L);
        response = restTemplate.postForEntity(createUrl("/services"), serviceInDto, ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(1, map.get("id"));

        //add subService
        ServiceInDto subServiceInDto = new ServiceInDto(1L, "subService1", "description", 2000L);
        response = restTemplate.postForEntity(createUrl("/services"), subServiceInDto, ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(1, map.get("id"));

        //accept expert by admin
        response = restTemplate.postForEntity(createUrl("/admins/1/experts/2/accept"), null, ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(2, map.get("id"));

        //add service to expert
        ExpertAddServiceInDto expertAddServiceInDto = new ExpertAddServiceInDto(2L, 1L);
        response = restTemplate.postForEntity(createUrl("/experts/addService"), expertAddServiceInDto, ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(2, map.get("expertId"));

        //add order
        RequestInDto requestInDto = new RequestInDto(
                1L,
                2000L,
                new Date(2022, 02, 15, 18, 40),
                "details",
                "Mashhad");
        response = restTemplate.postForEntity(createUrl("/customers/3/orders"), requestInDto, ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(1, map.get("id"));

        //get orders for expert with id 2
        response = restTemplate.getForEntity(createUrl("/orders/experts/2"), ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(1, map.get("totalElements"));

        //add suggestion from expert(2) for order(1)
        ExpertAddSuggestionInDto expertAddSuggestionInDto = new ExpertAddSuggestionInDto(
                1L,
                2000L,
                new Date(2022, 02, 15, 18, 40),
                "details",
                3.5D);
        response = restTemplate.postForEntity(createUrl("/experts/2/suggestions"), expertAddSuggestionInDto, ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(1, map.get("suggestionId"));

        //get added suggestions to customers
        response = restTemplate.getForEntity(createUrl("/customers/3/suggestions"), ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(1, map.get("totalElements"));

        //accept suggestion(1)
        response = restTemplate.postForEntity(createUrl("/customers/3/suggestions/1/accept"), null, ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(1, map.get("id"));

        //final accept by expert
        response = restTemplate.postForEntity(createUrl("/experts/2/suggestions/1/accept"), null, ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(BaseMessageStatus.BUSY.name(), map.get("answer"));

        //expert start to work
        response = restTemplate.postForEntity(createUrl("/experts/2/suggestions/1/start"), null, ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(1, map.get("id"));

        //expert finish work
        response = restTemplate.postForEntity(createUrl("/experts/2/suggestions/1/done"), null, ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(1, map.get("id"));

        //customer increase credit
        UserIncreaseCreditInDto userIncreaseCreditInDto = new UserIncreaseCreditInDto(5000L);
        response = restTemplate.postForEntity(createUrl("/customers/3/credit"), userIncreaseCreditInDto, ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(Double.valueOf(5000.0), map.get("balance"));

        //customer pay for order
        response = restTemplate.postForEntity(createUrl("/customers/3/suggestions/1/pay"), null, ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(1, map.get("id"));

        //check if the amount is subtracted from customer credit and added to expert credit
        response = restTemplate.getForEntity(createUrl("/customers/3/credit"), ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(Double.valueOf(3000), map.get("credit"));

        response = restTemplate.getForEntity(createUrl("/experts/2/credit"), ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(Double.valueOf(2000 * 0.7), map.get("credit"));


        //customer comment on the work of the expert
        CommentInDto commentInDto = new CommentInDto(5, "best");
        response = restTemplate.postForEntity(createUrl("/customers/3/suggestions/1/comment"), commentInDto, ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(1, map.get("id"));

        //show comments of the expert
        response = restTemplate.getForEntity(createUrl("/experts/2/comments"), ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(1, map.get("totalElements"));

        //show comment(1) of the expert
        response = restTemplate.getForEntity(createUrl("/experts/2/comments/1"), ResponseResult.class);
        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
        assertEquals(5, map.get("points"));
        //TODO: continue integration test here
    }

    private String createUrl(String path) {
        return "http://localhost:" + serverPort + "/api" + path;
    }
}
