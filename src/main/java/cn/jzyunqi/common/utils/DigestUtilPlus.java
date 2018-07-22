package cn.jzyunqi.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class DigestUtilPlus {
    //现代密码学算法大致可以分为3个大块。分别是对称加密symmetric，非对称加密asymmetric，消息摘要(不可逆加密)hash。
    //消息摘要(不可逆加密)：MD5,SHA（SHA1,SHA2、SHA256、SHA512）,MAC(HmacMD5、HmacSHA1、HmacSHA256、HmacSHA384、HmacSHA512)
    //对称加密算法：AES,DES,3DES
    //非对称加密算法：RSA,DSA,ECC

    //消息摘要(不可逆加密)使用Mac或MessageDigest负责完成加密或解密工作
    //对称加密算法使用Cipher负责完成加密或解密工作
    //非对称加密算法使用Signature完成加密或解密工作

    private static final String DEFAULT_ENCODING = "utf-8";

    public static class Base64 extends org.apache.commons.codec.binary.Base64 {}

    /**
     * 消息摘要(不可逆加密)：MD5
     */
    public static class MD5{
        public static String sign(String content) {
            return DigestUtils.md5Hex(content);
        }

        public static byte[] sign(byte[] content) {
            return DigestUtils.md5(content);
        }
    }

    /**
     * 消息摘要(不可逆加密)：SHA1
     */
    public static class SHA1{
        public static String sign(String content) {
            return DigestUtils.sha1Hex(content);
        }
    }

    /**
     * 消息摘要(不可逆加密)：HmacSHA1
     */
    public static class HmacSHA1{
        public static String sign(String content, String key) throws Exception{
            Key javaKey = new SecretKeySpec(key.getBytes(DEFAULT_ENCODING), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(javaKey);
            return Base64.encodeBase64String(mac.doFinal(content.getBytes(DEFAULT_ENCODING)));
        }
    }

    /**
     * 对称加密算法：AES
     * PS: 因为某些国家的进口管制限制，Java发布的运行环境包中的加解密有一定的限制。默认不允许256位密钥的AES加解密，解决方法就是修改策略文件。
     * 官方网站提供了JCE无限制权限策略文件的下载：
     *   JDK6的下载地址：
     *   http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html
     *
     *   JDK7的下载地址：
     *   http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
     *
     *   JDK8的下载地址：
     *   http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
     */
    public static class AES{
        static {
            Security.addProvider(new BouncyCastleProvider());
        }

        /**
         * 加密
         */
        public static String encrypt(String content, String key, String iv) throws Exception {
            Key javaKey = new SecretKeySpec(Base64.decodeBase64(key), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, javaKey, new IvParameterSpec(Base64.decodeBase64(iv)));
            return Base64.encodeBase64String(cipher.doFinal(Base64.decodeBase64(content)));
        }

        /**
         * 解密
         */
        public static String decrypt(String content, String key, String iv) throws Exception {
            Key javaKey = new SecretKeySpec(Base64.decodeBase64(key), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, javaKey, new IvParameterSpec(Base64.decodeBase64(iv)));
            return Base64.encodeBase64String(cipher.doFinal(Base64.decodeBase64(content)));
        }
    }

    /**
     * 非对称加密算法：RSA256
     */
    public static class RSA256{

        /**
         * 加密
         */
        public static String encrypt(String content, String privateKey) throws Exception {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey priKey =  keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey)));

            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initSign(priKey);
            signature.update(content.getBytes(DEFAULT_ENCODING));

            return Base64.encodeBase64String(signature.sign());
        }

        /**
         * 解密（校验）
         */
        public static boolean decrypt(String content, String sign, String publicKey) throws Exception {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(publicKey)));

            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initVerify(pubKey);
            signature.update(content.getBytes(DEFAULT_ENCODING));

            return signature.verify(Base64.decodeBase64(sign));
        }
    }
}
