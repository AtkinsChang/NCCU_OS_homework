package edu.nccu.plsm.osproject.queue.jmx;

import edu.nccu.plsm.osproject.queue.Consumer;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.Future;

public class ConsumerInfo implements ConsumerInfoMBean {

    private static final MBeanServer M_BEAN_SERVER = ManagementFactory.getPlatformMBeanServer();

    private final Consumer consumer;
    private Future<?> future;

    public ConsumerInfo(Consumer consumer) {
        this.consumer = consumer;
    }

    public void setFuture(Future<?> f) {
        this.future = f;
    }

    @Override
    public String getName() {
        return this.consumer.getName();
    }

    @Override
    public long getJobCompleteCount() {
        return this.consumer.getCount();
    }

    @Override
    public int getEfficiency() {
        return this.consumer.getEfficiency();
    }

    @Override
    public void setEfficiency(int efficiency) {
        this.consumer.setEfficiency(efficiency);
    }

    @Override
    public void shutdown(boolean interrupt) {
        this.consumer.shutdown();
        future.cancel(interrupt);
        try {
            ObjectName cmName = new ObjectName("App:service=consumer,name=" + this.consumer.getName());
            M_BEAN_SERVER.unregisterMBean(cmName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}