package com.kk.router.round;

import com.kk.util.ServiceWrapper;
import com.kk.router.RouterAbs;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class RoundRouter extends RouterAbs {

    private volatile int index = 0;
    private final ReentrantLock lock = new ReentrantLock();
    @Override
    public ServiceWrapper select(List<ServiceWrapper> services) {
        return services.get(getNextIndex(services.size()));
    }
    private int getNextIndex(int size) {
        try {
            lock.lock();
            if (index + 1 >= size) {
                index = 0;
            }
            return index++;
        } finally {
            lock.unlock();
        }
    }
}