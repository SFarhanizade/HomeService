package ir.farhanizade.homeservice.controller.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseResult<T> {
    private int code;
    private T data;
    private String message;
}
