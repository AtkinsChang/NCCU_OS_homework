package edu.nccu.plsm.osproject;

import edu.nccu.plsm.osproject.api.ProducerMonitor;
import edu.nccu.plsm.osproject.task.SimpleTask;
import edu.nccu.plsm.osproject.task.api.Task;
import edu.nccu.plsm.osproject.queue.TimeRange;
import edu.nccu.plsm.osproject.queue.api.ProducerBuffer;
import edu.nccu.plsm.osproject.queue.api.ProducerStateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Producer implements ProducerStateHelper, ProducerMonitor, Runnable {

    public static final int INIT = 0;
    public static final int WAITING_LOCK = 1;
    public static final int WAITING_NOT_FULL = 2;
    public static final int PUTTING = 3;
    public static final int CREATING_TASK = 10;
    public static final int SHUTDOWN = 20;
    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);
    private final String name;
    private final ProducerBuffer<Task> producerBuffer;
    private final AtomicLong count;
    private final AtomicBoolean isRunning;
    private final AtomicInteger state;
    private TimeRange productionTimeRange;
    private TimeRange taskExecutionTimeRange;

    public Producer(String name, ProducerBuffer<Task> producerBuffer) {
        this.name = name;
        this.producerBuffer = producerBuffer;
        this.count = new AtomicLong(0L);
        this.isRunning = new AtomicBoolean(Boolean.FALSE);
        this.productionTimeRange = new TimeRange(5000, 5000);
        this.taskExecutionTimeRange = new TimeRange(5000, 5000);
        this.state = new AtomicInteger(INIT);
    }

    @Override
    public int getState() {
        return this.state.get();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getTaskCreationCount() {
        return this.count.get();
    }

    @Override
    public int getMaxProductionTime() {
        return this.productionTimeRange.getMax();
    }

    @Override
    public int getMinProductionTime() {
        return this.productionTimeRange.getMin();
    }

    @Override
    public void setProductionTimeRange(int min, int max) {
        this.productionTimeRange = new TimeRange(min, max);
    }

    @Override
    public int getMaxTaskExecutionTime() {
        return this.taskExecutionTimeRange.getMax();
    }

    @Override
    public int getMinTaskExecutionTime() {
        return this.taskExecutionTimeRange.getMin();
    }

    @Override
    public void setTaskExecutionTimeRange(int min, int max) {
        this.taskExecutionTimeRange = new TimeRange(min, max);
    }

    @Override
    public void shutdownGracefully() {
        this.isRunning.getAndSet(Boolean.FALSE);
    }

    @Override
    public void run() {
        //this.runningThread = Thread.currentThread();
        //this.runningThread.setName("producer-" + name);
        MDC.put("name", name + " -     ");
        isRunning.set(Boolean.TRUE);
        try {
            while (isRunning.get()) {
                state.set(CREATING_TASK);
                Task task = new SimpleTask(this.name, taskExecutionTimeRange.random());
                LOGGER.info("Creating task...");
                count.getAndIncrement();
                Thread.sleep(productionTimeRange.random());
                LOGGER.info("Task creation done, putting to producer buffer");
                state.set(WAITING_LOCK);
                producerBuffer.put(this, task);
            }
        } catch (InterruptedException e) {
            LOGGER.warn("Forced stop, abandon task");
        } catch (Exception e) {
            LOGGER.error("error", e);
        } finally {
            state.set(SHUTDOWN);
            LOGGER.info("Shutdown complete");
        }
    }

    @Override
    public void setPutting() {
        state.set(PUTTING);
    }

    @Override
    public void setWaitingNotFull() {
        state.set(WAITING_NOT_FULL);
    }
}
