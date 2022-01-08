package ir.farhanizade.homeservice.dto.in;

import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.User;
import lombok.Data;

@Data
public class UserInDto {
    private String type;
    private String firstname;
    private String lastname;
    private String email;
    private String password;

    public Expert convert2Expert() {
        return Expert.builder()
                .fName(firstname)
                .lName(lastname)
                .email(email)
                .password(password)
                .build();
    }

    public Customer convert2Customer() {
        return Customer.builder()
                .fName(firstname)
                .lName(lastname)
                .email(email)
                .password(password)
                .build();
    }
}
