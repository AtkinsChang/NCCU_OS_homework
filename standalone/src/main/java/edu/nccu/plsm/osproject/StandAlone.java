package edu.nccu.plsm.osproject;

import edu.nccu.plsm.osproject.management.Control;
import edu.nccu.plsm.osproject.management.queue.QueueInfo;
import edu.nccu.plsm.osproject.queue.OSProjectQueue;
import edu.nccu.plsm.osproject.task.api.Task;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class StandAlone {

    public static void main(String[] args) throws Exception {
        //Producer Thread Pool
        ThreadFactory producerThreadFactory = new BasicThreadFactory.Builder()
                .namingPattern("producer-thread-%d")
                .build();
        ExecutorService producerExecutorService = Executors.newCachedThreadPool(producerThreadFactory);

        //Consumer Thread Pool
        ThreadFactory consumerThreadFactory = new BasicThreadFactory.Builder()
                .namingPattern("consumer-thread-%d")
                .build();
        ExecutorService consumerExecutorService = Executors.newCachedThreadPool(consumerThreadFactory);

        //Locker Thread Pool
        ThreadFactory lockThreadFactory = new BasicThreadFactory.Builder()
                .namingPattern("lock-thread-%d")
                .build();
        ExecutorService lockerExecutorService = Executors.newCachedThreadPool(lockThreadFactory);

        //Create Queue
        int queueSize = Integer.MAX_VALUE;
        OSProjectQueue<Task> queue = new OSProjectQueue<>(queueSize, lockerExecutorService);

        //Register mBean
        QueueInfo queueBean = new QueueInfo();
        queueBean.setQueue(queue);
        queueBean.init();
        Control controlMBean = new Control();
        controlMBean.setQueue(queue);
        controlMBean.init(producerExecutorService, consumerExecutorService);


        //
        controlMBean.addNewProducer("p1");
        controlMBean.addNewConsumer("c1");
    }
}
