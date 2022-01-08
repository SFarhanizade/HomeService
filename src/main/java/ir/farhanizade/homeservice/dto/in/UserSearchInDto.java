package ir.farhanizade.homeservice.dto.in;

import lombok.Data;

import java.util.List;

@Data
public class UserSearchInDto {
    protected String type;
    protected String firstname;
    protected String lastname;
    protected String email;
    private List<Long> expertises;
    private int points;
}
