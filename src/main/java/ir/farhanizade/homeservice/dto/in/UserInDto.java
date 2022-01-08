package ir.farhanizade.homeservice.dto.in;

import ir.farhanizade.homeservice.entity.user.User;
import lombok.Data;

@Data
public class UserInDto {
    private String type;
    private String firstname;
    private String lastname;
    private String email;
    private String password;

    public User convert() {
        return User.builder()
                .fName(firstname)
                .lName(lastname)
                .email(email)
                .password(password)
                .build();
    }
}
