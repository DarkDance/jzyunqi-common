package cn.jzyunqi.common.exception;


import java.util.List;
import java.util.Map;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class ValidateException extends RuntimeException {

    private static final long serialVersionUID = 7450363518285869297L;

    private final List<String> codeList;

    private final Map<String, Object[]> argumentsMap;

    public ValidateException(List<String> codeList, Map<String, Object[]> argumentsMap) {
        this.codeList = codeList;
        this.argumentsMap = argumentsMap;
    }

    public List<String> getCodeList() {
        return codeList;
    }

    public Map<String, Object[]> getArgumentsMap() {
        return argumentsMap;
    }

}
