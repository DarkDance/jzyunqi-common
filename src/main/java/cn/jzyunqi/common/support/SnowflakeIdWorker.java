package cn.jzyunqi.common.support;

import cn.jzyunqi.common.model.SnowflakeId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * snowflake每秒能够产生26万ID左右
 *
 * @author wiiyaya
 * @date 2018/5/3
 */
public class SnowflakeIdWorker {
    // ==============================Fields===========================================
    /**
     * 纪元-开始时间截 (2018-06-01)
     */
    private final static long EPOCH = 1527782400000L;

    /**
     * 机器id所占的位数，可使用MAC地址来唯一标示工作机器
     */
    private final static long WORKER_ID_BITS = 5L;

    /**
     * 数据标识id所占的位数，可以使用IP+Path来区分工作进程
     */
    private final static long DATA_CENTER_ID_BITS = 5L;

    /**
     * 序列在id中占的位数 同一毫秒内的同一集群的同一机器上同时有2^12 - 1 个线程
     */
    private final static long SEQUENCE_BITS = 12L;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /**
     * 支持的最大数据标识id，结果是31
     */
    private final static long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    /**
     * 机器ID向左移12位
     */
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 数据标识id向左移17位(12+5)
     */
    private final static long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private final static long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private final static long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /**
     * 工作机器ID(0~31)
     */
    private long workerId;

    /**
     * 数据中心ID(0~31)
     */
    private long dataCenterId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    //==============================Constructors=====================================

    /**
     * 构造函数
     *
     * @param dataCenterId 数据中心ID (0~31)
     * @param workerId     工作ID (0~31)
     */
    public SnowflakeIdWorker(long dataCenterId, long workerId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("data center Id can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    // ==============================Methods==========================================

    /**
     * 获取下一个十六进制id
     *
     * @return id
     */
    public String nextHexId() {
        return Long.toHexString(nextId()).toUpperCase();
    }

    /**
     * 获得下一个ID (该方法是线程安全的)，最大为7FFFFFFFFFFFFFFF（9223372036854775807）
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT) //
                | (dataCenterId << DATA_CENTER_ID_SHIFT) //
                | (workerId << WORKER_ID_SHIFT) //
                | sequence;
    }

    /**
     * 根据id解析出时间
     *
     * @param hexString id 16进制
     * @return 时间
     */
    public SnowflakeId parserId(String hexString) {
        return parserId(Long.parseLong(hexString, 16));
    }

    /**
     * 根据id解析出时间
     *
     * @param id id
     * @return 时间
     */
    public SnowflakeId parserId(long id) {
        long timestamp = (id >> TIMESTAMP_LEFT_SHIFT) + EPOCH; //时间戳
        long dcwksq = id - ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT); //剩下dc + wk + sq
        long dc = dcwksq >> DATA_CENTER_ID_SHIFT; //dc
        long wksq = dcwksq - (dc << DATA_CENTER_ID_SHIFT); //剩下wk + sq
        long wk = wksq >> WORKER_ID_SHIFT;
        long sq = wksq - (wk << WORKER_ID_SHIFT); //剩下 sq

        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("+8"));

        SnowflakeId snowflakeId = new SnowflakeId();
        snowflakeId.setTime(time);
        snowflakeId.setDataCenterId(dc);
        snowflakeId.setWorkerId(wk);
        snowflakeId.setSequence(sq);
        return snowflakeId;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }
}
