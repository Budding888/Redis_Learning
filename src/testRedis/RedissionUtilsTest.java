package testRedis;

import java.util.Arrays;  
import java.util.Collection;  
import java.util.concurrent.ExecutionException;  
import java.util.concurrent.TimeUnit;  

import org.junit.After;  
import org.junit.Before;  
import org.junit.Test;  
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RDeque;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RQueue;
import org.redisson.api.RSet;
import org.redisson.api.RSortedSet;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;

public class RedissionUtilsTest {

	RedissonClient redisson;  
	  
    /** 
     * ÿ���ڲ��Է�������֮ǰ ���д˷��� 
     * �����ͻ������ӷ�������redisson���� 
     */  
    @Before  
    public void before() {  
        String ip = "192.168.65.130";  
        String port = "6379";  
        redisson = RedissionUtils.getInstance().getRedisson(ip, port);  
    }  
  
    /** 
     * ÿ�β��Է���������֮�� ���д˷��� 
     * ���ڹرտͻ������ӷ�������redisson���� 
     */  
    @After  
    public void after(){  
        RedissionUtils.getInstance().closeRedisson(redisson);  
    }  
      
    /** 
     * RBucket ӳ��Ϊ redis server �� string ���� 
     * ֻ�ܴ�����洢��һ���ַ��� 
     * redis server ����: 
     * �鿴���м�---->keys * 
     * �鿴key������--->type testBucket 
     * �鿴key��ֵ ---->get testBucket 
     */  
    @Test  
    public void testGetRBucket() {  
        RBucket<String> rBucket=RedissionUtils.getInstance().getRBucket(redisson, "testBucket");  
        //ͬ������  
        rBucket.set("redisBucketASync");  
        //�첽����  
        rBucket.setAsync("����");  
        String bucketString=rBucket.get();  
        System.out.println(bucketString);  
    }  
  
    /** 
     * RMap  ӳ��Ϊ  redis server �� hash ���� 
     * ��Ϊ 
     * put(���ؼ�ֵ) �� fast(����״̬) 
     * ͬ��    �첽 
     * redis server ����: 
     * �鿴���м�---->keys * 
     * �鿴key������--->type testMap 
     * �鿴key��ֵ ---->hgetall testMap 
     * @throws InterruptedException 
     * @throws ExecutionException 
     */  
    @Test  
    public void testGetRMap() throws InterruptedException, ExecutionException {  
        RMap<String, Integer> rMap=RedissionUtils.getInstance().getRMap(redisson, "testMap");  
        //�������  
        rMap.clear();  
        //���key-value ����֮ǰ��������ֵ  
        Integer firrtInteger=rMap.put("111", 111);  
        System.out.println(firrtInteger);  
        //���key-value ����֮ǰ��������ֵ  
        Integer secondInteger=rMap.putIfAbsent("222", 222);  
        System.out.println(secondInteger);  
        //�Ƴ�key-value  
        Integer thirdInteger=rMap.remove("222");  
        System.out.println(thirdInteger);  
        //���key-value ������֮ǰ��������ֵ  
        boolean third=rMap.fastPut("333", 333);  
        System.out.println(third);   
        //��������  
        for(String key :rMap.keySet()){  
            System.out.println(key+":"+rMap.get(key));  
        }  
          
    }  
  
    /** 
     * RSortedSet ӳ��Ϊ redis server �� list ���� 
     * �洢�����򼯺ϵ���ʽ��� 
     *  redis server ����: 
     * �鿴���м�---->keys * 
     * �鿴key������--->type testSortedSet 
     * �鿴key��ֵ ---->lrange testSortedSet 0 10 
     */  
    @Test  
    public void testGetRSortedSet() {  
        RSortedSet<Integer> rSortedSet=RedissionUtils.getInstance().getRSortedSet(redisson, "testSortedSet");  
        //�������  
        rSortedSet.clear();  
        rSortedSet.add(45);  
        rSortedSet.add(12);  
        rSortedSet.addAsync(45);  
        rSortedSet.add(100);  
        //��������  
        System.out.println(Arrays.toString(rSortedSet.toArray()));;  
    }  
  
    /** 
     * RSet ӳ��Ϊ redis server ��set ���� 
     *  redis server ����: 
     * �鿴���м�---->keys * 
     * �鿴key������--->type testSet 
     * �鿴key��ֵ ---->smembers testSet  
     */  
    @Test  
    public void testGetRSet() {  
        RSet<Integer> rSet=RedissionUtils.getInstance().getRSet(redisson, "testSet");  
        //�������  
        rSet.clear();  
        Collection<Integer> c=Arrays.asList(12,45,12,34,56,78);  
        rSet.addAll(c);  
        //��������  
        System.out.println(Arrays.toString(rSet.toArray()));  
    }  
  
    /** 
     * RList ӳ��Ϊ redis server��list���� 
     *  redis server ����: 
     * �鿴���м�---->keys * 
     * �鿴key������--->type testList 
     * �鿴key��ֵ ---->lrange testList 0 10 
     */  
    @Test  
    public void testGetRList() {  
        RList<Integer> rList=RedissionUtils.getInstance().getRList(redisson, "testList");  
        //�������  
        rList.clear();  
        Collection<Integer> c=Arrays.asList(12,45,12,34,56,78);  
        rList.addAll(c);  
        //��������  
        System.out.println(Arrays.toString(rList.toArray()));  
    }  
  
    /** 
     * RQueue ӳ��Ϊ redis server��list���� 
     * ����--�����ȳ� 
     *  redis server ����: 
     * �鿴���м�---->keys * 
     * �鿴key������--->type testQueue 
     * �鿴key��ֵ ---->lrange testQueue 0 10  
     */  
    @Test  
    public void testGetRQueue() {  
        RQueue<Integer> rQueue=RedissionUtils.getInstance().getRQueue(redisson, "testQueue");  
        //�������  
        rQueue.clear();  
        Collection<Integer> c=Arrays.asList(12,45,12,34,56,78);  
        rQueue.addAll(c);  
        //�鿴����Ԫ��  
        System.out.println(rQueue.peek());  
        System.out.println(rQueue.element());  
        //�Ƴ�����Ԫ��  
        System.out.println(rQueue.poll());  
        System.out.println(rQueue.remove());  
        //�������  
        System.out.println(Arrays.toString(rQueue.toArray()));  
    }  
  
    /** 
     * RDeque ӳ��Ϊ redis server �� list���� 
     * ˫�˶���--��ͷ�Ͷ�β������ӻ����Ƴ���Ҳ��ѭ���е������ȳ� 
     *  redis server ����: 
     * �鿴���м�---->keys * 
     * �鿴key������--->type testDeque 
     * �鿴key��ֵ ---->lrange testDeque 0 10   
     */  
    @Test  
    public void testGetRDeque() {  
        RDeque<Integer> rDeque=RedissionUtils.getInstance().getRDeque(redisson, "testDeque");  
        //���˫�˶���  
        rDeque.clear();  
        Collection<Integer> c=Arrays.asList(12,45,12,34,56,78);  
        rDeque.addAll(c);  
        //��ͷ���Ԫ��  
        rDeque.addFirst(100);  
        //��β���Ԫ��  
        rDeque.addLast(200);  
        System.out.println(Arrays.toString(rDeque.toArray()));  
        //�鿴��ͷԪ��  
        System.out.println(rDeque.peek());  
        System.out.println(rDeque.peekFirst());  
        //�鿴��βԪ��  
        System.out.println(rDeque.peekLast());  
        System.out.println(Arrays.toString(rDeque.toArray()));  
        //�Ƴ���ͷԪ��  
        System.out.println(rDeque.poll());  
        System.out.println(rDeque.pollFirst());  
        //�Ƴ���βԪ��  
        System.out.println(rDeque.pollLast());  
        System.out.println(Arrays.toString(rDeque.toArray()));  
        //��Ӷ�βԪ��  
        System.out.println(rDeque.offer(300));  
        System.out.println(rDeque.offerFirst(400));  
        System.out.println(Arrays.toString(rDeque.toArray()));  
        //�Ƴ���ͷԪ��  
        System.out.println(rDeque.pop());  
        //��ʾ˫�˶��е�Ԫ��  
        System.out.println(Arrays.toString(rDeque.toArray()));  
          
    }  
  
    /** 
     * RLock ӳ��Ϊredis server��string ���� 
     * string�д�� �̱߳�ʾ���̼߳��� 
     * �鿴���м�---->keys * 
     * �鿴key������--->type testLock1 
     * �鿴key��ֵ ---->get testLock1  
     * �������redis server�� ���� testLock1 
     * �Ͳ���ʹ��   rLock.unlock(); 
     * ��Ϊʹ�� rLock.unlock(); ֮�� �ͻ�ɾ��redis server�е� testLock1 
     *  
     */  
    @Test  
    public void testGetRLock() {  
        RLock rLock=RedissionUtils.getInstance().getRLock(redisson, "testLock1");  
        if(rLock.isLocked()) rLock.unlock();  
        else rLock.lock();  
        //  
        System.out.println(rLock.getName());  
        System.out.println(rLock.getHoldCount());  
        System.out.println(rLock.isLocked());  
        rLock.unlock();  
    }  
  
    /** 
     * RAtomicLong ӳ��Ϊredis server��string ���� 
     * string����ֵ 
     * �鿴���м�---->keys * 
     * �鿴key������--->type testAtomicLong 
     * �鿴key��ֵ ---->get testAtomicLong  
     */  
    @Test  
    public void testGetRAtomicLong() {  
        RAtomicLong rAtomicLong=RedissionUtils.getInstance().getRAtomicLong(redisson, "testAtomicLong");  
        rAtomicLong.set(100);  
        System.out.println(rAtomicLong.addAndGet(200));  
        System.out.println(rAtomicLong.decrementAndGet());  
        System.out.println(rAtomicLong.get());  
    }  
  
    /** 
     * RCountDownLatch ӳ��Ϊredis server��string ���� 
     * string����ֵ 
     * ����--�ȴ������߳��еĲ��������� �ڽ��в��� 
     * �鿴���м�---->keys * 
     * �鿴key������--->type testCountDownLatch 
     * �鿴key��ֵ ---->get testCountDownLatch  
     */  
    @Test  
    public void testGetRCountDownLatch() throws InterruptedException {  
        RCountDownLatch rCountDownLatch=RedissionUtils.getInstance().getRCountDownLatch(redisson, "testCountDownLatch");  
        System.out.println(rCountDownLatch.getCount());  
        //rCountDownLatch.trySetCount(1l);  
        System.out.println(rCountDownLatch.getCount());  
        rCountDownLatch.await(10, TimeUnit.SECONDS);  
        System.out.println(rCountDownLatch.getCount());  
    }  
  
    /** 
     * ��Ϣ���еĶ����� 
     * @throws InterruptedException 
     */  
    @Test  
    public void testGetRTopicSub() throws InterruptedException {  
        RTopic<String> rTopic=RedissionUtils.getInstance().getRTopic(redisson, "testTopic");  
//        rTopic.addListener(new MessageListener<String>() {  
//
//			@Override
//			public void onMessage(String arg0, String arg1) {
//				System.out.println("�㷢������:"+arg0);
//			}  
//        });  
        //�ȴ������߷�����Ϣ  
        RCountDownLatch rCountDownLatch=RedissionUtils.getInstance().getRCountDownLatch(redisson, "testCountDownLatch");  
        rCountDownLatch.trySetCount(1);  
        rCountDownLatch.await();  
    }  
      
    /** 
     * ��Ϣ���еķ����� 
     */  
    @Test  
    public void testGetRTopicPub() {  
        RTopic<String> rTopic=RedissionUtils.getInstance().getRTopic(redisson, "testTopic");  
        System.out.println(rTopic.publish("�����Ƕ�ͯ�ڣ���Ҷ�ͯ�ڿ���"));  
        //��������Ϣ�� �ö����߲��ٵȴ�  
        RCountDownLatch rCountDownLatch=RedissionUtils.getInstance().getRCountDownLatch(redisson, "testCountDownLatch");  
        rCountDownLatch.countDown();  
    }  

}
