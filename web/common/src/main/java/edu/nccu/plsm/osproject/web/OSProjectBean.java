package edu.nccu.plsm.osproject.web;

import com.google.common.collect.ImmutableList;
import edu.nccu.plsm.osproject.web.info.ConsumerJson;
import edu.nccu.plsm.osproject.web.info.ProducerJson;
import edu.nccu.plsm.osproject.web.info.QueueJson;
import edu.nccu.plsm.osproject.web.info.TaskJson;

import javax.ejb.Remote;

@Remote
public interface OSProjectBean {

    public QueueJson getQueueJson();

    ImmutableList<TaskJson> getTaskJson();

    ImmutableList<ConsumerJson> getConsumerJson();

    ImmutableList<ProducerJson> getProducerJson();

    void updateConsumer(String name, int efficiency);

    void shutdownConsumer(String name, boolean mayInterruptIfRunning);

    void updateProducer(String name, int pMin, int pMax, int tMin, int tMax);

    void shutdownProducer(String name, boolean mayInterruptIfRunning);

    void updateQueue(int capacity, int pMin, int pMax, int tMin, int tMax);

    void lockTake();

    void unlockTake();

    void lockPut();

    void unlockPut();

}
