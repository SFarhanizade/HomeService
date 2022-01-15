package ir.farhanizade.homeservice.bootstrap;

import ir.farhanizade.homeservice.dto.in.*;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.Admin;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {
    private final AdminService adminService;
    private final CustomerService customerService;
    private final ExpertService expertService;
    private final MainServiceService mainService;
    private final SubServiceService subService;
    private final OrderService orderService;

    @Override
    public void run(String... args) throws Exception {
        adminService.save(UserInDto.builder()
                .firstname("Admin")
                .lastname("Admin")
                .email("admin@admin.ir")
                .password("admin1234")
                .build());

        customerService.save(UserInDto.builder()
                .firstname("Customer")
                .lastname("Customer")
                .email("customer@customer.ir")
                .password("customer1234")
                .build());

        expertService.save(UserInDto.builder()
                .firstname("Expert")
                .lastname("Expert")
                .email("expert@expert.ir")
                .password("expert1234")
                .build());

        mainService.save(new ServiceInDto("Main","description",1000L));
        subService.save(new ServiceInDto("service1","description",1000L), 1L);
        subService.save(new ServiceInDto("service2","description",1000L), 1L);

        adminService.acceptExpert(1L,3L);

        ExpertAddServiceInDto expertAddServiceInDto = new ExpertAddServiceInDto();
        expertAddServiceInDto.setExpertId(3L);
        expertAddServiceInDto.setServiceId(1L);
        expertService.addService(expertAddServiceInDto);

        RequestInDto requestInDto = new RequestInDto(
                1L,
                1500L,
                new Date(2022, 01, 21, 18, 25, 43),
                "details",
                "Mashhad");
        customerService.request(2L,requestInDto);

        RequestInDto requestInDto2 = new RequestInDto(
                2L,
                2500L,
                new Date(2022, 01, 21, 18, 25, 43),
                "details",
                "Mashhad");
        customerService.request(2L,requestInDto2);

//        ExpertAddSuggestionInDto expertAddSuggestionInDto = new ExpertAddSuggestionInDto();
//        expertAddSuggestionInDto.setOrderId(1L);
//        expertAddSuggestionInDto.setPrice(1300L);
//        expertAddSuggestionInDto.setDetails("details");
//        expertAddSuggestionInDto.setDuration(2.5);
//        expertAddSuggestionInDto.setDateTime(new Date(2022, 01, 21, 18, 25, 43));
//        expertService.suggest(3L,expertAddSuggestionInDto);
//
//        customerService.acceptSuggestion(2L,1L);
//
//        expertService.answerSuggestion(3L,1L, BaseMessageStatus.BUSY);
//
//        expertService.startToWork(3L,1L);
//
//        expertService.finishWork(3L,1L);

        //customerService.pay(2L,1L);
    }
}
