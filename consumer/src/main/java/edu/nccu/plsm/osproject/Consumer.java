package edu.nccu.plsm.osproject;

import edu.nccu.plsm.osproject.api.ConsumerMonitor;
import edu.nccu.plsm.osproject.task.TaskNotCompleteException;
import edu.nccu.plsm.osproject.task.api.Task;
import edu.nccu.plsm.osproject.queue.api.ConsumerBuffer;
import edu.nccu.plsm.osproject.queue.api.ConsumerStateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Consumer implements ConsumerStateHelper, ConsumerMonitor, Runnable {

    public static final int INIT = 0;
    public static final int WAITING_LOCK = 1;
    public static final int WAITING_NOT_EMPTY = 2;
    public static final int TAKING = 3;
    public static final int EXECUTING = 10;
    public static final int SHUTDOWN = 20;
    public static final int ROLLBACK = 30;
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
    private final String name;
    private final ConsumerBuffer<Task> consumerBuffer;
    private final AtomicBoolean isRunning;
    private final AtomicInteger efficiency;
    private final AtomicLong count;
    private final AtomicInteger state;


    public Consumer(String name, ConsumerBuffer<Task> consumerBuffer) {
        this.name = name;
        this.consumerBuffer = consumerBuffer;
        this.isRunning = new AtomicBoolean(Boolean.FALSE);
        this.efficiency = new AtomicInteger(10);
        this.count = new AtomicLong(0L);
        this.state = new AtomicInteger(INIT);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getState() {
        return this.state.get();
    }

    @Override
    public int getEfficiency() {
        return efficiency.get();
    }

    @Override
    public void setEfficiency(int efficiency) {
        this.efficiency.getAndSet(efficiency);
    }

    @Override
    public void shutdownGracefully() {
        isRunning.getAndSet(Boolean.FALSE);
    }

    @Override
    public long getTaskExecutionCount() {
        return count.get();
    }

    @Override
    public void setTaking() {
        this.state.set(TAKING);
    }

    @Override
    public void setWaitingNotEmpty() {
        this.state.set(WAITING_NOT_EMPTY);
    }

    @Override
    public void run() {
        //this.runningThread = Thread.currentThread();
        //this.runningThread.setName("consumer-" + name);
        this.isRunning.getAndSet(Boolean.TRUE);
        MDC.put("name", name + " - ");
        Task task = null;
        try {
            while (isRunning.get()) {
                LOGGER.info("Taking task from queue");
                state.set(WAITING_LOCK);
                task = consumerBuffer.take(this);
                LOGGER.info("Executing task create by {}...", task.getCreatorName());
                state.set(EXECUTING);
                count.getAndIncrement();
                task.doTask(efficiency.get());
                LOGGER.trace("Task complete");
            }
        } catch (TaskNotCompleteException e) {
            state.set(ROLLBACK);
            LOGGER.warn("Interrupted while executing task, save task state and put back to buffer...", e);
            boolean isTaskRePut = Boolean.FALSE;
            while (!isTaskRePut) {
                try {
                    consumerBuffer.put(task);
                    isTaskRePut = Boolean.TRUE;
                } catch (InterruptedException ignored) {
                }
            }
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted while taking task", e);
        } catch (Exception exception) {
            LOGGER.error("Error", exception);
        } finally {
            state.set(SHUTDOWN);
            LOGGER.info("Shutdown complete");
        }
    }


}