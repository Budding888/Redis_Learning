package testRedis;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.SortingParams;

public class SomeOperate {

	/*
	 * 2017/11/25 ����pipeline��redis����������
	 */
	public static void PipelineTransactions(Jedis jedis, JedisPool jedisPool) {
		try {
			Pipeline pipeLine = jedis.pipelined();
			pipeLine.set("value", "100");
			pipeLine.watch("value");
			pipeLine.multi();// ��������
			pipeLine.incrBy("value", 10);// ����10
			// �Դ������������ʹ���˲�֧�ֵĲ���
			pipeLine.lpush("value", "error");//ִ�д���Ĳ���lpush
			pipeLine.incrBy("value", 10);// �ٴε���10
			// ִ��exec����,��ȡ"δ��"�ķ��ؽ��
			Response<List<Object>> listResponse = pipeLine.exec();
			pipeLine.sync();// ����pipeling
			List<Object> result = listResponse.get();
			if (result != null && result.size() > 0) {
				for (Object o : result)
					System.out.println(o.toString());
			}
			// ��Ȼ�����еڶ�������ʧ����,����Ӱ��value��ֵ
			System.out.println("\nvalue is " + jedis.get("value"));
		} catch (Exception e) {
			// jedisPool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/*
	 * ���õļ�����
	 */
	public static void KeyOperate(Jedis jedis, ShardedJedis shardedJedis) {

		System.out.println("==================key======================");

		// flushDB():ɾ����ǰѡ�����ݿ��е�����key
		// flushall()��ɾ���������ݿ��е�����key
		System.out.println("��տ����������ݣ�" + jedis.flushDB());

		// exists(key):�ж�key�����
		System.out.println("�ж�key999���Ƿ���ڣ�" + shardedJedis.exists("key999"));
		System.out.println("������ֵ�ԣ�" + shardedJedis.set("key001", "value001"));
		System.out.println("�ж�key001�Ƿ���ڣ�" + shardedJedis.exists("key001"));
		System.out.println("������ֵ�ԣ�" + shardedJedis.set("key002", "value002"));

		System.out.println("ϵͳ�����м����£�");
		// keys(pattern)�������������pattern������key
		Set<String> keys = jedis.keys("*");
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			System.out.println(key);
		}

		// del(key):ɾ��ĳ��key,��key�����ڣ�����Ը����
		// Problem1��Ϊʲôɾ����Jedis�ģ���shardedJedis��Ҳɾ���ˣ�����������������������������
		System.out.println("ϵͳ��ɾ��key002: " + jedis.del("key002"));
		System.out.println("�ж�key002�Ƿ���ڣ�" + shardedJedis.exists("key002"));

		// expire(key,time)���趨һ��key�Ļʱ�䣨s��
		System.out.println("���� key001�Ĺ���ʱ��Ϊ5��:" + jedis.expire("key001", 5));

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}

		// ttl(key):�鿴ĳ��key��ʣ������ʱ��,��λ���롿����������߲����ڵĶ�����-1
		System.out.println("�鿴key001��ʣ������ʱ�䣺" + jedis.ttl("key001"));
		// �Ƴ�ĳ��key������ʱ��
		System.out.println("�Ƴ�key001������ʱ�䣺" + jedis.persist("key001"));
		System.out.println("�鿴key001��ʣ������ʱ�䣺" + jedis.ttl("key001"));

		// Problem2�����һ���������ˣ��������ڣ���������������
		System.out.println("����������" + jedis.get("key001"));
		// rename(oldname, newname)��������key
		System.out.println("��������" + jedis.rename("key001", "newkey001"));

		// type(key)�鿴key�������ֵ������
		System.out.println("�鿴key�������ֵ�����ͣ�" + jedis.type("newkey001"));
	}

	/*
	 * ���õ�String���Ͳ���
	 */
	public static void StringOperate(Jedis jedis, ShardedJedis shardedJedis) {

		System.out.println("==================String_1======================");
		System.out.println("��տ����������ݣ�" + jedis.flushDB());

		System.out.println("=============��=============");
		jedis.set("key001", "value001");
		jedis.set("key002", "value002");
		jedis.set("key003", "value003");
		System.out.println("��������3����ֵ�����£�");
		System.out.println("key001 -> " + jedis.get("key001"));
		System.out.println("key002 -> " + jedis.get("key002"));
		System.out.println("key003 -> " + jedis.get("key003"));

		System.out.println("=============ɾ=============");
		System.out.println("ɾ��key003��ֵ�ԣ�" + jedis.del("key003"));
		System.out.println("��ȡkey003����Ӧ��ֵ��" + jedis.get("key003"));

		System.out.println("=============��=============");
		// 1��ֱ�Ӹ���ԭ��������
		System.out.println("����key001ԭ�������ݣ�"
				+ jedis.set("key001", "value001-update"));
		System.out.println("��ȡkey001��Ӧ����ֵ��" + jedis.get("key001"));
		// 2��append(key, value)������Ϊkey��string��ֵ����value
		System.out.println("��key002ԭ��ֵ����׷�ӣ�"
				+ jedis.append("key002", "+appendString"));
		System.out.println("��ȡkey002��Ӧ����ֵ" + jedis.get("key002"));

		System.out.println("=============����ɾ���飨�����=============");
		/**
		 * mset(key N, value N)���������ö��string��ֵ mget(key1, key2,��,
		 * keyN)�����ؿ��ж��string��value
		 */
		System.out.println("һ��������key201,key202,key203,key204�����Ӧֵ��"
				+ jedis.mset("key201", "value201", "key202", "value202",
						"key203", "value203", "key204", "1"));

		System.out.println("һ���Ի�ȡkey201,key202,key203,key204���Զ�Ӧ��ֵ��"
				+ jedis.mget("key201", "key202", "key203", "key204"));

		System.out.println("һ����ɾ��key201,key202��"
				+ jedis.del(new String[] { "key201", "key202" }));

		System.out.println("һ���Ի�ȡkey201,key202,key203,key204���Զ�Ӧ��ֵ��"
				+ jedis.mget("key201", "key202", "key203", "key204"));
		// incr(key)������Ϊkey��string��1����; decr(key)������Ϊkey��string��1����
		// incrby(key, integer)������Ϊkey��string����integer
		System.out.println("key204��ֵ��" + jedis.incr("key204"));
		System.out.println();

		// jedis�߱��Ĺ���shardedJedis��Ҳ��ֱ��ʹ��
		System.out.println("==================String_2=====================");
		System.out.println("��տ����������ݣ�" + jedis.flushDB());

		System.out.println("=============������ֵ��ʱ��ֹ����ԭ��ֵ============");
		System.out.println("ԭ��key301������ʱ������key301��"
				+ shardedJedis.setnx("key301", "value301"));
		System.out.println("ԭ��key302������ʱ������key302��"
				+ shardedJedis.setnx("key302", "value302"));
		System.out.println("��key302����ʱ����������key302��"
				+ shardedJedis.setnx("key302", "value302_new"));
		System.out.println("��ȡkey301��Ӧ��ֵ��" + shardedJedis.get("key301"));
		System.out.println("��ȡkey302��Ӧ��ֵ��" + shardedJedis.get("key302"));

		System.out.println("=============������Ч�ڼ�ֵ�Ա�ɾ��=============");
		// setex(key, time, value)����������string���趨����ʱ��time
		System.out.println("����key303����ָ������ʱ��Ϊ2��"
				+ shardedJedis.setex("key303", 2, "key303-2second"));
		System.out.println("��ȡkey303��Ӧ��ֵ��" + shardedJedis.get("key303"));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		System.out.println("3��֮�󣬻�ȡkey303��Ӧ��ֵ��" + shardedJedis.get("key303"));

		System.out.println("===========��ȡԭֵ������Ϊ��ֵһ�����============");
		System.out.println("key302ԭֵ��"
				+ shardedJedis.getSet("key302", "value302-after-getset"));
		System.out.println("key302��ֵ��" + shardedJedis.get("key302"));

		System.out.println("=============��ȡ�Ӵ�=============");
		System.out.println("��ȡkey302��Ӧֵ�е��Ӵ���"
				+ shardedJedis.getrange("key302", 5, 7));
	}

	/*
	 * ���õ�List���Ͳ���
	 */
	public static void ListOperate(Jedis jedis, ShardedJedis shardedJedis) {

		System.out.println("===================list=======================");
		System.out.println("��տ����������ݣ�" + jedis.flushDB());

		System.out.println("=============��=============");
		// rpush(key, value)��������Ϊkey��listβ���һ��ֵΪvalue��Ԫ��
		// lpush(key, value)��������Ϊkey��listͷ���һ��ֵΪvalue��Ԫ��
		shardedJedis.lpush("stringlists", "vector");
		shardedJedis.lpush("stringlists", "ArrayList");
		shardedJedis.lpush("stringlists", "vector");
		shardedJedis.lpush("stringlists", "vector");
		shardedJedis.lpush("stringlists", "LinkedList");
		shardedJedis.lpush("stringlists", "MapList");
		shardedJedis.lpush("stringlists", "SerialList");
		shardedJedis.lpush("stringlists", "HashList");
		shardedJedis.rpush("stringlists", "TestList");
		shardedJedis.lpush("numberlists", "3");
		shardedJedis.lpush("numberlists", "1");
		shardedJedis.lpush("numberlists", "5");
		shardedJedis.lpush("numberlists", "2");

		// lrange(key, start, end)����������Ϊkey��list��start��end֮���Ԫ��
		System.out.println("����Ԫ��-stringlists��"
				+ shardedJedis.lrange("stringlists", 0, -1));
		System.out.println("����Ԫ��-numberlists��"
				+ shardedJedis.lrange("numberlists", 0, -1));

		System.out.println("=============ɾ=============");
		// lrem(key, count, value)��ɾ��count��key��list��ֵΪvalue��Ԫ��
		// ���ڶ�������Ϊɾ���ĸ���,�����ظ�ʱ����add��ȥ��ֵ�ȱ�ɾ�������ڳ�ջ
		System.out.println("�ɹ�ɾ��ָ��Ԫ�ظ���-stringlists��"
				+ shardedJedis.lrem("stringlists", 2, "vector"));
		System.out.println("ɾ��ָ��Ԫ��֮��-stringlists��"
				+ shardedJedis.lrange("stringlists", 0, -1));
		// ��ȡָ�����������
		System.out.println("ɾ���±�0-3����֮���Ԫ�أ�"
				+ shardedJedis.ltrim("stringlists", 0, 3));
		System.out.println("ɾ��ָ������֮��Ԫ�غ�-stringlists��"
				+ shardedJedis.lrange("stringlists", 0, -1));
		// lpop(key)�����ز�ɾ������Ϊkey��list�е���Ԫ��
		// rpop(key)�����ز�ɾ������Ϊkey��list�е�βԪ��
		System.out.println("��ջԪ�أ�" + shardedJedis.lpop("stringlists"));
		System.out.println("Ԫ�س�ջ��-stringlists��"
				+ shardedJedis.lrange("stringlists", 0, -1));

		System.out.println("=============��=============");
		// �޸��б���ָ���±��ֵ
		shardedJedis.lset("stringlists", 0, "hello list!");
		System.out.println("�±�Ϊ0��ֵ�޸ĺ�-stringlists��"
				+ shardedJedis.lrange("stringlists", 0, -1));

		System.out.println("=============��=============");
		// llen(key)����������Ϊkey��list�ĳ���
		System.out
				.println("����-stringlists��" + shardedJedis.llen("stringlists"));
		System.out
				.println("����-numberlists��" + shardedJedis.llen("numberlists"));
		// lindex(key, index)����������Ϊkey��list��indexλ�õ�Ԫ��
		System.out.println("stringlists�е�����Ԫ�أ� "
				+ shardedJedis.lindex("stringlists", 2));

		// ����
		/*
		 * list�д��ַ���ʱ����ָ������Ϊalpha�������ʹ��SortingParams������ֱ��ʹ��sort("list")��
		 * �����"ERR One or more scores can't be converted into double"
		 */
		SortingParams sortingParameters = new SortingParams();
		sortingParameters.alpha();// ���ֵ�������
		// limit(int start, int count) ���Ʒ���Ԫ�صĸ���
		sortingParameters.limit(0, 3);
		System.out.println("���������Ľ��-stringlists��"
				+ shardedJedis.sort("stringlists", sortingParameters));
		System.out.println("���������Ľ��-numberlists��"
				+ shardedJedis.sort("numberlists"));
		// �Ӵ��� startΪԪ���±꣬endҲΪԪ���±ꣻ-1������һ��Ԫ�أ�-2�������ڶ���Ԫ��
		// ע�⣺��Ȼ�Ƚ�����������������˴���Ȼ�������ԭ���Ĵ洢˳��
		System.out.println("�Ӵ�-�ڶ�����ʼ��������"
				+ shardedJedis.lrange("stringlists", 1, -1) + "\n");
	}

	/*
	 * ���õ�Set���Ͳ���
	 */
	public static void SetOperate(Jedis jedis, ShardedJedis shardedJedis) {
		System.out.println("================set=================");
		System.out.println("��տ����������ݣ�" + jedis.flushDB());

		System.out.println("=============��=============");
		System.out.println("��sets�����м���Ԫ��element001��"
				+ jedis.sadd("sets", "element001"));
		System.out.println("��sets�����м���Ԫ��element002��"
				+ jedis.sadd("sets", "element002"));
		System.out.println("��sets�����м���Ԫ��element003��"
				+ jedis.sadd("sets", "element003"));
		System.out.println("��sets�����м���Ԫ��element004��"
				+ jedis.sadd("sets", "element004"));
		System.out.println("�鿴sets�����е�����Ԫ��:" + jedis.smembers("sets"));
		System.out.println();

		System.out.println("=============ɾ=============");
		System.out.println("����sets��ɾ��Ԫ��element003��"
				+ jedis.srem("sets", "element003"));
		// smembers(key) ����������Ϊkey��set������Ԫ��
		System.out.println("�鿴sets�����е�����Ԫ��:" + jedis.smembers("sets"));
		/*
		 * System.out.println("sets����������λ�õ�Ԫ�س�ջ��"+jedis.spop("sets")); --��ʵ������
		 * System.out.println("�鿴sets�����е�����Ԫ��:"+jedis.smembers("sets"));
		 */
		System.out.println();

		System.out.println("=============��=============");
		// sismember(key, member) ��member�Ƿ�������Ϊkey��set��Ԫ��
		System.out.println("�ж�element001�Ƿ��ڼ���sets�У�"
				+ jedis.sismember("sets", "element001"));
		// scard(key) ����������Ϊkey��set�Ļ���
		System.out.println("������ " + jedis.scard("sets"));
		System.out.println("ѭ����ѯ��ȡsets�е�ÿ��Ԫ�أ�");
		Set<String> set = jedis.smembers("sets");
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			System.out.println(obj);
		}
		System.out.println();

		System.out.println("=============��������=============");
		System.out.println("sets1�����Ԫ��element001��"
				+ jedis.sadd("sets1", "element001"));
		System.out.println("sets1�����Ԫ��element002��"
				+ jedis.sadd("sets1", "element002"));
		System.out.println("sets1�����Ԫ��element003��"
				+ jedis.sadd("sets1", "element003"));

		System.out.println("sets2�����Ԫ��element002��"
				+ jedis.sadd("sets2", "element002"));
		System.out.println("sets2�����Ԫ��element003��"
				+ jedis.sadd("sets2", "element003"));
		System.out.println("sets2�����Ԫ��element004��"
				+ jedis.sadd("sets2", "element004"));

		System.out.println("�鿴sets1�����е�����Ԫ��:" + jedis.smembers("sets1"));
		System.out.println("�鿴sets2�����е�����Ԫ��:" + jedis.smembers("sets2"));
		System.out.println("sets1��sets2������" + jedis.sinter("sets1", "sets2"));
		System.out.println("sets1��sets2������" + jedis.sunion("sets1", "sets2"));
		// ��A��B���������ϣ�����������A�Ҳ�����B��Ԫ�ع��ɵļ���,Ϊ�����
		System.out.println("sets1��sets2���" + jedis.sdiff("sets1", "sets2"));
	}

	/*
	 * ���õ�SortedSet���Ͳ���
	 */
	public static void SortedSetOperate(Jedis jedis, ShardedJedis shardedJedis) {
		System.out.println("====================zset======================");
		System.out.println(jedis.flushDB());

		System.out.println("=============��=============");
		System.out.println("zset�����Ԫ��element001��"
				+ shardedJedis.zadd("zset", 7.0, "element001"));
		System.out.println("zset�����Ԫ��element002��"
				+ shardedJedis.zadd("zset", 8.0, "element002"));
		System.out.println("zset�����Ԫ��element003��"
				+ shardedJedis.zadd("zset", 2.0, "element003"));
		System.out.println("zset�����Ԫ��element004��"
				+ shardedJedis.zadd("zset", 3.0, "element004"));
		System.out
				.println("zset�����е�����Ԫ�أ�" + shardedJedis.zrange("zset", 0, -1));// ����Ȩ��ֵ����
		System.out.println();

		System.out.println("=============ɾ=============");
		System.out.println("zset��ɾ��Ԫ��element002��"
				+ shardedJedis.zrem("zset", "element002"));
		System.out
				.println("zset�����е�����Ԫ�أ�" + shardedJedis.zrange("zset", 0, -1));
		System.out.println();

		System.out.println("=============��=============");
		System.out.println("ͳ��zset�����е�Ԫ���и�����" + shardedJedis.zcard("zset"));
		System.out.println("ͳ��zset������Ȩ��ĳ����Χ�ڣ�1.0����5.0����Ԫ�صĸ�����"
				+ shardedJedis.zcount("zset", 1.0, 5.0));
		System.out.println("�鿴zset������element004��Ȩ�أ�"
				+ shardedJedis.zscore("zset", "element004"));
		System.out.println("�鿴�±�1��2��Χ�ڵ�Ԫ��ֵ��"
				+ shardedJedis.zrange("zset", 1, 2));
	}

	/*
	 * ���õ�Hash���Ͳ���
	 */
	public static void HashOperate(Jedis jedis, ShardedJedis shardedJedis) {

		System.out.println("================hash===================");
		System.out.println(jedis.flushDB());

		System.out.println("=============��=============");
		// hset(key, field, value)��������Ϊkey��hash�����Ԫ��field
		System.out.println("hashs�����key001��value001��ֵ�ԣ�"
				+ shardedJedis.hset("hashs", "key001", "value001"));
		System.out.println("hashs�����key002��value002��ֵ�ԣ�"
				+ shardedJedis.hset("hashs", "key002", "value002"));
		System.out.println("hashs�����key003��value003��ֵ�ԣ�"
				+ shardedJedis.hset("hashs", "key003", "value003"));
		// hincrby(key,field,integer)��������Ϊkey��hash��field��value����integer
		System.out.println("����key004��4�����ͼ�ֵ�ԣ�"
				+ shardedJedis.hincrBy("hashs", "key004", 4l));
		// hvals(key)����������Ϊkey��hash�����м���Ӧ��value
		System.out.println("hashs�е�����ֵ��" + shardedJedis.hvals("hashs"));
		System.out.println();

		System.out.println("=============ɾ=============");
		System.out.println("hashs��ɾ��key002��ֵ�ԣ�"
				+ shardedJedis.hdel("hashs", "key002"));
		System.out.println("hashs�е�����ֵ��" + shardedJedis.hvals("hashs"));
		System.out.println();

		System.out.println("=============��=============");
		System.out.println("key004���ͼ�ֵ��ֵ����100��"
				+ shardedJedis.hincrBy("hashs", "key004", 100l));
		System.out.println("hashs�е�����ֵ��" + shardedJedis.hvals("hashs"));
		System.out.println();

		System.out.println("=============��=============");
		System.out.println("�ж�key003�Ƿ���ڣ�"
				+ shardedJedis.hexists("hashs", "key003"));
		System.out.println("��ȡkey004��Ӧ��ֵ��"
				+ shardedJedis.hget("hashs", "key004"));
		// hlen(key)����������Ϊkey��hash��Ԫ�ظ���
		System.out.println("hashs��Ԫ�ظ�����" + shardedJedis.hlen("hashs"));
		System.out.println("������ȡkey001��key003��Ӧ��ֵ��"
				+ shardedJedis.hmget("hashs", "key001", "key003"));
		System.out.println("��ȡhashs�����е�key��" + shardedJedis.hkeys("hashs"));
		System.out.println("��ȡhashs�����е�value��" + shardedJedis.hvals("hashs"));
		// hgetAll(key)����������Ϊkey��hash�����еļ���field�������Ӧ��value
		System.out.println("��ȡhashs�����е�key-value��"
				+ shardedJedis.hgetAll("hashs"));
		System.out.println();
	}
}
