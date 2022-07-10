package org.nep.demo.snowflake;

import org.nep.demo.snowflake.exception.TimeBackWard;

/**
 * <h3>雪花算法</h3>
 * <h3>1. 建造者模式封装</h3>
 */
public class SnowFlake {

    public SnowFlake(long machine, long dataCenter) {
        this.machine = machine;
        this.dataCenter = dataCenter;
    }

    // 固定时间戳: 生成的时间戳 = 当前时间戳 - 固定时间戳
    private static final long FIXED_TIMESTAMP = 1480166465631L;

    /**
     * <h3>1. 序列号比特位长度: 12</h3>
     * <h3>2. 工作机器比特位可以细分为两个部分: </h3>
     * <h3>2.1 数据中心比特位长度: 5</h3>
     * <h3>2.2 机器序列号比特位长度: 5</h3>
     */
    private static final int SEQUENCE_BIT = 12;
    private static final int MACHINE_BIT = 5;
    private static final int DATA_CENTER_BIT = 5;

    /**
     * <h3>问题: 取反为什么就恰好是最大值, 低位部分可以理解, 但是高位部分不也应该取反吗</h3>
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
    private static final long MAX_MACHINE = ~(-1L << MACHINE_BIT);
    private static final long MAX_DATA_CENTER = ~(-1L << DATA_CENTER_BIT);

    /**
     * <h3>序列号偏移量: 0</h3>
     * <h3>机器序列号偏移量: 12</h3>
     * <h3>数据中心偏移量 5 + 12</h3>
     * <h3>时间戳偏移量: 5 + 5 + 12</h3>
     */
    private static final int SEQUENCE_LEFT = 0;
    private static final int MACHINE_LEFT = SEQUENCE_LEFT + SEQUENCE_BIT;
    private static final int DATA_CENTER_LEFT = MACHINE_LEFT + MACHINE_BIT;
    private static final int TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;

    private final long machine;
    private final long dataCenter;
    private long sequence;
    private long lastTimeStamp = -1L;

    /**
     * <h3>获取当前时间戳</h3>
     */
    private long getNewTimeStamp(){
        return System.currentTimeMillis();
    }

    /**
     * <h3>获取下个时间戳: 确保和当前的时间戳不同</h3>
     */
    private long getNextNewTimeStamp(){
        long currentTimeStamp = getNewTimeStamp();
        while (currentTimeStamp <= lastTimeStamp)
            currentTimeStamp = getNewTimeStamp();
        return currentTimeStamp;
    }

    /**
     * <h3>生成全局唯一 {@code id}</h3>
     */
    public synchronized long generate(){
        // 1. 比较当前时间戳和记录的上一次时间戳: 如果小于上次时间戳, 那么认为发生时钟回拨
        long currentTimeStamp = getNewTimeStamp();
        if (currentTimeStamp < lastTimeStamp)
            throw new TimeBackWard("[Neptune SnowFlake]: occurred time backward");
        // 2. 自增序列号: 如果当前时间戳和上次相同, 那么自增序列号, 否则直接置为 0 (思考: 时间戳可能相同吗)
        if (currentTimeStamp > lastTimeStamp){
            sequence = 0L;
        }else{
            // 2.1 和序列号最大值进行与运算, 取出最低位: 主要目的防止序列号超出最大值
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 2.2 如果序列号已经超过最大值, 那么选择下一秒
            if (sequence == 0L)
                currentTimeStamp = getNextNewTimeStamp();
        }
        // 3. 更新上一次记录的时间戳
        lastTimeStamp = currentTimeStamp;
        // 4. 组装返回结果
        return (currentTimeStamp - FIXED_TIMESTAMP) << TIMESTAMP_LEFT
                       | dataCenter << DATA_CENTER_LEFT
                       | machine << MACHINE_LEFT
                       | sequence;
    }

}
