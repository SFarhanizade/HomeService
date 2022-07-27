package ir.farhanizade.homeservice.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentInDto {
//    @NonNull
//    @Size(min = 1, max = 5)
    private Integer points;
    private String description;
}
