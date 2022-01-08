package ir.farhanizade.homeservice.dto.out;

import lombok.Data;

@Data
public class UserSearchOutDto {
    private String type;
    private String firstname;
    private String lastname;
    private String email;
}
