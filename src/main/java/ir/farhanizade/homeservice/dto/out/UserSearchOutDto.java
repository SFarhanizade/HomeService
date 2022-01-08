package ir.farhanizade.homeservice.dto.out;

import ir.farhanizade.homeservice.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchOutDto {
    protected Long id;
    protected String type;
    protected String firstname;
    protected String lastname;
    protected String email;

    public UserSearchOutDto convert2Dto(User user){
        return UserSearchOutDto.builder()
                .id(user.getId())
                .firstname(user.getFName())
                .lastname(user.getLName())
                .email(user.getEmail())
                .build();
    }
}
