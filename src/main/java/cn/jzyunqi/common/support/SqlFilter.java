package cn.jzyunqi.common.support;

import cn.jzyunqi.common.utils.StringUtilPlus;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class SqlFilter {

    public static final char DEFALT_ESCAPE_CHAR = '/';

    private static final String DB_LIKE = "%";
    private static final char[] ESCAPE_CHAR = {'%', '_', '/'};
    private static final String[] ESCAPE = {DB_LIKE, "_", "/"};
    private static final String[] REPLACEMENT = {"/%", "/_", "//"};

    /**
     * 过滤数据库模糊匹配的数据
     *
     * @param param 需要匹配的参数
     * @return 过滤后的参数
     */
    public static String filterForLike(String param) {
        if (StringUtilPlus.containsAny(param, ESCAPE_CHAR)) {
            return DB_LIKE + StringUtilPlus.replaceEach(param, ESCAPE, REPLACEMENT) + DB_LIKE;
        }
        return DB_LIKE + param + DB_LIKE;
    }

    /**
     * 将逗号分隔的数据转换为正则表达式
     *
     * @param docSplitStr 需要转换的参数
     * @return 转换后的正则表达式
     */
    public static String filterForMatches(String docSplitStr) {
        return StringUtilPlus.L_PT + StringUtilPlus.COMMA + docSplitStr.replaceAll(StringUtilPlus.COMMA, ",)|(,") + StringUtilPlus.COMMA + StringUtilPlus.R_PT;
    }
}
