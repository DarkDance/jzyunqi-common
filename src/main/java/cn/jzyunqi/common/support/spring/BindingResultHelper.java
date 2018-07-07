package cn.jzyunqi.common.support.spring;

import cn.jzyunqi.common.exception.ValidateException;
import cn.jzyunqi.common.model.ValidatorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class BindingResultHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BindingResultHelper.class);

    public static void checkAndThrowErrors(BindingResult bindingResult, ValidatorDto validatorDto, Class checkType) {
        if (bindingResult != null && bindingResult.hasErrors()) {
            List<String> codeList = new ArrayList<>();
            Map<String, Object[]> argumentsMap = new HashMap<>();

            String code;
            for (ObjectError oe : bindingResult.getGlobalErrors()) {
                code = oe.getCodes() != null && oe.getCodes().length > 0 ? oe.getCodes()[0] : null;
                if (code != null) {
                    codeList.add(code);
                    argumentsMap.put(code, oe.getArguments());
                }
                LOGGER.error("*****校验对象【{}】全局错误：{}", oe.getObjectName(), oe.getCodes()[0]);
            }
            for (FieldError fe : bindingResult.getFieldErrors()) {
                code = fe.getCodes() != null && fe.getCodes().length > 0 ? fe.getCodes()[0] : null;
                if (code != null) {
                    codeList.add(code);
                    argumentsMap.put(code, fe.getArguments());
                }
                LOGGER.error("*****校验对象【{}】的属性【{}】出现错误：{}，输入值为【{}】", fe.getObjectName(), fe.getField(), fe.getCodes()[0], fe.getRejectedValue());
            }
            throw new ValidateException(codeList, argumentsMap);
        }

        validatorDto.checkAndThrowErrors(checkType);
    }
}
