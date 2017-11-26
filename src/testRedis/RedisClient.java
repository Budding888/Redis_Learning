package testRedis;

import java.util.List;
import java.util.ArrayList;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;

public class RedisClient {

	// Redis��������IP��Ŀǰ������Ubuntuϵͳ��
	private static String ADDR = "192.168.65.130";
	// Redis�ӷ�����IP��Ŀǰ������RedHatϵͳ��(�����ķ�����������)
	private static String SUB_ADDR = "192.168.65.130";

	// Redis�Ķ˿ں�
	private static int PORT = 6379;
	private static int PORT2 = 6379;// ����ShardedJedisPool

	// �������룬ͨ��redis.conf����
	private static String PASSWORD = "redis";

	/*
	 * MAX_ACTIVE:��������ʵ���������Ŀ��Ĭ��ֵΪ8�������ֵΪ-1�����ʾ�����ƣ�
	 * ���pool�Ѿ�������maxActive��jedisʵ�������ʱpool��״̬Ϊexhausted(�ľ�)��
	 */
	private static int MAX_ACTIVE = 1024;

	// ����һ��pool����ж��ٸ�״̬Ϊidle(���е�)��jedisʵ����Ĭ��ֵҲ��8.
	private static int MAX_IDLE = 200;
	// ���������, Ĭ��8��
	private static int MAX_TOTAL = 2;
	// �ȴ��������ӵ����ʱ�䣬��λ���룬Ĭ��ֵΪ-1����ʾ������ʱ��
	// ��������ȴ�ʱ�䣬��ֱ���׳�
	private static int MAX_WAIT = 10000;

	// ��borrow(����)һ��jedisʵ��ʱ���Ƿ���ǰ����validate������
	// ���Ϊtrue����õ���jedisʵ�����ǿ��õģ�
	private static boolean TEST_ON_BORROW = true;
	private static int TIMEOUT = 10000;

	private Jedis jedis;
	// ע��Jedis���󲢲����̰߳�ȫ�ģ��ڶ��߳���ʹ��ͬһ��Jedis�������ֲ������⡣
	// Ϊ�˱���ÿ��ʹ��Jedis����ʱ����Ҫ���¹�����Jedis�ṩ��JedisPool
	private JedisPool jedisPool;
	private ShardedJedis shardedJedis;
	private ShardedJedisPool shardedJedisPool;// shard��Ƭ���ӳأ���˿ڣ��ֲ�ʽʹ��

	public RedisClient() {
		initialPool();
		initialShardedPool();
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
		// ���ǲ��� ��ʱ���������ķ������������ӷ���������
		// JedisShardInfo infoB = new JedisShardInfo(SUB_ADDR, PORT2);
		// infoB.setPassword("redis");
		// shards.add(infoB);
		// shards = Arrays.asList(infoA,infoB);
		shardedJedisPool = new ShardedJedisPool(config, shards,
				Hashing.MURMUR_HASH, ShardedJedis.DEFAULT_KEY_TAG_PATTERN);
	}

	public void show() {
		// jedis��ȡ��һ��Ҫ�رգ��������ʹ�����ݿ����ӳ���һ���ģ�
		// ����finally���б�֤jedis�Ĺر�.
		// Jedis3.0��returnResource�Ͳ�ʹ���ˣ�������close�滻
		/*
		 * ���Ե������� 2017/11/22
		 */
		try {
			jedis = jedisPool.getResource();//��ʼ��Jedis���󲢲�����Redis Server�������ӣ����ӷ����ڵ�һ��ִ������ʱ��
			shardedJedis = shardedJedisPool.getResource();
			SomeOperate.testPipeLineAndNormal(jedis);
//			SomeOperate.PipelineTransactions(jedis, jedisPool);
//			SomeOperate.KeyOperate(jedis, shardedJedis);
//			SomeOperate.StringOperate(jedis, shardedJedis);
//			SomeOperate.ListOperate(jedis, shardedJedis);
//			SomeOperate.SetOperate(jedis, shardedJedis);
//			SomeOperate.SortedSetOperate(jedis, shardedJedis);
//			SomeOperate.HashOperate(jedis, shardedJedis);
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);// �������쳣ʱ Ҫ���ٶ���
			shardedJedisPool.returnBrokenResource(shardedJedis);
			e.printStackTrace();
		} finally {
			if (null != jedisPool && null != shardedJedisPool) {
				jedisPool.returnResource(jedis);// �����Jedisʵ���黹��JedisPool��
				shardedJedisPool.returnResource(shardedJedis);
			}
		}

		/*
		 * ���Զ������ 2017/11/24 �����ķ�����
		 */
		// for (int i = 0; i < 100; i++) {
		//
		// String key = generateKey();
		// // key += "{aaa}";
		// ShardedJedis jds = null;
		// try {
		// jds = shardedJedisPool.getResource();
		// System.out.println(key + ":"
		// + jds.getShard(key).getClient().getHost());
		// System.out.println(jds.set(key,
		// "1111111111111111111111111111111"));
		// } catch (Exception e) {
		// shardedJedisPool.returnBrokenResource(jds);
		// e.printStackTrace();
		// } finally {
		// shardedJedisPool.returnResource(jds);
		// }
		// }
	}

	private static int index = 1;

	private String generateKey() {
		return String.valueOf(Thread.currentThread().getId()) + "_" + (index++);
	}
}