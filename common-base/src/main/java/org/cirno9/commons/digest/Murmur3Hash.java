package org.cirno9.commons.digest;

/**
 * Created by lixuhui on 2019/3/28.
 */
public class Murmur3Hash implements HashHelper {

    /**
     * seed copy from com.facebook.util.digest.MurmurHash
     * 一致性哈希要求结果固定
     */
    private static final int JCOMMON_SEED = 131800700;

    @Override
    public byte[] hash(Object object) {
        try {
            String str = String.valueOf(object);
            byte[] bytes = str.getBytes();
            MurmurHash3.LongPair output = new MurmurHash3.LongPair();

            MurmurHash3.murmurhash3_x64_128(bytes, 0, bytes.length, JCOMMON_SEED, output);

            return toBytes(output);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int outLength() {
        return 128;
    }

    private byte[] toBytes(MurmurHash3.LongPair longPair) {
        long val1 = longPair.val1;
        long val2 = longPair.val2;

        byte[] bytes = new byte[32];

        int index = 31;
        for (; index >= 16; index --) {
            bytes[index] = (byte) (val2 % 16);
            val2 /= 16;
        }
        for (; index >= 0; index --) {
            bytes[index] = (byte) (val1 % 16);
            val1 /= 16;
        }
        return bytes;
    }
}
