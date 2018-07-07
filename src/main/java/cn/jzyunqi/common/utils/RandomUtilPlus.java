package cn.jzyunqi.common.utils;

import org.apache.commons.lang3.RandomUtils;

import java.util.List;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public final class RandomUtilPlus extends RandomUtils {

    private RandomUtilPlus() {

    }

    /**
     * 生成随机数，且生成的随机数不在排除的范围内
     *
     * @param startInclusive 开始值
     * @param endExclusive   结束值
     * @param exclusives     需要排除的值
     * @return 随机数
     */
    public static int nextInt(final int startInclusive, final int endExclusive, Integer... exclusives) {
        int i = nextInt(startInclusive, endExclusive);
        List<Integer> exclusiveList = CollectionUtilPlus.asList(exclusives);
        while (exclusiveList.contains(i)) {
            i = nextInt(startInclusive, endExclusive);
        }
        return i;
    }
}
