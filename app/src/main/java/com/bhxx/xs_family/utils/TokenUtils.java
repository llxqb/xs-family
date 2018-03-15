package com.bhxx.xs_family.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

public class TokenUtils {
    private static TokenUtils instance;

    private TokenUtils() {
    }

    public static TokenUtils getInstance() {
        if (instance == null) {
            instance = new TokenUtils();
        }
        return instance;
    }

    /**
     * @return 返回加密后的字符串token信息
     */
    public String configParams(String actions, HashMap<String, String> map) {
        actions = actions.substring(actions.indexOf("/xisheng/") + 9, actions.length());

        Object[] keys = map.keySet().toArray();

        if (keys == null || keys.length == 0) {
            return generateMD5(
                    actions + "taoken=" + "9ed203443190fdeea0271c2d7cbafe58cb520641da3f1697eea15dba4ea58d9e42f1a641")
                    + "9ed203443190fdeea0271c2d7cbafe58cb520641da3f1697eea15dba4ea58d9e42f1a641";
        }

        Arrays.sort(keys);

        StringBuilder sb = new StringBuilder();
        sb.append(actions);
        // 遍历集合
        for (int i = 0; i < keys.length; i++) {

            if (map.get(keys[i]) != null || !"".equals(map.get(keys[i]))) {
                if (i == 0) {
                    sb.append(keys[i] + "=" + map.get(keys[i]));
                } else {
                    sb.append("&" + keys[i] + "=" + map.get(keys[i]));
                }

            }

        }
        return generateMD5(
                sb.toString() + "&taoken=" + "9ed203443190fdeea0271c2d7cbafe58cb520641da3f1697eea15dba4ea58d9e42f1a641")
                + "9ed203443190fdeea0271c2d7cbafe58cb520641da3f1697eea15dba4ea58d9e42f1a641";
    }

    /**
     * @param key
     * @return 16进制字符串 加密传递过来的字符串信息
     */
    private static String generateMD5(String key) {
        try {
            // 得到MD5加密的对象
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            // 使用update方法向mDigest传送数据
            mDigest.update(key.getBytes());
            // 计算摘要，生成散列码，返回结果二进制数组
            byte[] bytes = mDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                // 将加密后的字节数组转换为16进制的字符串
                // （(0xFF & bytes[i])）先将数据进行与运算，0XFF表示整数，意思是先将byt转化为整数，防止计算错误
                String hex = Integer.toHexString(0xFF & bytes[i]);
                // 16进制每两个字符代表一个字节，为了保证为16进制，当不足两位时前面加0
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(key.hashCode());
        }
    }

}
