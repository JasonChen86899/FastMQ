package fastmq.broker;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import javax.annotation.Resource;

import com.github.zkclient.ZkClient;
import fastmq.broker.message.KeyMessage;
import fastmq.broker.storage.MessageStorageStructure;
import fastmq.broker.storage.messagenumberrecords.RecordsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Jason Chen on 2016/8/30.
 */

/**
 * 每个broker Id 机器都要求生成这个消息Map，进行消息的异步发送，存储
 */
@Service
public class MessageQueueMap {

    private static RecordsUtil recordsUtil;
    private static MessageStorageStructure messageStorageStructure;
    private static ConcurrentHashMap<String, Queue<KeyMessage<String, Object>>> messageQueueMap = new ConcurrentHashMap<String, Queue<KeyMessage<String, Object>>>();
    private static ZkClient zkClient;

    public static Queue getQueueByName(String topic_partition) {
        if (messageQueueMap.get(topic_partition) == null) {
            creatQueueByname(topic_partition);
        }
        return messageQueueMap.get(topic_partition);
    }

    //优先队列保持队列的绝对有序性
    public static void creatQueueByname(String topic_patition) {
        PriorityBlockingQueue<KeyMessage<String, Object>> q;
        messageQueueMap.put(topic_patition, q = new PriorityBlockingQueue<>());
        /**
         * 在创建队列的时候进行检查，是否发生过宕机或者扩容，有的话需要进行下面的操作
         */
        try {
            messageStorageStructure.getMessageAndPutIntoQueue(topic_patition, q);
        } catch (Exception e) {
            if (!(e instanceof NullPointerException))//如果是初始分配非宕机或扩容的恢复则绕过
            {
                e.printStackTrace();
            }
        }

        /**
         * 每产生一个队列就产生对应的BrokerPush线程进行消息的推送
         */
        try {
            new BrokerPush(zkClient, topic_patition, q, recordsUtil).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Resource(name = "redis")
    public void setSqlDBUtil(RecordsUtil records) {
        recordsUtil = records;
    }

    @Autowired
    public void setMessageStorageStructure(MessageStorageStructure mss) {
        messageStorageStructure = mss;
    }

    @Autowired
    public void setZkClient(ZkClient zk) {
        MessageQueueMap.zkClient = zk;
    }
}
