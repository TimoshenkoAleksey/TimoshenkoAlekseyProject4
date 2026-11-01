package ru.aston.timoshenko.aleksey.project4.baglock;

import ru.aston.timoshenko.aleksey.project4.enums.Role;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class BagLockExample {
    // DeepSeek назвал это не livelock, а baglock, несмотря на то, что сам дал такой пример!
    // Это синхронизированный цикл, а не классический livelock.
    // Освобождение ресурсов (намерений) номинальное.
    // Из-за синхронизации циклов, захват невозможен.
    // Потратил на него два дня. Оставлю здесь. Не буду удалять.
    // Кроме того, этот вариант интернет выдает за livelock.


    public static void main(String[] args) {
        Thread thread1 = new Thread(new Worker("1", Role.THREAD_ONE));
        Thread thread2 = new Thread(new Worker("2", Role.THREAD_TWO));
        thread1.start();
        thread2.start();
    }

    public static class Worker implements Runnable {
        private final String message;
        private final Role role;
        private static final IntentBoard intent = new IntentBoard();
        private static final CyclicBarrier barrier = new CyclicBarrier(2);


        public Worker(String message, Role role) {
            this.message = message;
            this.role = role;
        }

        public void run() {
            while (true) {
                setMyIntent(true);
                try {
                    barrier.await();
                    while (getOtherIntent(role)) {
                        setMyIntent(false);
                        Thread.onSpinWait();
                        setMyIntent(true);
                        barrier.await();
                    }
                    System.out.println(message);
                    setMyIntent(false);
                    barrier.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void setMyIntent(boolean choice) {
            switch (role) {
                case THREAD_ONE -> intent.threadOneIntent = choice;
                case THREAD_TWO -> intent.threadTwoIntent = choice;
            }
        }

        private boolean getOtherIntent(Role role) {
            switch (role) {
                case THREAD_ONE -> {
                    return intent.threadTwoIntent;
                }
                default -> {
                    return intent.threadOneIntent;
                }
            }
        }

        private static class IntentBoard {
            public volatile boolean threadOneIntent;
            public volatile boolean threadTwoIntent;
        }
    }
}
