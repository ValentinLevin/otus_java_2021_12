package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class HomeWork {
    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);

    private static final int THREAD_COUNT = 2;
    private final List<Thread> threads = new ArrayList<>(THREAD_COUNT);
    private final Object monitor = new Object();
    private int activeThreadId;

    private List<Integer> generateSeriesToProcess(int digitCount) {
        List<Integer> series = new ArrayList<>();

        series.addAll(IntStream.rangeClosed(1, digitCount).boxed().toList());
        series.addAll(IntStream.range(1, digitCount).boxed().sorted(Collections.reverseOrder()).toList());

        return series;
    }

    private void setNextThreadId(int afterThreadId) {
        synchronized (monitor) {
            activeThreadId = (afterThreadId % THREAD_COUNT) + 1;
        }
    }

    private boolean isAllowedToProcess(int threadId) {
        return activeThreadId == threadId;
    }

    private void threadTask(int threadId, List<Integer> seriesToProcess) {
        for (Integer value: seriesToProcess) {
            if (!Thread.currentThread().isInterrupted()) {
                synchronized (monitor) {
                    try {
                        while (!isAllowedToProcess(threadId)) {
                            monitor.wait();
                        }

                        logger.info("Thread ID: {}, value: {}", threadId, value);
                        Thread.sleep(400);

                        setNextThreadId(threadId);
                        monitor.notifyAll();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    public void go() {
        List<Integer> series = generateSeriesToProcess(10);
        setNextThreadId(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i+1;
            Thread thread = new Thread(() -> threadTask(threadId, series));
            thread.start();
            threads.add(thread);
        }

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(15));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        threads.forEach(Thread::interrupt);
    }
}
