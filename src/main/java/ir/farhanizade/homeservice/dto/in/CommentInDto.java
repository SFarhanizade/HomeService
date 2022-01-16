package ir.farhanizade.homeservice.dto.in;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Size;

@Data
public class CommentInDto {
    @NonNull
    @Size(min = 1, max = 5)
    private Integer points;
    private String description;
}
