package ru.aston.timoshenko.aleksey.project4.deadlock;

public class DeadlockExample {

    private final static Lock lock1 = new Lock("Замок №1");
    private final static Lock lock2 = new Lock("Замок №2");

    public static void main(String[] args) {

        new Thread(()->{
            while(true){
                System.out.println("1");
                veryImportantMethod(lock1, lock2);
            }
        }, "Первый поток").start();

        new Thread(()->{
            while(true){
                System.out.println("2");
                veryImportantMethod(lock2, lock1);
            }
        }, "Второй поток").start();

    }

    private static void veryImportantMethod (Lock lock1, Lock lock2) {
        synchronized (lock1) {
            String name = Thread.currentThread().getName();
            System.out.println(name + " Захватил " + lock1.name + " и ожидает " + lock2.name);
            try {
                Thread.sleep(3000);
                if ("Первый поток".equals(name)) {
                    System.out.println();
                    System.out.println(name + " : Похоже это дедлок! Как считаете коллега?");
                } else if ("Второй поток".equals(name)) {
                    System.out.println(name + " : Согласен в вами, коллега! Это, определенно, дедлок!");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            synchronized (lock2) {
                System.out.println(Thread.currentThread().getName() + " Захватил " + lock1.name);
            }
        }
    }

    private record Lock (String name) {
    }
}
