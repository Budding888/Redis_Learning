package testRedis;

import org.junit.Test;

import redis.clients.jedis.Jedis;
/*
 * �������Ƶ��redisChatTest������Ϣ����Ϊ�����˸�Ƶ�������Ի��յ�����Ϣ��
 */
public class TestPublish {

	@Test
	public void testPublish() throws Exception {
		Jedis jedis = new Jedis("192.168.65.130", 6379);
		jedis.auth("redis");
        jedis.publish("redisChatTest", "�������");  
        Thread.sleep(5000);  
        jedis.publish("redisChatTest", "��ţ��");  
        Thread.sleep(5000);  
        jedis.publish("redisChatTest", "����");
	}
}
