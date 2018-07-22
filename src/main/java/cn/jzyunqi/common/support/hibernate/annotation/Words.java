package cn.jzyunqi.common.support.hibernate.annotation;

import cn.jzyunqi.common.utils.StringUtilPlus;
import cn.jzyunqi.common.utils.WordUtilPlus;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author wiiyaya
 * @date 2018/7/10.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {Words.WordsValidator.class})
public @interface Words {

    String message() default "{javax.validation.constraint.Words.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String fileName() default "sensitiveWords.txt";

    class WordsValidator implements ConstraintValidator<Words, String> {

        private static Map sensitiveWordMap;

        @Override
        public void initialize(Words constraintAnnotation) {
            String fileName = constraintAnnotation.fileName();
            try {
                List<String> words = FileUtils.readLines(ResourceUtils.getFile(StringUtilPlus.join("classpath:", fileName)), "UTF-8");
                sensitiveWordMap = WordUtilPlus.getSensitiveWordMap(new HashSet<>(words));
            } catch (IOException e) {
                sensitiveWordMap = new HashMap();
            }
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
            if (StringUtilPlus.isBlank(value)){
                return Boolean.TRUE;
            }
            return !WordUtilPlus.hasSensitiveWord(sensitiveWordMap, value, WordUtilPlus.MatchType.SHOT);
        }
    }
}
