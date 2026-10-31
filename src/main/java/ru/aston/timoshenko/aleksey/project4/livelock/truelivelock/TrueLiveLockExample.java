package ru.aston.timoshenko.aleksey.project4.livelock.truelivelock;

public class TrueLiveLockExample {
    // Вот, что реально делает код из примера "вежливые супруги".
    // Одна значимая проверка, которая никогда не выполнится ни у одного из потоков.

    public static void main(String[] args) {
        Worker worker1 = new Worker("1");
        Worker worker2 = new Worker("2");
        worker1.setOther(worker2);
        worker2.setOther(worker1);
        new Thread(worker1).start();
        new Thread(worker2).start();
    }

    private static class Worker implements Runnable {
        private final String message;
        private Worker other;
        private boolean intent = true;

        public Worker(String message) {
            this.message = message;
        }

        public void setOther(Worker other) {
            this.other = other;
        }

        @Override
        public void run() {
            while(true) {
                if(other.intent) {
                    continue;
                }
                System.out.println(message);
                intent = false;
            }
        }
    }
}
