package ir.farhanizade.homeservice.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPasswordInDto {
    private Long id;
    private String currentPassword;
    private String newPassword;
}
