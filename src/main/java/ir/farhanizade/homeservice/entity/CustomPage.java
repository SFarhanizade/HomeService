package ir.farhanizade.homeservice.entity;

import ir.farhanizade.homeservice.dto.out.UserOutDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
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
