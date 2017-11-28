package testRedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPubSub;
/*
 * ��Ϣ�������࣬�̳���JedisPubSub����ʵ������󷽷�
 */
public class RedisMsgPubSubListener extends JedisPubSub{

	private static Logger logger = LoggerFactory.getLogger(RedisMsgPubSubListener.class);
	@Override
	public void onMessage(String channel, String message) {
		logger.info("Message received. Channel: {}, Msg: {}", channel, message);
		System.out.println("channel:" + channel + "receives message :" + message);
		// ���Ը������ú�ʱȡ������
        // this.unsubscribe(); // ���ע�͵�����յ�������Ϣ�������5��
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {
		
	}

	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) {
		
	}

	@Override
	public void onPUnsubscribe(String pattern, int subscribedChannels) {
		
	}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		System.out.println("channel:" + channel + "is been subscribed:" + subscribedChannels);  
	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		System.out.println("channel:" + channel + "is been unsubscribed:" + subscribedChannels);  
	}

}
