package ru.aston.timoshenko.aleksey.project4.livelock;

import ru.aston.timoshenko.aleksey.project4.enums.Role;



public class LiveLockExample {

    // livelock реализован в соответствии с примером под названием «вежливые супруги»
    // Он плохой! Этот пример создает только видимость уступки.
    // В нём нужно пройти две проверки, но вторую пройти невозможно, так как намерение не меняется ни у кого,
    // поэтому первая проверка бесполезна. Она не функциональна.
    // И вместо передачи общего объекта, можно делать что угодно, но нельзя снимать флаги намерений.
    // Видимо, общепризнанный пример livelock нам лжёт!


    public static void main(String[] args) {
        Printer printer = new Printer();
        Worker worker1 = new Worker("1", Role.THREAD_ONE, printer);
        Worker worker2 = new Worker("1", Role.THREAD_TWO, printer);
        printer.setOwner(worker1);
        Thread threadOne = new Thread(() -> worker1.printMessage(worker2), "Первый поток");
        Thread threadTwo = new Thread(() -> worker2.printMessage(worker1), "Второй поток");
        threadOne.start();
        threadTwo.start();
    }
}
