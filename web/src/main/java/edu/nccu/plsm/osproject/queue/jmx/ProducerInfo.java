package edu.nccu.plsm.osproject.queue.jmx;

import edu.nccu.plsm.osproject.queue.Producer;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.Future;

public class ProducerInfo implements ProducerInfoMBean {

    private static final MBeanServer M_BEAN_SERVER = ManagementFactory.getPlatformMBeanServer();

    private final Producer producer;
    private Future<?> future;

    public ProducerInfo(Producer producer) {
        this.producer = producer;
    }

    public void setFuture(Future<?> f) {
        this.future = f;
    }

    @Override
    public String getName() {
        return producer.getName();
    }

    @Override
    public long getJobCreationCount() {
        return producer.getCount();
    }

    @Override
    public int getMaxCreationTime() {
        return producer.getProducingTimeRange().getMax();
    }

    @Override
    public void setCreationTimeRange(int min, int max) {
        producer.getProducingTimeRange().setRange(max, min);
    }

    @Override
    public int getMinCreationTime() {
        return producer.getProducingTimeRange().getMin();
    }

    @Override
    public int getMaxTaskProcessTime() {
        return producer.getTaskExecuteTimeRange().getMax();
    }

    @Override
    public int getMinTaskProcessTime() {
        return producer.getTaskExecuteTimeRange().getMin();
    }

    @Override
    public void setTaskProcessTimeRange(int min, int max) {
        producer.getTaskExecuteTimeRange().setRange(max, min);
    }

    @Override
    public void shutdown(boolean interrupt) {
        producer.shutdown();
        future.cancel(interrupt);
        try {
            ObjectName pmName = new ObjectName("App:service=producer,name=" + this.producer.getName());
            M_BEAN_SERVER.unregisterMBean(pmName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
