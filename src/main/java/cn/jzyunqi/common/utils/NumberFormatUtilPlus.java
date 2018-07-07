package cn.jzyunqi.common.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class NumberFormatUtilPlus {

    public static final String SYSTEM_MONEY_FORMAT = "#,##0.00#";
    public static final String SYSTEM_INTEGER_MONEY_FORMAT = "#,##0";

    private static final String OTHER_ONE = "\u4e59";
    private static final String OTHER_TWO = "\u5169";
    private static final String TEN_LC = "\u5341";
    private static final String TEN_UC = "\u62fe";
    private static final String[] NUM_CN_LC = {"\u25cb", "\u4e00", "\u4e8c", "\u4e09", "\u56db", "\u4e94", "\u516d", "\u4e03", "\u516b", "\u4e5d", TEN_LC};
    private static final String[] DIGIT_CN_L_LC = {"", TEN_LC, "\u767e", "\u5343"};
    private static final String[] DIGIT_CN_H_LC = {"", "\u4e07", "\u4ebf"};

    private static final String[] NUM_CN_UC = {"\u96f6", "\u58f9", "\u8cb3", "\u53c1", "\u8086", "\u4f0d", "\u9678", "\u67d2", "\u634c", "\u7396", TEN_UC};
    private static final String[] DIGIT_CN_L_UC = {"", TEN_UC, "\u4f70", "\u4edf"};
    private static final String[] DIGIT_CN_H_UC = {"", "\u842c", "\u5104"};
    private static final String[] DIGIT_CN_M_UC = {"\u89d2", "\u5206"};
    private static final String CN_YUAN = "\u5713";

    private static final char C_ZERO = '0';
    private static final int CN_NUM_GRP_LEN = 4;
    private static final int CN_NUM_GRP_COUNT = 3;

    private NumberFormatUtilPlus() {

    }

    /**
     * 将指定年份转换为中文年份
     *
     * @param num 数字年
     * @return 中文年
     */
    public static String formatCnYear(String num) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num.length(); i = i + 1) {
            sb.append(NUM_CN_LC[num.charAt(i) - C_ZERO]);
        }
        return sb.toString();
    }

    /**
     * 将金额转换为中文大写金额
     *
     * @param num 金额
     * @return 中文大写金额
     */
    public static String formatCnMoney(BigDecimal num) {
        if (num == null) {
            return StringUtilPlus.EMPTY;
        }
        String[] numArray = StringUtilPlus.split(StringUtilPlus.defaultString(num), "\\.");
        StringBuilder money = new StringBuilder(formatCnWithUnit(numArray[0], false, true));
        money.append(CN_YUAN);
        if (numArray.length > 1) {
            String decim = numArray[1];
            for (int i = 0; i < DIGIT_CN_M_UC.length && i < decim.length(); i = i + 1) {
                int d = decim.charAt(i) - C_ZERO;
                money.append(NUM_CN_UC[d]);
                money.append(DIGIT_CN_M_UC[i]);
            }
        }
        return money.toString();
    }

    /**
     * 将数字转换为中文
     *
     * @param numS 数字
     * @return 中文数字
     */
    public static String formatCnNum(String numS) {
        return formatCnWithUnit(numS, false, false);
    }

    /**
     * 将数字转换为中文
     *
     * @param num 数字
     * @return 中文数字
     */
    public static String formatCnNum(Integer num) {
        String numS = StringUtilPlus.defaultString(num);
        return formatCnWithUnit(numS, false, false);
    }

    /**
     * 将数字转换为中文
     *
     * @param num 数字
     * @return 中文数字
     */
    public static String formatCnCount(Integer num) {
        String numS = StringUtilPlus.defaultString(num);
        return formatCnWithUnit(numS, true, false);
    }

    private static String formatCnWithUnit(String numS, boolean other, boolean money) {
        String[] numCn = NUM_CN_LC;
        String[] digitCnLow = DIGIT_CN_L_LC;
        String[] digitCnHigh = DIGIT_CN_H_LC;

        if (money) {
            numCn = NUM_CN_UC;
            digitCnLow = DIGIT_CN_L_UC;
            digitCnHigh = DIGIT_CN_H_UC;
        }

        int remaind = numS.length() % CN_NUM_GRP_LEN;
        int padLength = 0;
        if (remaind > 0) {
            padLength = numS.length() + CN_NUM_GRP_LEN - remaind;
        }
        String numSlpad = new StringBuilder(StringUtilPlus.leftPad(numS, padLength, C_ZERO)).reverse().toString();

        Pattern p = Pattern.compile("\\d{4}");
        Matcher m = p.matcher(numSlpad);

        List<String> numList = new ArrayList<String>();
        while (m.find()) {
            numList.add(m.group());
        }
        if (numList.size() > digitCnHigh.length) {
            return "not support";
        }
        StringBuilder sb = new StringBuilder();
        boolean lastZero = false;
        for (int grountCount = numList.size() - 1; grountCount >= 0; grountCount = grountCount - 1) {
            String group = numList.get(grountCount);
            if (Integer.parseInt(group) == 0) {
                if (sb.length() == 0 || !sb.substring(sb.length() - 1).equals(numCn[0])) {
                    sb.append(numCn[0]);
                }
                lastZero = true;
            } else {
                for (int i = CN_NUM_GRP_COUNT; i >= 0; i = i - 1) {
                    int d = group.charAt(i) - C_ZERO;
                    if (!lastZero || d != 0) {
                        sb.append(numCn[d]);
                    }
                    if (d != 0) {
                        sb.append(digitCnLow[i]);
                        lastZero = false;
                    } else {
                        lastZero = true;
                    }
                }
                if (numList.size() == 1) {
                    removeFstZero(sb, numCn);
                    removeLstZero(sb, numCn);
                    removeFstOne(sb, numCn, digitCnLow);
                } else if (grountCount == numList.size() - 1) {
                    removeFstZero(sb, numCn);
                    removeFstOne(sb, numCn, digitCnLow);
                } else if (grountCount == 0) {
                    removeLstZero(sb, numCn);
                }
                if (sb.length() > 1 && sb.substring(sb.length() - 1).equals(numCn[0])) {
                    sb.insert(sb.length() - 1, digitCnHigh[grountCount]);
                } else {
                    sb.append(digitCnHigh[grountCount]);
                }
            }
        }
        if (numList.size() > 1) {
            removeLstZero(sb, numCn);
        } else if (sb.length() == 1 && other) {
            if (sb.toString().equals(numCn[1])) {
                return OTHER_ONE;
            } else if (sb.toString().equals(numCn[2])) {
                return OTHER_TWO;
            }
        }
        return sb.toString();
    }

    private static void removeFstOne(StringBuilder sb, String[] numCn, String[] digitCnLow) {
        if (sb.length() > 1 && sb.substring(0, 2).equals(numCn[1] + digitCnLow[1])) {
            sb.deleteCharAt(0);
        }
    }

    private static void removeLstZero(StringBuilder sb, String[] numCn) {
        if (sb.length() > 1 && sb.substring(sb.length() - 1).equals(numCn[0])) {
            sb.deleteCharAt(sb.length() - 1);
            removeLstZero(sb, numCn);
        }
    }

    private static void removeFstZero(StringBuilder sb, String[] numCn) {
        if (sb.length() > 1 && sb.substring(0, 1).equals(numCn[0])) {
            sb.deleteCharAt(0);
            removeFstZero(sb, numCn);
        }
    }

    /**
     * 计算百分比
     *
     * @param divisor  除数
     * @param dividend 被除数
     * @return 百分比
     */
    public static String calcPercentage(double divisor, double dividend) {
        if (divisor == 0) {
            return "0.00%";
        }
        if (dividend == 0) {
            return "0.00%";
        }
        double divideNum = divisor / dividend;
        //使用百分比格式化工具
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        //最少保留2位小数点
        percentFormat.setMinimumFractionDigits(2);
        String percentage = percentFormat.format(divideNum);
        return percentage;
    }
}
