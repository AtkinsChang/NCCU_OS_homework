package edu.nccu.plsm.osproject.queue.api;

public interface ProducerBuffer<E> {

    void put(E element) throws InterruptedException;

}
