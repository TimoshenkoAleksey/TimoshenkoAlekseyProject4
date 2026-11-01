package ru.aston.timoshenko.aleksey.project4.truelivelock;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class LiveLockExample {
    // Это реализация честного livelock, когда потоки конкурируют за ресурсы,
    // но при этом освобождают то, что успели захватить, если не обладают обоими,
    // давая возможность другому потоку захватить этот ресурс и выполнить действие.
    // При этом, редкие "прорывы" неизбежны, так как на современной jvm в многопоточном приложении livelock невозможен.
    // Если это честный livelock, конечно!
    // А не синхронизированный цикл (барьеры) или не скрытый deadlock (вежливые супруги)

    public static void main(String[] args) {
        ReentrantLock lock1 = new ReentrantLock();
        ReentrantLock lock2 = new ReentrantLock();
        CyclicBarrier barrier = new CyclicBarrier(2);
        Thread thread1 = new Thread(new Worker("1", lock1, lock2, barrier), "Первый поток");
        Thread thread2 = new Thread(new Worker("2", lock2, lock1, barrier), "Второй поток");
        thread1.start();
        thread2.start();
    }

    public static class Worker implements Runnable {
        private final String message;
        private final ReentrantLock firstLock;
        private final ReentrantLock secondLock;
        private final CyclicBarrier barrier;

        public Worker(String message, ReentrantLock firstLock, ReentrantLock secondLock, CyclicBarrier barrier) {
            this.message = message;
            this.firstLock = firstLock;
            this.secondLock = secondLock;
            this.barrier = barrier;
        }

        public void run() {
            boolean hasFirstLock;
            boolean hasSecondLock;
            String name = Thread.currentThread().getName();
            try {
                barrier.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                hasFirstLock = false;
                hasSecondLock = false;
                try {
                    System.out.println(name + ": пытается распечатать послание");
                    hasFirstLock = firstLock.tryLock(10, TimeUnit.MILLISECONDS);
                    if (hasFirstLock) {
                        Thread.sleep(500);
                        hasSecondLock = secondLock.tryLock(10, TimeUnit.MILLISECONDS);
                        if (hasSecondLock) {
                            System.out.println(message);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } finally {
                    if (hasSecondLock) {
                        secondLock.unlock();
                    }
                    if (hasFirstLock) {
                        firstLock.unlock();
                    }
                }
                Thread.yield();
            }
        }
    }
}

