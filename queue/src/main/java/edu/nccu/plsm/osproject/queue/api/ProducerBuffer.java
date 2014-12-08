package edu.nccu.plsm.osproject.queue.api;

public interface ProducerBuffer<E> {

    void put(ProducerStateHelper producer, E element) throws InterruptedException;

}
