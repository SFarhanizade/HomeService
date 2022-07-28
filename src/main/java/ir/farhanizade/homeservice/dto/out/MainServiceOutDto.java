package ir.farhanizade.homeservice.dto.out;

import ir.farhanizade.homeservice.entity.service.MyService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MainServiceOutDto {

    public MainServiceOutDto(MyService service) {
        MyService parent = service.getParent();
        parentId = parent.getId();
        parentName = parent.getName();
        List<MyService> subMyServices = service.getSubMyServices();

        subServices = subMyServices.stream()
                .map(s -> new ServiceOutDto(s.getId(), s.getName(), null)).toList();
    }

    private Long id;
    private String name;
    private Long parentId;
    private String parentName;
    private List<ServiceOutDto> subServices;
}
