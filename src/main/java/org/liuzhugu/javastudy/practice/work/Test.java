package org.liuzhugu.javastudy.practice.work;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.Security;
import java.util.*;

public class Test {
    public static void main(String[] args) {
        Test en = new Test();
        try {
            //测试加密
            Map<String, String> map = new HashMap<String, String>();
            map.put("order_id", "25415852018092510514118");
            String jsonObject = JSONObject.toJSONString(map);

            String encodingAesKey = "omKBhV2NThBSiVxV1noymkF0ZmzRCzVY";
            String dataA = en.encryptA(jsonObject, encodingAesKey);
            System.out.println("mfw加密后数据:" + dataA);
            String dataB = en.encryptB(jsonObject, encodingAesKey);
            System.out.println("test加密后数据:" + dataB);

//            String ret = en.decrypt("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", encodingAesKey);
//            System.out.println("解密后数据:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String encryptA(String text, String aesKey) throws Exception {
        ByteGroup byteCollector = new ByteGroup();
        byte[] textBytes = text.getBytes(CHARSET);
        byteCollector.addBytes(textBytes);
        byte[] padBytes = fillByte(byteCollector.size());
        byteCollector.addBytes(padBytes);
        // 获得最终的字节流, 未加密
        byte[] unencrypted = byteCollector.toBytes();
        System.out.println("mfw待加密原始数据:" + text);
        System.out.println("mfw待加密数据:" + new String(unencrypted));

        Base64 base64 = new Base64();
        String base64Encrypted = null;
        try {
            // 设置加密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey.getBytes(CHARSET), "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKey.getBytes(CHARSET), 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            // 加密
            byte[] encrypted = cipher.doFinal(unencrypted);

            // 使用BASE64对加密后的字符串进行编码
            base64Encrypted = base64.encodeToString(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return base64Encrypted;
    }
    /**
     * 加密
     * @param content	需要加密的内容
     * @param aseKey	生成密钥使用的密码
     * @return
     */
    public String encryptB(String content, String aseKey) {
        Base64 base64 = new Base64();
        ByteGroup byteCollector = new ByteGroup();
        byte[] textBytes = content.getBytes(CHARSET);
        byteCollector.addBytes(textBytes);
        byte[] padBytes = fillByte(byteCollector.size());
        byteCollector.addBytes(padBytes);
        // 获得最终的字节流, 未加密
        byte[] unencrypted = byteCollector.toBytes();
        try {
            /**
             * SecretKeySpec(byte[] key, String algorithm) : SecretKeySpec的构造方法之一，根据给定的字节数组构造一个密钥。
             * key : 密钥的密钥内容。复制该数组的内容来防止后续修改。
             * algorithm : 与给定的密钥内容相关联的密钥算法的名称。
             */
            SecretKeySpec key = new SecretKeySpec(aseKey.getBytes(CHARSET), "AES");
            /**
             * Security : 此类集中了所有的安全属性和常见的安全方法。其主要用途之一是管理提供程序。
             * addProvider(Provider provider) : 将提供程序添加到下一个可用位置。
             * provider : 要添加的提供程序。
             */
            Security.addProvider(new BouncyCastleProvider());
            /**
             * Cipher : 该类为加密和解密提供加密密码功能。
             * getInstance(String transformation, String provider) : 创建一个实现指定转换的 Cipher 对象，该转换由指定的提供程序提供。
             * transformation : 转换的名称，例如 DES/CBC/PKCS5Padding。描述为产生某种输出而在给定的输入上执行的操作（或一组操作）的字符串。转换始终包括加密算法的名称（例如，DES），后面可能跟有一个反馈模式和填充方案。
             * provider : 提供程序的名称
             */
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            /**
             * init(int opmode, Key key) : 用密钥初始化此 cipher。为以下 4 种操作之一初始化该 cipher：加密、解密、密钥包装或密钥打开，这取决于 opmode 的值。
             * opmode : 此 cipher 的操作模式（其为如下之一：ENCRYPT_MODE、DECRYPT_MODE、WRAP_MODE 或 UNWRAP_MODE）
             * key : 密钥
             */
            IvParameterSpec iv = new IvParameterSpec(aseKey.getBytes(CHARSET), 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, key,iv);
            byte[] byteContent = unencrypted;
            /**
             * doFinal(byte[] input) : 按单部分操作加密或解密数据，或者结束一个多部分操作。数据被加密还是解密取决于此 cipher 的初始化方式。
             * input : 输入缓冲区
             */
            byte[] cryptograph = cipher.doFinal(byteContent);
            /**
             * ！注：在这儿，加密后的byte数组是不能强制转换成字符串的(即：new String（result）); 换言之,字符串和byte数组在这种情况下不是互逆的。
             * 处理方式有两种：
             * 	1.将result转化为十六进制的数据再做处理（需要自己写一个转换方法）—— 用法和“128位密钥的加解密算法”的一模一样，可以参考上文；
             * 	2.将result进行Base64(也可以用 BASE64Encode)再次加密在进行强制转换（不需要自己写方法，省事儿）。（主要编解码方式有Base64, HEX, UUE, 7bit等等。此处看服务器需要什么编码方式）
             */
            return new String(base64.encodeToString(cryptograph));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String decrypt(String text, String aesKey) throws Exception{
        byte[] original;
        byte[] result = new byte[0];
        try {
            // 设置解密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec key_spec = new SecretKeySpec(aesKey.getBytes(CHARSET), "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey.getBytes(CHARSET), 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);

            // 使用BASE64对密文进行解码
            byte[] encrypted = Base64.decodeBase64(text);

            // 解密
            original = cipher.doFinal(encrypted);
            //去除补位
            result = removeByte(original);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Unicode转中文
        return new String(result, CHARSET);
    }

    static Charset CHARSET = Charset.forName("utf-8");
    static int BLOCK_SIZE = 32;

    /**
     * 获得对明文进行补位填充的字节.
     *
     * @param count 需要进行填充补位操作的明文字节个数
     * @return 补齐用的字节数组
     */
    public static byte[] fillByte(int count) {
        // 计算需要填充的位数
        int amountToPad = BLOCK_SIZE - (count % BLOCK_SIZE);
        if (amountToPad == 0) {
            amountToPad = BLOCK_SIZE;
        }
        // 获得补位所用的字符
        char padChr = chr(amountToPad);
        String tmp = new String();
        for (int index = 0; index < amountToPad; index++) {
            tmp += padChr;
        }
        return tmp.getBytes(CHARSET);
    }

    /**
     * 删除解密后明文的补位字符
     *
     * @param decrypted 解密后的明文
     * @return 删除补位字符后的明文
     */
    public static byte[] removeByte(byte[] decrypted) {
        int pad = (int) decrypted[decrypted.length - 1];
        if (pad < 1 || pad > 32) {
            pad = 0;
        }
        return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
    }

    /**
     * 将数字转化成ASCII码对应的字符，用于对明文进行补码
     *
     * @param a 需要转化的数字
     * @return 转化得到的字符
     */
    static char chr(int a) {
        byte target = (byte) (a & 0xFF);
        return (char) target;
    }




    class ByteGroup {
        ArrayList<Byte> byteContainer = new ArrayList<Byte>();

        public byte[] toBytes() {
            byte[] bytes = new byte[byteContainer.size()];
            for (int i = 0; i < byteContainer.size(); i++) {
                bytes[i] = byteContainer.get(i);
            }
            return bytes;
        }

        public ByteGroup addBytes(byte[] bytes) {
            for (byte b : bytes) {
                byteContainer.add(b);
            }
            return this;
        }

        public int size() {
            return byteContainer.size();
        }
    }

    /**
     * 随机生成字符串
     * @param maxLength
     * @return string 生成的字符串
     */
    public String sGetRandomStr(int maxLength)
    {
        String sRandom = "";
        Random random=new Random();
        String sPol = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < maxLength; i++) {
            sRandom += sPol.indexOf(random.nextInt(64));
        }
        return sRandom;
    }

}
