package testRedis;

import redis.clients.jedis.JedisPubSub;
/*
 * ��Ϣ��������
 */
public class RedisMsgPubSubListener extends JedisPubSub{

	@Override
	public void onMessage(String channel, String message) {
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
