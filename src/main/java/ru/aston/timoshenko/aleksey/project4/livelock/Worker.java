package ru.aston.timoshenko.aleksey.project4.livelock;

import ru.aston.timoshenko.aleksey.project4.enums.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

public class Worker {
    private final String message;
    private final Role role;
    private final Printer printer;
    private static final IntentBoard intent = new IntentBoard();
    private static final CyclicBarrier barrier = new CyclicBarrier(2);
    private final List<String> list = new ArrayList<>();

    {
        list.add("Прошу вас! Вы первый!");
        list.add("Нет! Вы первый!");
        list.add("Только после вас!");
        list.add("Умоляю, проходите первым!");
        list.add("Что вы, я настаиваю — вы!");
        list.add("Ни за что, сначала вы!");
        list.add("Давайте вместе?");
        list.add("Нет-нет, я подожду.");
        list.add("Входите, я не спешу.");
        list.add("Вы оказались здесь раньше — вам и первому!");
        list.add("Я настаиваю, прошу вас!");
        list.add("Не смею вас задерживать!");
        list.add("Будьте так любезны, проходите!");
        list.add("Извините, вы, кажется, торопитесь — вам первым!");
        list.add("Позвольте мне уступить вам дорогу!");
        list.add("Только через мой труп — проходите вы!");
        list.add("О, я могу и подождать, право слово!");
        list.add("Да что вы, это честь для меня — уступить вам!");
        list.add("Спор бесконечен... Может, всё-таки вы?");
    }

    public Worker(String message, Role role, Printer printer) {
        this.message = message;
        this.role = role;
        this.printer = printer;
    }

    public void printMessage(Worker other) {
        while (true) {
            setMyIntent(true);
            if (printer.getOwner() != this) {
                try {
                    Thread.sleep(100);
                    continue;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (other.getOtherIntent()) {
                printer.setOwner(other);
                System.out.println(Thread.currentThread().getName() + ": "
                        + list.get(ThreadLocalRandom.current().nextInt(list.size())));
                System.out.println();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }
            printer.print(message);
            setMyIntent(false);
        }
    }

    private void setMyIntent(boolean choice) {
        switch (role) {
            case THREAD_ONE -> intent.threadOneIntent = choice;
            case THREAD_TWO -> intent.threadTwoIntent = choice;
        }
    }

    public boolean getOtherIntent() {
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
