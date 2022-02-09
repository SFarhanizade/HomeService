package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.ServiceInDto;
import ir.farhanizade.homeservice.dto.out.MainServiceOutDto;
import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.exception.DuplicateEntityException;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.exception.NullFieldException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SubServiceServiceTest {
    @Autowired
    private SubServiceService repository;
    @Autowired
    private MainServiceService parentRepository;

    private ServiceInDto getMainService() {
        return new ServiceInDto(
                1L,
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
    void test_save_duplicate_throwException() throws EntityNotFoundException {
        ServiceInDto parent = getMainService();
        try {
            parentRepository.save(parent);
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
        }
        ServiceInDto s1 = getSubService();
        ServiceInDto s2 = getSubService();

        try {
            repository.save(s1,1L);
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
        }
        try {
            repository.save(s2, parent.getParent());
            fail();
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
            MainServiceOutDto mainService = parentRepository.loadAll(Pageable.ofSize(10)).getData().get(0);
            assertEquals(1, mainService.getSubServices().size());
        }

    }


}