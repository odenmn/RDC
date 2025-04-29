package com.xjx.example.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncryptUtil {
    public static String encryptPassword(String password) {
        try {
            // 使用MessageDigest类的静态方法getInstance来获取一个MD5算法的实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 将传入的密码字符串先转换为字节数组（password.getBytes()），
            // 然后计算出该字节数组的哈希值，结果存储在byte数组digest中。
            byte[] digest = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                // 将字节数组中的每个字节转换为对应的十六进制字符串形式，并将其追加到sb中
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
