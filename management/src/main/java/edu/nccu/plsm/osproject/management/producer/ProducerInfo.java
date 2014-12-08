package edu.nccu.plsm.osproject.management.producer;

import edu.nccu.plsm.osproject.api.ProducerMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.Future;

public class ProducerInfo implements ProducerInfoMBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerInfo.class);
    private static final MBeanServer M_BEAN_SERVER = ManagementFactory.getPlatformMBeanServer();

    private final ProducerMonitor producerMonitor;
    private Future<?> future;

    public ProducerInfo(ProducerMonitor producerMonitor) {
        super();
        this.producerMonitor = producerMonitor;
    }

    public void setFuture(Future<?> f) {
        this.future = f;
    }

    @Override
    public String getName() {
        return producerMonitor.getName();
    }

    @Override
    public long getTaskCreationCount() {
        return producerMonitor.getTaskCreationCount();
    }

    @Override
    public int getMaxProductionTime() {
        return producerMonitor.getMaxProductionTime();
    }

    @Override
    public int getMinProductionTime() {
        return producerMonitor.getMinProductionTime();
    }

    @Override
    public void setProductionTimeRange(int min, int max) {
        this.producerMonitor.setProductionTimeRange(min, max);
    }

    @Override
    public int getMaxTaskExecutionTime() {
        return this.producerMonitor.getMaxTaskExecutionTime();
    }

    @Override
    public int getMinTaskExecutionTime() {
        return this.producerMonitor.getMinTaskExecutionTime();
    }

    @Override
    public void setTaskExecutionTimeRange(int min, int max) {
        this.producerMonitor.setTaskExecutionTimeRange(min, max);
    }

    @Override
    public void shutdown(boolean interrupt) {
        this.producerMonitor.shutdownGracefully();
        future.cancel(interrupt);
        try {
            ObjectName name = new ObjectName("OS.Project:service=producer,name=" + this.producerMonitor.getName());
            M_BEAN_SERVER.unregisterMBean(name);
        } catch (MalformedObjectNameException | InstanceNotFoundException | MBeanRegistrationException e) {
            LOGGER.error("Internal Error", e);
        }
    }

}
