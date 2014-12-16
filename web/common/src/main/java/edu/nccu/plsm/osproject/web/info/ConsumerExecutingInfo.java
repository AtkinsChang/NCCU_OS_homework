package edu.nccu.plsm.osproject.web.info;

import edu.nccu.plsm.osproject.api.ConsumerMonitor;

import java.util.concurrent.Future;

public class ConsumerExecutingInfo {

    private final Future<?> future;
    private final ConsumerMonitor consumerMonitor;

    public ConsumerExecutingInfo(ConsumerMonitor consumerMonitor, Future<?> future) {
        this.consumerMonitor = consumerMonitor;
        this.future = future;
    }

    public ConsumerMonitor getConsumerMonitor() {
        return consumerMonitor;
    }

    public Future<?> getFuture() {
        return future;
    }

}