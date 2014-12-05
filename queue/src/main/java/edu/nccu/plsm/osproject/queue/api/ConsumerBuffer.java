package edu.nccu.plsm.osproject.queue.api;

public interface ConsumerBuffer<E> extends ProducerBuffer<E> {

    E take() throws InterruptedException;

}
