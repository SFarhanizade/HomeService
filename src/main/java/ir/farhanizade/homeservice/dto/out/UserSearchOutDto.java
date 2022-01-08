package ir.farhanizade.homeservice.dto.out;

import lombok.Data;

@Data
public class UserSearchOutDto {
    protected String type;
    protected String firstname;
    protected String lastname;
    protected String email;
}
