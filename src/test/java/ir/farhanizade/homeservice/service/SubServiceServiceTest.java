package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.exception.DuplicateEntityException;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.exception.NullFieldException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class SubServiceServiceTest {
    @Autowired
    private SubServiceService repository;
    @Autowired
    private MainServiceService parentRepository;

    @TestConfiguration
    @ComponentScan("ir.farhanizade.homeservice")
    public static class SubServiceServiceTestConfig{}

    @Test
    void test_save_duplicate_throwException() throws EntityNotFoundException {
        MainService parent = MainService.builder()
                .name("parent")
                .build();
        try {
            parentRepository.save(parent);
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
        } catch (NullFieldException e) {
            e.printStackTrace();
        }
        SubService s1 = SubService.builder()
                .name("s1")
                .basePrice(new BigDecimal(1))
                .description("")
                .parent(parent)
                .build();

        SubService s2 = SubService.builder()
                .name("s1")
                .basePrice(new BigDecimal(1))
                .description("")
                .parent(parent)
                .build();

        try {
            repository.save(s1, parent.getId());
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
        }
        System.out.println();
        try {
            repository.save(s2, parent.getId());
            fail();
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
            MainService mainService = parentRepository.loadAll().get(0);
            assertEquals(1,mainService.getSubServices().size());
        }

    }


}