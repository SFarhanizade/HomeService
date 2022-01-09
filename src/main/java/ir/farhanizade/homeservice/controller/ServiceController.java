package ir.farhanizade.homeservice.controller;

import ir.farhanizade.homeservice.service.MainServiceService;
import ir.farhanizade.homeservice.service.SubServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final MainServiceService mainService;
    private final SubServiceService subService;

}
