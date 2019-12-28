package com_7idear.framework.utils;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 加解密工具类
 * @author ieclipse 19-12-10
 * @description
 */
public class CipherUtils {

    /**
     * 密钥
     */
    private final static String SECRET_KEY = "cn.flu.framework";
    /**
     * 向量
     */
    private final static String IV         = "01234567";
    /**
     * 加解密统一使用的编码方式
     */
    private final static String ENCODING   = "UTF-8";

    /**
     * 3DES加密
     * @param s 普通文本
     * @return
     * @throws Exception
     */
    public static String encode3DES(String s)
            throws Exception {
        return encode3DES(SECRET_KEY, s);
    }

    /**
     * 3DES加密
     * @param key 密钥
     * @param s   普通文本
     * @return
     * @throws Exception
     */
    public static String encode3DES(String key, String s)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] encryptData = cipher.doFinal(s.getBytes(ENCODING));
        return Base64.encodeToString(encryptData, Base64.DEFAULT);
    }

    /**
     * 3DES解密
     * @param s 加密文本
     * @return
     * @throws Exception
     */
    public static String decode3DES(String s)
            throws Exception {
        return decode3DES(SECRET_KEY, s);
    }

    /**
     * 3DES解密
     * @param key 密钥
     * @param s   加密文本
     * @return
     * @throws Exception
     */
    public static String decode3DES(String key, String s)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(IV.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

        byte[] decryptData = cipher.doFinal(Base64.decode(s, Base64.DEFAULT));

        return new String(decryptData, ENCODING);
    }

    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
            'b', 'c', 'd', 'e', 'f'};

    /**
     * MD5加密
     * @param s 加密文本
     * @return
     */
    public static String MD5(String s) {
        if (TxtUtils.isEmpty(s)) return "";
        try {
            // 使用MD5创建MessageDigest对象
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            return toHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String MD5(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "";
        try {
            // 使用MD5创建MessageDigest对象
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(bytes);
            byte messageDigest[] = digest.digest();
            return toHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密字节
     * @param bytes 字节数组
     * @return
     */
    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(HEX_DIGITS[(bytes[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }

        return sb.toString();
    }

    /**
     * 获取文件的MD5
     */
    public static String MD5(File file) {
        if (file == null) {
            return "";
        }
        try {
            return MD5(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /** 获取文件的MD5 */
    public static String MD5(InputStream is) {
        if (is == null) {
            return "";
        }
        try {
            byte[] buffer = new byte[1024];
            MessageDigest digest = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while (numRead != -1) {
                numRead = is.read(buffer);
                if (numRead > 0) {
                    digest.update(buffer, 0, numRead);
                }
            }
            return toHexString(digest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String sha256(String base) {
        if (TxtUtils.isEmpty(base)) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /** Base64字符集 */
    private static final char[] BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
            .toCharArray();

    /**
     * Base64加密
     * @param bytes 字节数组
     * @return
     */
    public static String encodeBase64(byte[] bytes) {
        int start = 0;
        int len = bytes.length;
        StringBuffer buf = new StringBuffer(bytes.length * 3 / 2);

        int end = len - 3;
        int i = start;
        int n = 0;

        while (i <= end) {
            int d = ((((int) bytes[i]) & 0x0ff) << 16)
                    | ((((int) bytes[i + 1]) & 0x0ff) << 8)
                    | (((int) bytes[i + 2]) & 0x0ff);

            buf.append(BASE64_CHARS[(d >> 18) & 63]);
            buf.append(BASE64_CHARS[(d >> 12) & 63]);
            buf.append(BASE64_CHARS[(d >> 6) & 63]);
            buf.append(BASE64_CHARS[d & 63]);

            i += 3;

            if (n++ >= 14) {
                n = 0;
                buf.append(" ");
            }
        }

        if (i == start + len - 2) {
            int d = ((((int) bytes[i]) & 0x0ff) << 16) | ((((int) bytes[i + 1]) & 255) << 8);

            buf.append(BASE64_CHARS[(d >> 18) & 63]);
            buf.append(BASE64_CHARS[(d >> 12) & 63]);
            buf.append(BASE64_CHARS[(d >> 6) & 63]);
            buf.append("=");
        } else if (i == start + len - 1) {
            int d = (((int) bytes[i]) & 0x0ff) << 16;

            buf.append(BASE64_CHARS[(d >> 18) & 63]);
            buf.append(BASE64_CHARS[(d >> 12) & 63]);
            buf.append("==");
        }

        return buf.toString();
    }

    /**
     * Base64解密
     * @param c 字符
     * @return
     */
    private static int decodeBase64(char c) {
        if (c >= 'A' && c <= 'Z') return ((int) c) - 65;
        else if (c >= 'a' && c <= 'z') return ((int) c) - 97 + 26;
        else if (c >= '0' && c <= '9') return ((int) c) - 48 + 26 + 26;
        else switch (c) {
                case '+':
                    return 62;
                case '/':
                    return 63;
                case '=':
                    return 0;
                default:
                    throw new RuntimeException("unexpected code: " + c);
            }
    }

    /**
     * Base64解密
     * @param s 字符串
     * @return
     */
    public static byte[] decodeBase64(String s) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            decodeBase64(s, bos);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        byte[] decodedBytes = bos.toByteArray();
        try {
            bos.close();
            bos = null;
        } catch (IOException ex) {
            System.err.println("Error while decoding BASE64: " + ex.toString());
        }
        return decodedBytes;
    }

    /**
     * Base64解密
     * @param s  字符串
     * @param os 输出流
     * @throws IOException
     */
    private static void decodeBase64(String s, OutputStream os)
            throws IOException {
        int i = 0;

        int len = s.length();

        while (true) {
            while (i < len && s.charAt(i) <= ' ') i++;

            if (i == len) break;

            int tri = (decodeBase64(s.charAt(i)) << 18) + (decodeBase64(s.charAt(i + 1)) << 12) + (
                    decodeBase64(s.charAt(i + 2))
                            << 6) + (decodeBase64(s.charAt(i + 3)));

            os.write((tri >> 16) & 255);
            if (s.charAt(i + 2) == '=') break;
            os.write((tri >> 8) & 255);
            if (s.charAt(i + 3) == '=') break;
            os.write(tri & 255);

            i += 4;
        }
    }
}
