package org.cirno9.commons.concurrent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * snowflake一般实现
 * Created by lixuhui on 2019/3/5.
 */
public class Snowflake {

    private static final String TIMESTAMP_OFFSET_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_TIMESTAMP_OFFSET_DATE = "2019-01-01";
    private static final int DEFAULT_TIMESTAMP_BITS = 41;
    private static final int DEFAULT_WORKER_ID_BITS = 10;
    private static final int DEFAULT_SEQUENCE_BITS = 12;

    // 时间戳左移位数
    private int timestampShift;

    // 基准时间戳
    private long timestampOffset;

    // 机器id mask
    private long workerIdMask;

    // sequence取模数
    private int sequenceMod;

    // sequence位数
    private int sequenceBits;

    // 第一位
    private static final long FINAL_MASK = Long.MAX_VALUE;

    private volatile long cachedTimestamp;

    private volatile int sequence;

    // 单例
    private static volatile Snowflake instance;

    /**
     * 默认设置
     * @param workerId 机器id
     * @return
     */
    public static Snowflake getSingletonWithDefaultSettings(long workerId) {
        return getSingletonWithCustomSettings(workerId, DEFAULT_TIMESTAMP_OFFSET_DATE, DEFAULT_TIMESTAMP_BITS, DEFAULT_WORKER_ID_BITS, DEFAULT_SEQUENCE_BITS);
    }

    /**
     * 带基准时间
     * @param workerId 机器id
     * @param timestampOffsetDate 设置基准时间
     * @return
     */
    public static Snowflake getSingletonWithOffsetDate(long workerId, String timestampOffsetDate) {
        return getSingletonWithCustomSettings(workerId, timestampOffsetDate, DEFAULT_TIMESTAMP_BITS, DEFAULT_WORKER_ID_BITS, DEFAULT_SEQUENCE_BITS);
    }

    /**
     * 自定义各bit分配
     * @param workerId 机器id
     * @param timestampOffsetDate 基准时间
     * @param timestampBits 时间戳分配长度，建议33bit以上，否则无法通过parseTimestamp转换回timestamp
     * @param workerIdBits 机器id分配长度
     * @param sequenceBits 序列号分配长度
     * @return
     */
    public static Snowflake getSingletonWithCustomSettings(long workerId, String timestampOffsetDate, int timestampBits, int workerIdBits, int sequenceBits) {
        Snowflake result = instance;
        if (result == null) {
            synchronized (Snowflake.class) {
                result = instance;
                if (result == null) {
                    result = new Snowflake(workerId, timestampOffsetDate, timestampBits, workerIdBits, sequenceBits);
                    instance = result;
                }
            }
        }
        return result;
    }

    private Snowflake(long workerId, String timestampOffsetDate, int timestampBits, int workerIdBits, int sequenceBits) {
        if (workerId < 0 || timestampBits < 0 || workerIdBits < 0 || sequenceBits < 0 || sequenceBits > 31) {
            throw new IllegalArgumentException();
        }
        if (timestampBits + workerIdBits + sequenceBits != 63) {
            throw new IllegalArgumentException();
        }
        int maxWorkerId = 1 << workerIdBits - 1;
        if (maxWorkerId < workerId) {
            throw new IllegalArgumentException();
        }

        try {
            timestampOffset = new SimpleDateFormat(TIMESTAMP_OFFSET_DATE_PATTERN).parse(timestampOffsetDate).getTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException();
        }
        timestampShift = workerIdBits + sequenceBits;
        workerIdMask = workerId << sequenceBits;
        sequenceMod = 1 << sequenceBits;

        this.sequenceBits = sequenceBits;
    }

    /**
     * 经过测试，使用synchronized关键字，生成效率已足够满足日常需求
     *
     * 计算方式：
     * id = 0111111....1111 &
     *      ((当前时间戳 - 基准时间戳) << 时间戳左移位数 | 机器id mask | 递增序列号)
     * 存在的问题：
     *      1. 时钟回调，该情况无法感知，可自行判断回调发生并自行解决id冲突问题，如使用新workerId等
     *      2. 单机每秒生产id数的极限理论上是 2^sequenceBits 个，请设置合理范围
     * @throws IndexOutOfBoundsException 消耗速度大于理论上限
     * @return
     */
    public synchronized long getId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp > cachedTimestamp) {
            cachedTimestamp = timestamp;
            sequence = 0;
        }
        long sequenceValue = sequence++;
        if (sequenceValue >= sequenceMod) {
            throw new IndexOutOfBoundsException();
        }
        return FINAL_MASK &
                (((timestamp - timestampOffset) << timestampShift) | workerIdMask | sequenceValue);
    }

    public String parseBinary(long id) {
        StringBuilder stringBuilder = new StringBuilder("0000000000000000000000000000000000000000000000000000000000000000");
        for (int i = 63; i >= 0 && id > 0; i--) {
            stringBuilder.setCharAt(i, (char) ((id & 1) + 48));
            id >>= 1;
        }
        stringBuilder.insert(64 - this.sequenceBits, ' ');
        stringBuilder.insert(64 - timestampShift, ' ');
        stringBuilder.insert(1, ' ');
        return stringBuilder.toString();
    }

    public long parseTimestamp(long id) {
        return (id >> timestampShift) + timestampOffset;

    }

    public long parseWorkerId(long id) {
        return (id & ((1L << timestampShift) - 1)) >> sequenceBits;
    }

    public long parseSequence(long id) {
        return id & ((1L << sequenceBits) - 1);
    }

    public String describe(long id) {
        return "id:" + id + ", idBinary:" + parseBinary(id) + ", idPart:0-" + parseTimestamp(id) + "-" + parseWorkerId(id) + "-" + parseSequence(id);
    }

    public static void main(String[] args) throws Exception {
        // 并发测试，
        Snowflake snowflake = Snowflake.getSingletonWithCustomSettings(1, "2019-01-01", 33, 7, 23);
        Map<Long, Long> idSet = new ConcurrentHashMap<>();
        int threads = 50;
        int loopTimes = 100000;
        CountDownLatch latch = new CountDownLatch(threads);
        long start = System.nanoTime();
        for (int i = 0; i < threads; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < loopTimes; i++) {
                        try {
                            long id = snowflake.getId();
                            // 检查是否重复
                            if (idSet.containsKey(id)) {
                                System.out.println("contains" + id);
                            } else {
                                idSet.put(id, id);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    latch.countDown();
                }
            }).start();
        }
        latch.await();
        long end = System.nanoTime();
        for (long id : idSet.values()) {
            System.out.println(snowflake.describe(id));
        }
        System.out.println("size=" + idSet.size());
        System.out.println("cost=" + (end - start) / 1000000);
    }

}
