package ir.farhanizade.homeservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomPage<T> {
    private Integer pageNumber;
    private List<T> data;
    private Integer lastPage;
    private Integer pageSize;
    private Long totalElements;

    public CustomPage<T> convert(Page page) {
        pageSize = page.getSize();
        totalElements = page.getTotalElements();
        lastPage = page.getTotalPages();
        pageNumber = page.getNumber();
        return this;
    }
}
