package ir.farhanizade.homeservice.dto.in;

import ir.farhanizade.homeservice.entity.user.Admin;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.User;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserInDto {
    @NonNull
    @Size(min = 3, max = 20)
    private String firstname;
    @NonNull
    @Size(min = 3, max = 20)
    private String lastname;
    @NonNull
    private String email;
    @NonNull
    @Size(min = 8, max = 20)
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

    public Admin convert2Admin(){
        return Admin.builder()
                .fName(firstname)
                .lName(lastname)
                .email(email)
                .password(password)
                .build();
    }
}
