package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.ServiceInDto;
import ir.farhanizade.homeservice.dto.out.MainServiceOutDto;
import ir.farhanizade.homeservice.exception.DuplicateEntityException;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SubServiceServiceTest {
    @Autowired
    private ServiceService repository;
    @Autowired
    private ServiceService parentRepository;

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
            s1.setParent(1L);
            repository.save(s1);
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
        }
        try {
            s2.setParent(parent.getParent());
            repository.save(s2);
            fail();
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
            MainServiceOutDto mainService = parentRepository.loadAllMain(Pageable.ofSize(10)).getData().get(0);
            assertEquals(1, mainService.getSubServices().size());
        }

    }


}