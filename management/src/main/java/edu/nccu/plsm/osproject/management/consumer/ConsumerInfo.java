package edu.nccu.plsm.osproject.management.consumer;

import edu.nccu.plsm.osproject.api.ConsumerMonitor;
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

public class ConsumerInfo implements ConsumerInfoMBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerInfo.class);
    private static final MBeanServer M_BEAN_SERVER = ManagementFactory.getPlatformMBeanServer();

    private final ConsumerMonitor consumerMonitor;
    private Future<?> future;

    public ConsumerInfo(ConsumerMonitor consumerMonitor) {
        super();
        this.consumerMonitor = consumerMonitor;
        MDC.put("name", consumerMonitor.getName());
    }

    public void setFuture(Future<?> f) {
        this.future = f;
    }

    @Override
    public String getName() {
        return this.consumerMonitor.getName();
    }

    @Override
    public long getTaskExecutionCount() {
        return this.consumerMonitor.getTaskExecutionCount();
    }

    @Override
    public int getEfficiency() {
        return this.consumerMonitor.getEfficiency();
    }

    @Override
    public void setEfficiency(int efficiency) {
        this.consumerMonitor.setEfficiency(efficiency);
    }

    @Override
    public void shutdown(boolean mayInterruptIfRunning) {
        this.consumerMonitor.shutdownGracefully();
        future.cancel(mayInterruptIfRunning);
        try {
            ObjectName name = new ObjectName("OS.Project:service=consumer,name=" + this.consumerMonitor.getName());
            M_BEAN_SERVER.unregisterMBean(name);
        } catch (MalformedObjectNameException | InstanceNotFoundException | MBeanRegistrationException e) {
            LOGGER.error("Internal Error", e);
        }
    }

}