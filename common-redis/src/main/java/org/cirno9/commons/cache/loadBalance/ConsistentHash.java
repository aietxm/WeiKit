package org.cirno9.commons.cache.loadBalance;

import org.cirno9.commons.RandomUtils;
import org.cirno9.commons.digest.HashHelper;
import org.cirno9.commons.digest.Md5Hash;
import sun.plugin.dom.exception.InvalidStateException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidParameterException;
import java.util.*;

/**
 * Created by lixuhui on 2019/3/22.
 */
public class ConsistentHash<N, D> {

    private SortedMap<Long, N> nodes = new TreeMap<>();

    private final HashHelper hashHelper;

    private long maxValue;

    public ConsistentHash(HashHelper hashHelper) {
        this.hashHelper = hashHelper;
    }

    private long murmurHash(Object object) {
        String origin = String.valueOf(object);
        ByteBuffer buf = ByteBuffer.wrap(origin.getBytes());
        int seed = 0x1234ABCD;

        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(
                    ByteOrder.LITTLE_ENDIAN);
            // for big-endian version, do this first:
            // finish.position(8-buf.remaining());
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }
        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return (int) (h & 0x7fffffffL);
    }

    private long hash(Object object) {
        byte[] hash = hashHelper.hash(object);
        if (hash == null || hash.length < 8) {
            throw new InvalidParameterException();
        }
        return Long.MAX_VALUE &
                (((hash[7] & 0xFFL) << 56) |
                ((hash[6] & 0xFFL) << 48) |
                ((hash[5] & 0xFFL) << 40) |
                ((hash[4] & 0xFFL) << 32) |
                ((hash[3] & 0xFFL) << 24) |
                ((hash[2] & 0xFFL) << 16) |
                ((hash[1] & 0xFFL) << 8) |
                ((hash[0] & 0xFFL)));
    }

    public void addNode(N node) {
        long key = murmurHash(node);
        if (nodes.get(key) != null) {
            throw new InvalidStateException("duplicated node");
        }
        nodes.put(key, node);
        if (key > maxValue) {
            maxValue = key;
        }
    }

    public void removeNode(N node) {
        long key = murmurHash(node);
        if (nodes.get(key) != null) {
            nodes.remove(key);
        }
        if (key == maxValue) {
            maxValue = nodes.lastKey();
        }
    }

    public N findNode(D data) {
        if (nodes.isEmpty()) {
            throw new InvalidStateException("empty node pool");
        }
        long key = murmurHash(data) % maxValue;
        SortedMap<Long, N> tails = nodes.tailMap(key);
        if (tails.isEmpty()) {
            throw new InvalidStateException("invalid state");
        }
        return tails.get(tails.firstKey());
    }

    public static void main(String[] args) {
        ConsistentHash<String, String> consistentHash = new ConsistentHash<>(new Md5Hash());

        // 生成100个Node
        List<String> serverIps = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            serverIps.add("192.168.1." + i);
            // 添加虚拟节点
            serverIps.add("192.168.1." + i + "-replica");
        }

        // 添加到环中
        Map<String, List<String>> serverMap = new HashMap<>();
        for (String ip : serverIps) {
            serverMap.put(ip, new ArrayList<>());
            consistentHash.addNode(ip);
        }

        // 生成数据，放到Node中并另外保存
        Map<String, String> dataMap = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            String randomString = RandomUtils.randomString(10);
            String ip = consistentHash.findNode(randomString);
            dataMap.put(randomString, ip);
            serverMap.get(ip).add(randomString);
        }

        // 统计每个Node中的数据量并计算方差
        double total = 0;
        for (String ip : serverIps) {
            int size = serverMap.get(ip).size();
            total += size;
        }
        double avg = total / serverIps.size();
        double variance = 0;
        for (String ip : serverIps) {
            int size = serverMap.get(ip).size();
            variance += (size - avg) * (size - avg);
            serverMap.get(ip).clear();
        }
        System.out.println("variance:" + variance / serverIps.size());


        // 移除20个Node
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            String ip = serverIps.remove(random.nextInt(serverIps.size()));
            consistentHash.removeNode(ip);
        }

        // 重放数据，统计两次命中相同Node的比例
        int count = 0;
        for (String data : dataMap.keySet()) {
            String ip = consistentHash.findNode(data);
            serverMap.get(ip).add(data);
            if (ip == dataMap.get(data)) {
                count++;
            }
        }
        System.out.println("percent:" + (double) count / 10000);

        // 统计每个Node中的数据量并计算方差
        total = 0;
        for (String ip : serverIps) {
            int size = serverMap.get(ip).size();
            total += size;
        }
        avg = total / serverIps.size();
        variance = 0;
        for (String ip : serverIps) {
            int size = serverMap.get(ip).size();
            variance += Math.pow(size - avg, 2);
        }
        System.out.println("variance:" + variance / serverIps.size());

    }


}
