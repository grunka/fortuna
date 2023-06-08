package com.grunka.random.fortuna;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

class PrefetchingSupplier<T> implements Supplier<T> {
    private final ReentrantLock lock = new ReentrantLock();
    private final Supplier<T> delegate;
    private final ExecutorService executorService;
    private final AtomicReference<Future<T>> value = new AtomicReference<>();

    PrefetchingSupplier(Supplier<T> delegate, ExecutorService executorService) {
        this.delegate = delegate;
        this.executorService = executorService;
        value.set(executorService.submit(delegate::get));
    }

    @Override
    public T get() {
        lock.lock();
        try {
            T delegateValue = this.value.get().get();
            value.set(executorService.submit(delegate::get));
            return delegateValue;
        } catch (InterruptedException e) {
            throw new IllegalStateException("Interrupted while waiting for prefetch result", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Failure while prefetching", e.getCause());
        } finally {
            lock.unlock();
        }
    }

    void shutdownPrefetch() {
        value.getAndSet(null).cancel(true);
    }
}
