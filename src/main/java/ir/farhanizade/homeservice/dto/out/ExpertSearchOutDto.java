package ir.farhanizade.homeservice.dto.out;

import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.Expert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ExpertSearchOutDto extends UserSearchOutDto{
    private List<SubService> expertises;
    private int points;

    public ExpertSearchOutDto convert2Dto(Expert expert){
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
