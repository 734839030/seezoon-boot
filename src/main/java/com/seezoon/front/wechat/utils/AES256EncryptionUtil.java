package com.seezoon.front.wechat.utils;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES256EncryptionUtil {
    public static final String TAG = AES256EncryptionUtil.class.getSimpleName();
    public static final String ALGORITHM = "AES/ECB/PKCS7Padding";
 
    static {
    	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
    /**
     * 生成key
     * @param password
     * @return
     * @throws Exception
     */
    private static byte[] getKeyByte(String password) throws Exception {
        byte[] seed = new byte[24];
        seed = password.getBytes();
        return seed;
    }
 
    /**
     * 加密
     * @param data
     * @return
     */
    public static String encrypt(String data,String mPassword) throws Exception{
        String string = "";
        byte[] keyByte = getKeyByte(mPassword);
        SecretKeySpec keySpec = new SecretKeySpec(keyByte,"AES"); //生成加密解密需要的Key
        byte[] byteContent = data.getBytes("utf-8");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] result = cipher.doFinal(byteContent);
        string = parseByte2HexStr(result);  //转成String
        return string;
    }
 
    /**
     * 解密
     * @param data
     * @return
     */
    public static String decrypt(String data,String mPassword) throws Exception{
        String string = "";
        byte[] keyByte = getKeyByte(mPassword);
        byte[] byteContent = parseHexStr2Byte(data);  //转成byte
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(keyByte,"AES"); //生成加密解密需要的Key
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = cipher.doFinal(byteContent);
        string = new String(decoded);
        return string;
    }
 
    /**
     * 转化为String
     * @param buf
     * @return
     */
    private static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
 
    /**
     * 将16进制转换为二进制
     * @param hexStr
     * @return
     */
    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}