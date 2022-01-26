package ir.farhanizade.homeservice.dto.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchInDto {
    protected String type;
    protected String firstname;
    protected String lastname;
    protected String email;
    private List<Long> expertises;
    private Integer points;
}
