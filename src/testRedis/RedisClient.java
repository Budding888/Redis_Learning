package testRedis;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.SortingParams;

public class RedisClient {

	// Redis������IP��Ŀǰ������Ubuntuϵͳ��
	private static String ADDR = "192.168.65.130";
	
	// Redis�Ķ˿ں�
	private static int PORT = 6379;
	private static int PORT2 = 6380;// ����ShardedJedisPool
	
	// �������룬ͨ��redis.conf����
	private static String PASSWORD = "redis";
	
	/* 
	 * MAX_ACTIVE:��������ʵ���������Ŀ��Ĭ��ֵΪ8�������ֵΪ-1�����ʾ�����ƣ�
	 * ���pool�Ѿ�������maxActive��jedisʵ�������ʱpool��״̬Ϊexhausted(�ľ�)��
	*/
	private static int MAX_ACTIVE = 1024;
	
	// ����һ��pool����ж��ٸ�״̬Ϊidle(���е�)��jedisʵ����Ĭ��ֵҲ��8.
	private static int MAX_IDLE = 200;
	
	// �ȴ��������ӵ����ʱ�䣬��λ���룬Ĭ��ֵΪ-1����ʾ������ʱ��
	// ��������ȴ�ʱ�䣬��ֱ���׳�
	private static int MAX_WAIT = 10000;
	
	// ��borrow(����)һ��jedisʵ��ʱ���Ƿ���ǰ����validate������
	// ���Ϊtrue����õ���jedisʵ�����ǿ��õģ�
	private static boolean TEST_ON_BORROW = true;
	private static int TIMEOUT = 10000;

	private Jedis jedis;
	private JedisPool jedisPool;// ����Ƭ���ӳأ����˿ڣ�����ʹ��
	private ShardedJedis shardedJedis;
	private ShardedJedisPool shardedJedisPool;// shard��Ƭ���ӳأ���˿ڣ��ֲ�ʽʹ��

	public RedisClient() {
		initialPool();
		initialShardedPool();
		shardedJedis = shardedJedisPool.getResource();
		jedis = jedisPool.getResource();
	}

	/**
	 * ��ʼ������Ƭ��
	 */
	private void initialPool() {
		// �ػ�������
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxActive(MAX_ACTIVE);
		config.setMaxIdle(MAX_IDLE);
		config.setMaxWait(MAX_WAIT);
		config.setTestOnBorrow(TEST_ON_BORROW);

		jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT, PASSWORD);
	}

	/**
	 * ��ʼ����Ƭ��
	 */
	private void initialShardedPool() {
		// �ػ�������
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxActive(20);
		config.setMaxIdle(5);
		config.setMaxWait(1000l);
		config.setTestOnBorrow(false);

		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		JedisShardInfo infoA = new JedisShardInfo(ADDR, PORT);
		infoA.setPassword("redis");
		shards.add(infoA);
//		���ǲ���
//		JedisShardInfo infoB = new JedisShardInfo(ADDR, PORT2);
//		infoB.setPassword("redis");
//		shards.add(infoB);
//		shards = Arrays.asList(infoA,infoB);
		shardedJedisPool = new ShardedJedisPool(config, shards);
	}

	public void show() {
//		SomeOperate.KeyOperate(jedis,shardedJedis);
//		SomeOperate.StringOperate(jedis,shardedJedis);
//		SomeOperate.ListOperate(jedis,shardedJedis);
//		SomeOperate.SetOperate(jedis,shardedJedis);
//		SomeOperate.SortedSetOperate(jedis,shardedJedis);
		SomeOperate.HashOperate(jedis,shardedJedis);
		// jedis��ȡ��һ��Ҫ�رգ��������ʹ�����ݿ����ӳ���һ���ģ�
		// ����finally���б�֤jedis�Ĺر�.
		// Jedis3.0��returnResource�Ͳ�ʹ���ˣ�������close�滻
		try {
			jedis = jedisPool.getResource();
			shardedJedis = shardedJedisPool.getResource();
		} catch (Exception e) {
			jedisPool.returnBrokenResource(jedis);
			shardedJedisPool.returnBrokenResource(shardedJedis);
			e.printStackTrace();
		} finally {
			if (null != jedisPool && null != shardedJedisPool) {
				jedisPool.returnResource(jedis);
				shardedJedisPool.returnResource(shardedJedis);
			}
		}

	}
}