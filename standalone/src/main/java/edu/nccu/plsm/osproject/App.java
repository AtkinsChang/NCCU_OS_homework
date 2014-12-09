package edu.nccu.plsm.osproject;

import edu.nccu.plsm.osproject.management.Control;
import edu.nccu.plsm.osproject.management.queue.QueueInfo;
import edu.nccu.plsm.osproject.queue.OSProjectQueue;
import edu.nccu.plsm.osproject.task.api.Task;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class App {
    public static void main(String[] args) throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ThreadFactory producerThreadFactory = new BasicThreadFactory.Builder()
                .namingPattern("producer-thread-%d")
                .build();
        ExecutorService pes = Executors.newCachedThreadPool(producerThreadFactory);

        ThreadFactory consumerThreadFactory = new BasicThreadFactory.Builder()
                .namingPattern("consumer-thread-%d")
                .build();
        ExecutorService ces = Executors.newCachedThreadPool(consumerThreadFactory);

        ThreadFactory lockThreadFactory = new BasicThreadFactory.Builder()
                .namingPattern("lock-thread-%d")
                .build();
        ExecutorService qes = Executors.newCachedThreadPool(lockThreadFactory);

        OSProjectQueue<Task> queue = new OSProjectQueue<>(Integer.MAX_VALUE, qes);

        QueueInfo queueBean = new QueueInfo();
        queueBean.setQueue(queue);
        queueBean.init();
        Control controlMBean = new Control();
        controlMBean.setQueue(queue);
        controlMBean.init(pes, ces);
        /*
        try {
            ObjectName queueMBeanName = new ObjectName("OS.Project:service=queue,name=QueueInfo");
            ObjectName controlMBeanName = new ObjectName("OS.Project:name=Control");

            mbs.registerMBean(queueBean, queueMBeanName);
            mbs.registerMBean(controlMBean, controlMBeanName);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        controlMBean.addNewProducer("p1");
        controlMBean.addNewConsumer("c1");

    }
}
