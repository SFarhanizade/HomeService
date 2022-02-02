package ir.farhanizade.homeservice.dto.in;

import ir.farhanizade.homeservice.entity.user.UserAdmin;
import ir.farhanizade.homeservice.entity.user.UserCustomer;
import ir.farhanizade.homeservice.entity.user.UserExpert;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Size;

@Data
@Builder
public class UserInDto {
    @NonNull
    private String type;

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

    public UserExpert convert2Expert() {
        return UserExpert.builder()
                .fName(firstname)
                .lName(lastname)
                .email(email)
                .password(password)
                .build();
    }

    public UserCustomer convert2Customer() {
        return UserCustomer.builder()
                .fName(firstname)
                .lName(lastname)
                .email(email)
                .password(password)
                .build();
    }

    public UserAdmin convert2Admin() {
        return UserAdmin.builder()
                .fName(firstname)
                .lName(lastname)
                .email(email)
                .password(password)
                .build();
    }
}
