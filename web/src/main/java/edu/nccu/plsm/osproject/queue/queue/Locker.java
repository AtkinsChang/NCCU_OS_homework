package edu.nccu.plsm.osproject.queue.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

public class Locker {

    private static final Logger LOGGER = LoggerFactory.getLogger(Locker.class);

    private final Lock putLock;
    private final Lock takeLock;
    private final ExecutorService es;
    private Future<?> lockingPut;
    private Future<?> lockingTake;

    public Locker(Lock putLock, Lock takeLock, ExecutorService es) {
        this.putLock = putLock;
        this.takeLock = takeLock;
        this.es = es;

    }

    public void lockPut() {
        if (lockingPut == null || lockingPut.isDone()) {
            lockingPut = es.submit(() -> {
                Thread.currentThread().setName("user-putlock-thread");
                try {
                    putLock.lock();
                    while (Boolean.TRUE) {
                        Thread.sleep(Long.MAX_VALUE);
                    }
                } catch (InterruptedException e) {
                    LOGGER.debug("Unlock put lock");
                } catch (Exception e) {
                    LOGGER.error("", e);
                } finally {
                    putLock.unlock();
                }
            });
        }
    }

    public void unlockPut() {
        if (this.lockingPut.isDone()) {
            try {
                throw new IllegalArgumentException("Already release");
            } finally {
                this.putLock.unlock();
            }
        } else {
            this.lockingPut.cancel(true);
        }
    }

    public void lockTake() {
        if (lockingTake == null || lockingTake.isDone()) {
            lockingTake = es.submit(() -> {
                Thread.currentThread().setName("user-takelock-thread");
                try {
                    takeLock.lock();
                    while (Boolean.TRUE) {
                        Thread.sleep(Long.MAX_VALUE);
                    }
                } catch (InterruptedException e) {
                    LOGGER.debug("Unlock take lock");
                } catch (Exception e) {
                    LOGGER.error("", e);
                } finally {
                    takeLock.unlock();
                }
            });
        }

    }

    public void unlockTake() {
        if (this.lockingTake.isDone()) {
            try {
                throw new IllegalArgumentException("Already release");
            } finally {
                this.takeLock.unlock();
            }
        } else {
            this.lockingTake.cancel(true);
        }
    }

}
