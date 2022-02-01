package ir.farhanizade.homeservice;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.*;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.security.jwt.UsernameAndPasswordAuthenticationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)

public class HomeServiceIntegrationTest {

    @LocalServerPort
    int serverPort;

    @Test
    public void test() {
        TestRestTemplate restTemplate = new TestRestTemplate();

        //add expert
        UserInDto expertInDto = UserInDto.builder()
                .type("expert")
                .firstname("expert")
                .lastname("expert")
                .email("expert@expert.ir")
                .password("abcd1234")
                .build();
        ResponseEntity<ResponseResult> response = restTemplate.postForEntity(createUrl("/users/sign-up"), expertInDto, ResponseResult.class);
        LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) response.getBody().getData();
        String expertUUID = map.get("uuid");
        assertNotNull(expertUUID);

        //verify expert email
        response = restTemplate.getForEntity(createUrl("/users/verify/" + expertUUID), ResponseResult.class);
        assertEquals("User verified successfully!", response.getBody().getMessage());


        //add customer
        UserInDto customerInDto = UserInDto.builder()
                .type("customer")
                .firstname("customer")
                .lastname("customer")
                .email("customer@customer.ir")
                .password("abcd1234")
                .build();
        response = restTemplate.postForEntity(createUrl("/users/sign-up"), customerInDto, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        String customerUUID = map.get("uuid");
        assertNotNull(customerUUID);

        //verify customer email
        response = restTemplate.getForEntity(createUrl("/users/verify/" + customerUUID), ResponseResult.class);
        assertEquals("User verified successfully!", response.getBody().getMessage());

        //admin login
        UsernameAndPasswordAuthenticationRequest adminLoginInfo =
                new UsernameAndPasswordAuthenticationRequest("admin@admin.ir", "admin1234");
        response = restTemplate.postForEntity(createUrl("/users/login"), adminLoginInfo, ResponseResult.class);
        String adminToken = response.getHeaders().get("Authorization").get(0);

        //accept expert by logged in admin
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", adminToken);
        HttpEntity<Object> request = new HttpEntity<>(null, httpHeaders);
        response = restTemplate.postForEntity(createUrl("/management/experts/2/accept"), request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue(String.valueOf(map.get("id")).equals("2"));

        //add main service
        ServiceInDto serviceInDto = new ServiceInDto(0L, "main1", "description", 1000L);
        request = new HttpEntity<>(serviceInDto, httpHeaders);
        response = restTemplate.postForEntity(createUrl("/management/services"), request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue(String.valueOf(map.get("id")).equals("1"));

        //add sub service
        serviceInDto = new ServiceInDto(1L, "sub1", "description", 1000L);
        request = new HttpEntity<>(serviceInDto, httpHeaders);
        response = restTemplate.postForEntity(createUrl("/management/services"), request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue(String.valueOf(map.get("id")).equals("1"));

        //expert login
        UsernameAndPasswordAuthenticationRequest expertLoginInfo =
                new UsernameAndPasswordAuthenticationRequest("expert@expert.ir", "abcd1234");
        response = restTemplate.postForEntity(createUrl("/users/login"), expertLoginInfo, ResponseResult.class);
        String expertToken = response.getHeaders().get("Authorization").get(0);

        //add service to expert
        ExpertAddServiceInDto expertAddServiceInDto = new ExpertAddServiceInDto(1L);
        httpHeaders.set("Authorization", expertToken);
        request = new HttpEntity<>(expertAddServiceInDto, httpHeaders);
        response = restTemplate.postForEntity(createUrl("/users/addService"), request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue(String.valueOf(map.get("expertId")).equals("2"));

        //customer login
        UsernameAndPasswordAuthenticationRequest customerLoginInfo =
                new UsernameAndPasswordAuthenticationRequest("customer@customer.ir", "abcd1234");
        response = restTemplate.postForEntity(createUrl("/users/login"), customerLoginInfo, ResponseResult.class);
        String customerToken = response.getHeaders().get("Authorization").get(0);

        //add order
        RequestInDto requestInDto = new RequestInDto(
                1L,
                2000L,
                new Date(2022, 02, 15, 18, 40),
                "details",
                "Mashhad");
        httpHeaders.set("Authorization", customerToken);
        request = new HttpEntity<>(requestInDto, httpHeaders);
        response = restTemplate.postForEntity(createUrl("/orders"), request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue(String.valueOf(map.get("id")).equals("1"));

        //get orders for the expert
        httpHeaders.set("Authorization", expertToken);
        request = new HttpEntity<>(null, httpHeaders);
        response = restTemplate.exchange(createUrl("/orders/experts"), HttpMethod.GET, request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue(String.valueOf(map.get("totalElements")).equals("1"));

        //add suggestion from expert(2) for order(1)
        ExpertAddSuggestionInDto expertAddSuggestionInDto = new ExpertAddSuggestionInDto(
                2000L,
                new Date(2022, 02, 15, 18, 40),
                "details",
                3.5D);
        request = new HttpEntity<>(expertAddSuggestionInDto, httpHeaders);
        response = restTemplate.postForEntity(createUrl("/orders/1/suggestion"), request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue(String.valueOf(map.get("suggestionId")).equals("1"));

        //get added suggestions to customers
        httpHeaders.set("Authorization", customerToken);
        request = new HttpEntity<>(null, httpHeaders);
        response = restTemplate.exchange(createUrl("/orders"), HttpMethod.GET, request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue(String.valueOf(map.get("totalElements")).equals("1"));

        //accept suggestion(1)
        response = restTemplate.postForEntity(createUrl("/orders/suggestions/1/accept"), request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue(String.valueOf(map.get("id")).equals("1"));

        //final accept by expert
        httpHeaders.set("Authorization", expertToken);
        request = new HttpEntity<>(null, httpHeaders);
        response = restTemplate.postForEntity(createUrl("/suggestions/1/accept"), request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue(String.valueOf(map.get("answer")).equals(BaseMessageStatus.BUSY.name()));

        //expert start to work
        response = restTemplate.postForEntity(createUrl("/suggestions/1/start"), request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue(String.valueOf(map.get("id")).equals("1"));

        //expert finish work
        response = restTemplate.postForEntity(createUrl("/suggestions/1/done"), request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue(String.valueOf(map.get("id")).equals("1"));

        //customer increase credit
        UserIncreaseCreditInDto userIncreaseCreditInDto = new UserIncreaseCreditInDto(5000L);
        httpHeaders.set("Authorization", customerToken);
        request = new HttpEntity<>(userIncreaseCreditInDto, httpHeaders);
        response = restTemplate.postForEntity(createUrl("/users/credit"), request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue("5000.0".equals(String.valueOf(map.get("balance"))));

        //customer pay for order
        response = restTemplate.postForEntity(createUrl("/orders/suggestions/1/pay/credit"), request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue("1".equals(String.valueOf(map.get("id"))));

        //check if the amount is subtracted from customer credit and added to expert credit
        response = restTemplate.exchange(createUrl("/users/credit"), HttpMethod.GET, request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue("3000.0".equals(String.valueOf(map.get("credit"))));

        httpHeaders.set("Authorization", expertToken);
        request = new HttpEntity<>(null, httpHeaders);
        response = restTemplate.exchange(createUrl("/users/credit"), HttpMethod.GET, request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue(String.valueOf(Double.valueOf(2000 * 0.7)).equals(String.valueOf(map.get("credit"))));

        //customer comment on the work of the expert
        CommentInDto commentInDto = new CommentInDto(5, "best");
        httpHeaders.set("Authorization", customerToken);
        request = new HttpEntity<>(commentInDto, httpHeaders);
        response = restTemplate.postForEntity(createUrl("/orders/suggestions/1/comment"), request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue("1".equals(String.valueOf(map.get("id"))));

        //show comments of the expert
        httpHeaders.set("Authorization", expertToken);
        request = new HttpEntity<>(null, httpHeaders);
        response = restTemplate.exchange(createUrl("/users/comments"), HttpMethod.GET, request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue("1".equals(String.valueOf(map.get("totalElements"))));

        //show comment(1) of the expert
        response = restTemplate.exchange(createUrl("/users/comments/1"), HttpMethod.GET, request, ResponseResult.class);
        map = (LinkedHashMap<String, String>) response.getBody().getData();
        assertTrue("5".equals(String.valueOf(map.get("points"))));
        //TODO: continue integration test here
    }

    private String createUrl(String path) {
        return "http://localhost:" + serverPort + "/api" + path;
    }
}
