package edu.nccu.plsm.osproject.queue.api;

public interface ConsumerBuffer<E> {

    E take(ConsumerStateHelper consumer) throws InterruptedException;

    void put(E e) throws InterruptedException;

}
