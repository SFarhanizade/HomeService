package ir.farhanizade.homeservice;

import ir.farhanizade.homeservice.controller.api.ResponseResult;
import ir.farhanizade.homeservice.dto.in.ServiceInDto;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

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

//        ServiceInDto serviceInDto = new ServiceInDto(0L, "mainService", "description", 2000L);
//        response = restTemplate.getForEntity(createUrl("/services"), ResponseResult.class,serviceInDto);
//        map = (LinkedHashMap<String, Integer>) response.getBody().getData();
//        assertEquals(1, map.get("id"));
    }

    private String createUrl(String path) {
        return "http://localhost:" + serverPort + "/api" + path;
    }
}
