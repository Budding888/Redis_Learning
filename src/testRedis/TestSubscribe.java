package testRedis;

import org.junit.Test;

import redis.clients.jedis.Jedis;
/*
 * ����ʵ�ֶ�Ƶ��redisChatTest�Ķ��ļ�����Ƶ���Ķ��ģ�ȡ�����ģ��յ���Ϣ�������listener����
 * �Ķ�Ӧ������ע�⣺subscribe��һ�������ķ�������ȡ�����ĸ�Ƶ��ǰ����һֱ�������⣬
 * ֻ�е�ȡ���˶��ĲŻ�ִ�������other code�����ԣ�����onMessage�����յ���Ϣ��
 * ������this.unsubscribe(); ��ȡ�����ģ������Ż�ִ�к����other code
 */
public class TestSubscribe {

	@Test
	public void testSubscribe() throws Exception{
		Jedis jedis = new Jedis("192.168.65.130", 6379); 
		jedis.auth("redis");
        RedisMsgPubSubListener listener = new RedisMsgPubSubListener();  
        jedis.subscribe(listener, "redisChatTest");  
        // other code
	}
}
