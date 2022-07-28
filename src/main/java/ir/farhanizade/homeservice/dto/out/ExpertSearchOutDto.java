package ir.farhanizade.homeservice.dto.out;

import ir.farhanizade.homeservice.entity.service.MyService;
import ir.farhanizade.homeservice.entity.user.UserExpert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.Set;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ExpertSearchOutDto extends UserSearchOutDto{
    private Set<MyService> expertises;
    private int points;

    public ExpertSearchOutDto convert2Dto(UserExpert expert){
        return ExpertSearchOutDto.builder()
                .id(expert.getId())
                .firstname(expert.getFName())
                .lastname(expert.getLName())
                .email(expert.getEmail())
                .expertises(expert.getExpertises())
                .points(expert.getPoints())
                .build();
    }

}
