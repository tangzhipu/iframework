package wph.iframework.utils;

import java.security.MessageDigest;

/**
 * MD5加密工具
 * 
 * @author 王培鹤
 * 
 */
public final class Md5Utils
{
    private Md5Utils()
    {
    }
    
    public static String encode(String text)
    {
        if (text == null)
        {
            throw new NullPointerException();
        }
        
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        try
        {
            byte[] input = text.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            md.update(input);
            // 获得密文
            byte[] output = md.digest();
            // 把密文转换成十六进制的字符串形式
            int length = output.length;
            char result[] = new char[length * 2];
            int k = 0;
            for (int i = 0; i < length; i++)
            {
                byte b = output[i];
                result[k++] = hexDigits[b >>> 4 & 0xF];
                result[k++] = hexDigits[b & 0xF];
            }
            return new String(result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void main(String[] args)
    {
        System.out.print(Md5Utils.encode("123"));
    }
}
