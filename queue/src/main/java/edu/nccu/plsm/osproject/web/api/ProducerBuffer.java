package edu.nccu.plsm.osproject.web.api;

public interface ProducerBuffer<E> {

    void put(ProducerStateHelper producer, E element) throws InterruptedException;

}
