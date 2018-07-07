package cn.jzyunqi.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wiiyaya
 * @date 2018/1/30.
 */
@Getter
@Setter
public class RestResultDto<T> {

    private int status;

    private T data;

    private String errorPath;

    private String errorCode;

    private String errorMessage;

    @JsonIgnore
    private String realErrorMessage;

    private RestResultDto() {
        this.status = 200;
    }

    private RestResultDto(T t) {
        this.status = 200;
        this.data = t;
    }

    private RestResultDto(int status, String errorCode, String errorMessage, String realErrorMessage) {
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.realErrorMessage = realErrorMessage;
    }

    private RestResultDto(int status, String errorPath, String errorCode, String errorMessage, String realErrorMessage) {
        this.status = status;
        this.errorPath = errorPath;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.realErrorMessage = realErrorMessage;
    }

    public static <T> RestResultDto success() {
        return new RestResultDto<>();
    }

    public static <T> RestResultDto success(T t) {
        return new RestResultDto<>(t);
    }

    public static <T> RestResultDto failed(int status, String errorPath, String errorCode, String errorMessage, String realErrorMessage) {
        return new RestResultDto<>(status, errorPath, errorCode, errorMessage, realErrorMessage);
    }

    public static <T> RestResultDto failed(int status, String errorCode, String errorMessage, String realErrorMessage) {
        return new RestResultDto<>(status, errorCode, errorMessage, realErrorMessage);
    }
}
