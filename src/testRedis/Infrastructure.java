package testRedis;

import redis.clients.jedis.Jedis;

public class Infrastructure {

	public static void main(String[] args) {
		/*
		 * ����VM��Ubuntu�� Redis ���񣬵����ļ����Ӳ���
		 */
//		Jedis jedis = new Jedis("192.168.65.130", 6379);// ��ipΪUbuntu��ip��ַ
//		jedis.auth("redis");// redis-cli�ķ�������
//		System.out.println("Connection to server sucessfully");
//		// �鿴�����Ƿ�����
//		System.out.println("Server is running: " + jedis.ping());
//		// �������ݿ��������
//		jedis.set("key6", "redis test");
//		jedis.set("key7", "������");//������Ubuntu��û������ʾ
//		String string = jedis.get("key7");
//		String string1 = jedis.get("key6");
//		System.out.println(string + " " + string1);
		
		/*
		 * jedisPool
		 */
		new RedisClient().show();
	}
	// Ȼ��ȥUbuntu�£��������get key6
	// ���ն˽��������jedis test6
	// ˵��Զ�����ӡ��������ݿ�ɹ���
}
