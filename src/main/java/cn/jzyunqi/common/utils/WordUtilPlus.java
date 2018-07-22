package cn.jzyunqi.common.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author wiiyaya
 * @date 2018/7/10.
 */
public class WordUtilPlus {

    public enum MatchType{
        /**
         * 最短匹配
         */
        SHOT,

        /**
         * 全匹配
         */
        LONG
    }

    /**
     * 是否包含敏感词
     */
    public static boolean hasSensitiveWord(Map sensitiveWordMap, String content, MatchType matchType) {
        boolean flag = false;
        for (int i = 0; i < content.length(); i++) {
            int matchFlag = WordUtilPlus.checkSensitiveWord(sensitiveWordMap, content, i, matchType);
            if (matchFlag > 0) {
                flag = true;
            }
        }
        return flag;
    }


    /**
     * 替换敏感字字符
     */
    public static String replaceSensitiveWord(Map sensitiveWordMap, String txt, MatchType matchType, String replaceChar) {
        String resultTxt = txt;
        Set<String> set = getSensitiveWord(sensitiveWordMap, txt, matchType); //获取所有的敏感词
        Iterator<String> iterator = set.iterator();
        String word = null;
        String replaceString = null;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = getReplaceChars(replaceChar, word.length());
            resultTxt = resultTxt.replaceAll(word, replaceString);
        }
        return resultTxt;


    }

    /**
     * 将数组转成敏感词Map
     *
     * @param sensitiveWordSet 数组
     * @return 敏感词Map
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map getSensitiveWordMap(Set<String> sensitiveWordSet) {
        Map sensitiveWordMap = new HashMap(sensitiveWordSet.size());
        String key;
        Map nowMap;
        Map<String, String> newWorMap;
        for (String aWordSet : sensitiveWordSet) {
            key = aWordSet;
            nowMap = sensitiveWordMap;
            for (int i = 0; i < key.length(); i++) {
                char charKey = key.charAt(i);
                Object wordMap = nowMap.get(charKey);
                if (wordMap != null) {
                    nowMap = (Map) wordMap;
                } else {
                    newWorMap = new HashMap<>();
                    newWorMap.put("isEnd", "0");
                    nowMap.put(charKey, newWorMap);
                    nowMap = newWorMap;
                }
                if (i == key.length() - 1) {
                    nowMap.put("isEnd", "1");
                }
            }
        }
        return sensitiveWordMap;
    }

    /**
     * 检查文字中敏感词的长度
     *
     * @param sensitiveWordMap 敏感词map
     * @param waitCheck 待检查内容
     * @param beginIndex 开始索引
     * @param matchType 匹配规则
     * @return 如果存在，则返回敏感词字符的长度，不存在返回0
     */
    @SuppressWarnings("rawtypes")
    private static int checkSensitiveWord(Map sensitiveWordMap, String waitCheck, int beginIndex, MatchType matchType) {
        Map nowMap = sensitiveWordMap;
        boolean flag = false; //敏感词结束标识位：用于敏感词只有1位的情况
        char word;
        int matchFlag = 0; //匹配标识数默认为0
        for (int i = beginIndex; i < waitCheck.length(); i++) {
            word = waitCheck.charAt(i);
            nowMap = (Map) nowMap.get(word); //获取指定key
            if (nowMap == null) {
                break;//不存在，直接返回
            }
            //输入的字(排列组合的匹配)出现在敏感词库中，判断是否为最后一个
            matchFlag++; //找到相应key，匹配标识+1
            if ("1".equals(nowMap.get("isEnd"))) { //如果为最后一个匹配规则,结束循环，返回匹配标识数
                flag = true; //结束标志位为true
                if (MatchType.SHOT == matchType) {
                    break;//最小规则，直接返回,最大规则还需继续查找
                }
            }
        }
        if (matchFlag < 2 || !flag) { //长度必须大于等于1，为词
            matchFlag = 0;
        }
        return matchFlag;
    }

    /**
     * 获取文字中的敏感词
     */
    private static Set<String> getSensitiveWord(Map sensitiveWordMap, String txt, MatchType matchType) {
        Set<String> sensitiveWordList = new HashSet<>();
        for (int i = 0; i < txt.length(); i++) {
            int length = WordUtilPlus.checkSensitiveWord(sensitiveWordMap, txt, i, matchType);
            if (length > 0) { //存在,加入list中
                sensitiveWordList.add(txt.substring(i, i + length));
                i = i + length - 1; //减1的原因，是因为for会自增
            }
        }
        return sensitiveWordList;
    }

    /**
     * 获取替换字符串
     */
    private static String getReplaceChars(String replaceChar, int length) {
        StringBuilder resultReplace = new StringBuilder();
        for (int i = 0; i < length; i++) {
            resultReplace.append(replaceChar);
        }
        return resultReplace.toString();
    }
}
