package ir.farhanizade.homeservice.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CustomPage<T> {
    private Integer pageNumber;
    private List<T> data;
    private Integer lastPage;
    private Integer pageSize;
}
