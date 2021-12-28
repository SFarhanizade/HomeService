package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.exception.DuplicateEntityException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MainServiceServiceTest {
    @Autowired
    private MainServiceService repository;

    @TestConfiguration
    @ComponentScan("ir.farhanizade.homeservice")
    public static class MainServiceServiceTestConfig{}

    @Test
    void save(){
        MainService service1 = MainService.builder()
                .name("1")
                .build();
        MainService service2 = MainService.builder()
                .name("1")
                .build();
        try {
            repository.save(service1);
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
        }
        try {
            repository.save(service2);
            fail();
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
            List<MainService> all = repository.loadAll();
            assertEquals(1,all.size());
        }
    }
}