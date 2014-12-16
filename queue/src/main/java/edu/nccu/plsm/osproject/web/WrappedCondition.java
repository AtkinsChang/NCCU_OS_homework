package edu.nccu.plsm.osproject.web;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class WrappedCondition implements Condition {

    private final Condition condition;


    private WrappedCondition(Condition condition) {
        super();
        this.condition = condition;
    }

    public static WrappedCondition wrap(Condition condition) {
        return new WrappedCondition(condition);
    }

    @Override
    public void await() throws InterruptedException {
        condition.await();
    }

    @Override
    public void awaitUninterruptibly() {
        condition.awaitUninterruptibly();
    }

    @Override
    public long awaitNanos(long nanosTimeout) throws InterruptedException {
        return condition.awaitNanos(nanosTimeout);
    }

    @Override
    public boolean await(long time, TimeUnit unit) throws InterruptedException {
        return condition.await(time, unit);
    }

    @Override
    public boolean awaitUntil(Date deadline) throws InterruptedException {
        return condition.awaitUntil(deadline);
    }

    @Override
    public void signal() {
        condition.signal();
    }

    @Override
    public void signalAll() {
        condition.signalAll();
    }
}
