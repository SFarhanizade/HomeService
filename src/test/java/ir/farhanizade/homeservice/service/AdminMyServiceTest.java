package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.UserSearchOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.user.UserAdmin;
import ir.farhanizade.homeservice.repository.user.AdminRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.notNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AdminMyServiceTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminRepository adminRepository;

    @MockBean
    private ExpertService expertService;

    @MockBean
    private UserService userService;

    private UserInDto getValidUserDto() {
        return UserInDto.builder()
                .type("admin")
                .firstname("User")
                .lastname("User")
                .email("User@User.ir")
                .password("abcd1234")
                .build();
    }

    private UserAdmin getValidUser() {
        return getValidUserDto().convert2Admin();
    }

    @Test
    void test_save_is_ok() {
        UserInDto user = getValidUserDto();
        try {
            assertEquals(new EntityOutDto(2L), adminService.save(user));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test_accept_expert_is_ok() {
        adminRepository.save(getValidUser());
        try {
            Mockito.when(expertService.acceptExpert(1L))
                    .thenReturn(new EntityOutDto(1L));
            assertEquals(new EntityOutDto(1L),
                    adminService.acceptExpert(1L));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void test_search_is_ok() {
        UserSearchInDto customerSearchInDto = UserSearchInDto.builder()
                .type("customer")
                .build();
        UserSearchInDto expertSearchInDto = UserSearchInDto.builder()
                .type("expert")
                .build();

        CustomPage<UserSearchOutDto> result = CustomPage.<UserSearchOutDto>builder()
                .data(List.of(new UserSearchOutDto(),
                        new UserSearchOutDto()))
                .build();
        try {
            Mockito.when(userService.search(notNull(), notNull()))
                    .thenReturn(result);

            assertEquals(2,
                    adminService.search(customerSearchInDto, Pageable.ofSize(10)).getData().size());
            assertEquals(2,
                    adminService.search(expertSearchInDto, Pageable.ofSize(10)).getData().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}