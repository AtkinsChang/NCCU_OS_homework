package edu.nccu.plsm.osproject;

import edu.nccu.plsm.osproject.jmx.Control;
import edu.nccu.plsm.osproject.jmx.ControlMBean;
import edu.nccu.plsm.osproject.jmx.QueueConfig;
import edu.nccu.plsm.osproject.jmx.QueueConfigMBean;
import edu.nccu.plsm.osproject.queue.ConfigurableQueue;
import edu.nccu.plsm.osproject.task.Task;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) throws Exception {
        ExecutorService es = Executors.newCachedThreadPool();
        ConfigurableQueue<Task> queue = new ConfigurableQueue<>();

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        QueueConfigMBean queueBean = new QueueConfig(queue);
        ControlMBean controlMBean = new Control(queue, es);
        try {
            ObjectName queueMBeanName = new ObjectName("App:name=Queue");
            ObjectName controlMBeanName = new ObjectName("App:name=Control");

            mbs.registerMBean(queueBean, queueMBeanName);
            mbs.registerMBean(controlMBean, controlMBeanName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        controlMBean.addNewProducer("p1");
        controlMBean.addNewConsumer("c1");

    }
}
