package ir.farhanizade.homeservice.bootstrap;

import ir.farhanizade.homeservice.dto.in.*;
import ir.farhanizade.homeservice.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {
    private final AdminService adminService;

    @Override
    public void run(String... args) throws Exception {
        adminService.save(UserInDto.builder()
                .type("admin")
                .firstname("Admin")
                .lastname("Admin")
                .email("admin@admin.ir")
                .password("admin1234")
                .build());

        //TODO: write bootstrap and tests
    }
}
