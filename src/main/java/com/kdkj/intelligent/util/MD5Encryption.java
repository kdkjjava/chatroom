package com.kdkj.intelligent.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encryption {
    private static final Logger logger = LogManager.getLogger(MD5Encryption.class);
    private MD5Encryption() {

    }
    public static String getEncryption(String originString)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        if (originString != null) {
            try {
                // 指定加密的方式为MD5
                MessageDigest md = MessageDigest.getInstance("MD5");
                // 进行加密运算
                byte[] bytes = md.digest(originString.getBytes("ISO8859-1"));
                for (int i = 0; i < bytes.length; i++) {
                    // 将整数转换成十六进制形式的字符串 这里与0xff进行与运算的原因是保证转换结果为32位
                    String str = Integer.toHexString(bytes[i] & 0xFF);
                    if (str.length() == 1) {
                        str += "F";
                    }
                    result.append(str);
                }
            } catch (NoSuchAlgorithmException e) {
                logger.error(e.getMessage());
            }
        }
        return result.toString();
    }

}