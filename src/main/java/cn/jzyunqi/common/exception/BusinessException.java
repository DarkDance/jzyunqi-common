package cn.jzyunqi.common.exception;

import cn.jzyunqi.common.utils.StringUtilPlus;

import java.util.Arrays;

/**
 * @author wiiyaya
 * @Date 2018/5/3
 */
public class BusinessException extends Exception {

    private static final long serialVersionUID = 2485329619448041725L;

    private final Object[] arguments;

    private final String code;

    public BusinessException(String code, Object... arguments) {
        super(StringUtilPlus.join(code, "[", StringUtilPlus.join(arguments, "|"), "]"));
        this.arguments = arguments;
        this.code = code;
    }

    public Object[] getArguments() {
        return Arrays.copyOf(arguments, arguments.length);
    }

    public String getCode() {
        return code;
    }
}
