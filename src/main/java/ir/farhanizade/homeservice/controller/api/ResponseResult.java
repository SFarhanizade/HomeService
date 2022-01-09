package ir.farhanizade.homeservice.controller.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResult<T> {
    private int code;
    private T data;
    private String message;

    public ResponseResult fail(Exception e) {
        code = -1;
        data = null;
        message=e.getMessage();
        return this;
    }
}
