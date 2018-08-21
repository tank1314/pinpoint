package com.navercorp.pinpoint.plugin.rocketmq.client.secret;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


/**
 * 加密过程为
 *  key为16位的16进制编码
 *  得到AES加密后的数组
 *  将数组转成BASE64字符中输出
 * qiang.hu依据周小洲的代码改善而成
 *  AES详细：
 *     AES加密模式：ECB
 *     填充方式：pkcs5padding/pkcs7padding
 *     字符集：utf8
 */
public class AESGeneralUtil
{
	
	/**
	 * 加密，password为16位md5，这个要在线加密后，双方约定 password
	 * 
	 * @param content 需要加密的内容
	 * @param password 加密密码
	 * @return
	 */
	public static String encrypt(String content, String password)
	{
		try
		{
			
			SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");// enCodeFormat的长度必须为16否则报错
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(byteContent);
			return BASE64Util.encode(result); // 加密
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchPaddingException e)
		{
			e.printStackTrace();
		}
		catch (InvalidKeyException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (IllegalBlockSizeException e)
		{
			e.printStackTrace();
		}
		catch (BadPaddingException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 解密
	 * 
	 * @param content 待解密内容
	 * @param password 解密密钥--一定要16位长度
	 * @return
	 */
	public static String decrypt(String content, String password) throws Exception
	{
		try
		{
			byte[] contentByte = BASE64Util.decode(content);
			SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(contentByte);
			return new String(result); // 加密
		}
		catch (Exception e)
		{
			throw new Exception(e.getMessage());
		}
	}
	
	/*public static void main(String[] args) throws Exception {
		
		ExecutorService fixThreadPool = Executors.newFixedThreadPool(20);
		for (int i = 0; i < 1000; i++)
		{
			final int index = i;
			fixThreadPool.execute(new Runnable()
			{
				public void run()
				{
					try
					{
						String source = "{'beanName':'monitorAction','methodName':'queryWmsOutBoundOrder'}";
			            System.out.println("原文：" + source);
			            String key = "tmall_oms";// 要为16位md5加密的字符串
			            key = MD5Util.getMD5_16(key);
			            System.out.println("密钥:"+key);
			            String result = encrypt(source, key);
			            System.out.println("加密:" + result);
			            System.out.println("解密:" + decrypt(result, key));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
		}
	}*/
}
