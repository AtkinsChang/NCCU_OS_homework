package edu.nccu.plsm.osproject.web.info;

import edu.nccu.plsm.osproject.api.ProducerMonitor;

import java.util.concurrent.Future;

public class ProducerExecutingInfo {
    private final Future<?> future;
    private final ProducerMonitor producerMonitor;

    public ProducerExecutingInfo(ProducerMonitor producerMonitor, Future<?> future) {
        this.producerMonitor = producerMonitor;
        this.future = future;
    }

    public ProducerMonitor getProducerMonitor() {
        return producerMonitor;
    }

    public Future<?> getFuture() {
        return future;
    }

}