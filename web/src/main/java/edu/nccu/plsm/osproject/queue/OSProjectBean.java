package edu.nccu.plsm.osproject.queue;

import com.google.common.collect.ImmutableList;
import edu.nccu.plsm.osproject.queue.message.entity.ConsumerJson;
import edu.nccu.plsm.osproject.queue.message.entity.ProducerJson;
import edu.nccu.plsm.osproject.queue.message.entity.QueueJson;
import edu.nccu.plsm.osproject.queue.message.entity.TaskJson;

import javax.ejb.Local;

@Local
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
