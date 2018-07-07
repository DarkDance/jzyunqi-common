package cn.jzyunqi.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public final class StringUtilPlus extends StringUtils {

    public static final String PERCENT = "%";
    public static final String SLASH = "/";//back slash\
    public static final String SPACE_SLASH = " / ";
    public static final String DOT = ".";
    public static final String HYPHEN = "-";//dash
    public static final String SPACE_HYPHEN = " - ";
    public static final String UNDER_SCORE = "_";//under line
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String L_PT = "(";//parenthesis
    public static final String R_PT = ")";
    public static final String ENTER = "\n";
    public static final String REGEX_LAST_COMMA = ",$";

    private StringUtilPlus() {

    }


    public static String defaultString(final Number num) {
        return num == null ? EMPTY : defaultString(num.toString());
    }

    public static String leftPad(final Number num, final int size, final char padChar) {
        return leftPad(defaultString(num), size, padChar);
    }

    public static String splitGetFirst(String input) {
        return split(input, ",")[0];
    }

    /**
     * 如果找到非法字符则返回true,如果没找到则返回false
     *
     * @param value 字符串
     * @return 是否含有非法字符
     */
    public static boolean stripXSS(String value) {
        boolean result = false;
        if (value != null) {
            value = value.replaceAll(" ", "");
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            result = scriptPattern.matcher(value).find();
            //如果找到则为true
            if (result) {
                return true;
            }

            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            result = scriptPattern.matcher(value).find();
            if (result) {
                return true;
            }

            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            result = scriptPattern.matcher(value).find();
            if (result) {
                return true;
            }

            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            result = scriptPattern.matcher(value).find();
            if (result) {
                return true;
            }

            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            result = scriptPattern.matcher(value).find();
            if (result) {
                return true;
            }

            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            result = scriptPattern.matcher(value).find();
            if (result) {
                return true;
            }

            scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            result = scriptPattern.matcher(value).find();
            if (result) {
                return true;
            }

            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            result = scriptPattern.matcher(value).find();
            if (result) {
                return true;
            }

            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            result = scriptPattern.matcher(value).find();
            if (result) {
                return true;
            }

            scriptPattern = Pattern.compile("alert", Pattern.CASE_INSENSITIVE);
            result = scriptPattern.matcher(value).find();
            if (result) {
                return true;
            }
        }
        return result;
    }

    public static CharSequence replaceEmoji(CharSequence input) {
        //emoji替换
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher emojiMatcher = emoji.matcher(input);
        if (emojiMatcher.find()) {
            return emojiMatcher.replaceAll("*");
        } else {
            return input;
        }
    }

    /**
     * 将字符串中的特殊字符替换为replaceChar
     *
     * @param input       待处理的字符串
     * @param replaceChar 替换成的字符
     * @return 处理完成后的字符串
     */
    public static CharSequence replaceCharacters(CharSequence input, String replaceChar) {
        //特殊字符替换
        Pattern characters = Pattern.compile("[·`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】'；：”“’。，、？]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher charactersMatcher = characters.matcher(input);
        if (charactersMatcher.find()) {
            return charactersMatcher.replaceAll(replaceChar);
        } else {
            return input;
        }
    }

    /**
     * 星号替换身份证号
     *
     * @param idCard 身份证号
     * @return 结果
     */
    public static String idCardReplaceWithStar(String idCard) {

        if (idCard == null || idCard.isEmpty()) {
            return null;
        } else {
            return idCard.replaceAll("(?<=\\d{3})\\d(?=\\d{2})", "*");
        }
    }


    /**
     * 星号替换中文姓名
     *
     * @param userName 姓名
     * @return 替换结果
     */
    public static String payPwdNameReplaceWithStar(String userName) {
        if (userName == null) {
            userName = "";
        }
        StringBuilder builder = new StringBuilder();
        int nameLength = userName.length();
        if (nameLength == 1) {
            return userName;
        } else if (nameLength == 2) {
            builder.append(userName.substring(0, 1)).append("*");
        } else if (nameLength >= 3) {
            builder.append(userName.substring(0, 1));
            for (int i = 0; i < nameLength - 2; i++) {
                builder.append("*");
            }
            builder.append(userName.substring(nameLength - 1, nameLength));
        }
        return builder.toString();

    }

    /**
     * 版本号比较
     *
     * @param firstVersion 输入版本号
     * @param secondVersion 最新版本号
     * @return 0 一样 | > 0 前者大  | < 0 后者大
     */
    public static int compareVersion(String firstVersion, String secondVersion){
        String[] fva = StringUtilPlus.split(firstVersion, ".");
        String[] sva = StringUtilPlus.split(secondVersion, ".");

        if(fva.length == 0 || sva.length == 0){
            return 0;
        }
        int maxLength = fva.length > sva.length ? fva.length : sva.length;

        int[] fv = new int[maxLength];
        for (int i = 0; i< maxLength; i++){
            if(i < fva.length){
                fv[i] = Integer.parseInt(fva[i]);
            }else{
                fv[i] = 0;
            }
        }
        int[] sv = new int[maxLength];
        for (int i = 0; i< maxLength; i++){
            if(i < sva.length){
                sv[i] = Integer.parseInt(sva[i]);
            }else{
                sv[i] = 0;
            }
        }

        int result = 0;
        for(int i = 0;i < maxLength;i++){
            result = fv[i] - sv[i];
            if(result != 0){
                break;
            }
        }
        return result;
    }
}
