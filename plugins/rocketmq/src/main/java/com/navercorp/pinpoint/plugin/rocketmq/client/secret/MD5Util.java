package com.navercorp.pinpoint.plugin.rocketmq.client.secret;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @Title:
 * @Company:宝尊电子商务有限公司
 * @Description:
 * @Author:xiaozhou.zhou
 * @Since:2015年5月14日
 * @Copyright:Copyright (c) 2015
 * @ModifyDate:
 * @Version:1.1.0
 */
public class MD5Util {

    /**
     * 默认的密码字符串组合，apache校验下载的文件的正确性用的就是默认的这个组合
     */
    protected static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    protected static MessageDigest messagedigest = null;
    protected MessageDigest messageDigestNew = null;

    public MD5Util() {
        try {
            messageDigestNew = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.err.println(MD5Util.class.getName() + "初始化失败，MessageDigest不支持MD5Util。");
            e.printStackTrace();
        }
    }
    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsaex) {
            System.err.println(MD5Util.class.getName() + "初始化失败，MessageDigest不支持MD5Util。");
            nsaex.printStackTrace();
        }
    }

    public static byte[] getFileMd5Byte(File file) {
        byte[] bytes = getByte(file);
        messagedigest.update(bytes);
        return messagedigest.digest();
    }

    /**
     * 适用于上G大的文件
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public static String getFileMD5String(File file) {
        return bufferToHex(getFileMd5Byte(file));
    }

    public static byte[] getMD5Byte(String resource) {
        try {
            messagedigest.update(resource.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return messagedigest.digest();
    }

    public static String getMD5String(String s) {
        byte[] byteArray = null;
        try {
            byteArray = s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return getMD5String(byteArray);
    }

    public static String getUpperMD5String(String s) {
        byte[] resource = null;
        try {
            resource = s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return getMD5String(resource).toUpperCase();
    }

    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    public synchronized static String getMD5_16(String string) {
        return getMD5String(string).substring(8, 24);
    }

    public static byte[] getByte(File file) {
        byte[] ret = null;
        try {
            if (file == null) {
                // log.error("helper:the file is null!");
                return null;
            }
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            out.close();
            ret = out.toByteArray();
        } catch (IOException e) {
            // log.error("helper:get bytes from file process error!");
            e.printStackTrace();
        }
        return ret;
    }
    
    
    /**
     * 为了防止多线程采用新建对象方式获取
     * 
     * @param resource
     * @return
     * @Description:
     */
    public byte[] getMD5ByteNew(String resource) {
        try {
            messageDigestNew.update(resource.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return messageDigestNew.digest();
    }

    public String getMd5_16New(String content) {
        return getMd5StringNew(content).substring(8, 24);
    }

    public String getMd5StringNew(String resource) {
        return bufferToHex(getMD5ByteNew(resource));
    }
    
}
