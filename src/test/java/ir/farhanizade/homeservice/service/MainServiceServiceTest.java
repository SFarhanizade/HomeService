package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.ServiceInDto;
import ir.farhanizade.homeservice.dto.out.MainServiceOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.exception.DuplicateEntityException;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.exception.NullFieldException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MainServiceServiceTest {
    @Autowired
    private MainServiceService mainServiceService;

    private ServiceInDto getMainService() {
        return new ServiceInDto(
                0L,
                "subService",
                "description",
                1500L);
    }

    private ServiceInDto getSubService() {
        ServiceInDto result = getMainService();
        result.setParent(1L);
        return result;
    }

    @Test
    void save() {
        ServiceInDto service1 = getMainService();
        ServiceInDto service2 = getMainService();
        try {
            mainServiceService.save(service1);
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
        }
        try {
            mainServiceService.save(service2);
            fail();
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
            CustomPage<MainServiceOutDto> all = null;
            try {
                all = mainServiceService.loadAll(Pageable.ofSize(10));
            } catch (EntityNotFoundException ex) {
                ex.printStackTrace();
            }
            assertEquals(1, all.getTotalElements());
        }
    }
}