package edu.nccu.plsm.osproject;

import edu.nccu.plsm.osproject.api.ConsumerMonitor;
import edu.nccu.plsm.osproject.queue.api.ConsumerBuffer;
import edu.nccu.plsm.osproject.task.TaskNotCompleteException;
import edu.nccu.plsm.osproject.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Consumer implements ConsumerMonitor, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    private final String name;
    private final ConsumerBuffer<Task> consumerBuffer;
    private AtomicBoolean isRunning;
    private AtomicInteger efficiency;
    private AtomicLong count;


    public Consumer(String name, ConsumerBuffer<Task> consumerBuffer) {
        this.name = name;
        MDC.put("name", name);
        this.consumerBuffer = consumerBuffer;
        this.isRunning = new AtomicBoolean(Boolean.FALSE);
        this.efficiency = new AtomicInteger(10);
        this.count = new AtomicLong(0L);
    }

    public String getName() {
        return name;
    }

    public int getEfficiency() {
        return efficiency.get();
    }

    public void setEfficiency(int efficiency) {
        this.efficiency.getAndSet(efficiency);
    }

    public void shutdownGracefully() {
        isRunning.getAndSet(Boolean.FALSE);
    }

    public long getTaskExecutionCount() {
        return count.get();
    }

    @Override
    public void run() {
        //this.runningThread = Thread.currentThread();
        //this.runningThread.setName("consumer-" + name);
        this.isRunning.getAndSet(Boolean.TRUE);
        MDC.put("name", name);
        Task task = null;
        try {
            while (isRunning.get()) {
                LOGGER.info("Taking task from queue");
                task = consumerBuffer.take();
                LOGGER.info("Executing task create by {}...", task.getCreatorName());
                count.getAndIncrement();
                task.doTask(efficiency.get());
                LOGGER.trace("Task complete");
            }
        } catch (TaskNotCompleteException e) {
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
        } finally {
            LOGGER.info("Shutdown complete");
        }
    }

}