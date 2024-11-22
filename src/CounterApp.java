import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CounterApp {
    private static final Lock lock = new ReentrantLock();
    private static final Condition thread1Done = lock.newCondition();
    private static boolean isThread1Complete = false;

    public static void main(String[] args) {
        Thread countUpThread = new Thread(() -> {
            lock.lock();
            try {
                for (int i = 1; i <= 20; i++) {
                    System.out.println("Counting up for thread 1: " + i);
                }
                isThread1Complete = true;
                thread1Done.signal(); // this is to notify thread 2
            } finally {
                lock.unlock();
            }
        });

        Thread countDownThread = new Thread(() -> {
            lock.lock();
            try {
                while (!isThread1Complete) {
                    thread1Done.await(); // this is intended wait while thread 1 finishes
                }
                for (int i = 20; i >= 0; i--) {
                    System.out.println("Counting down for thread 1: " + i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        });

        countUpThread.start();
        countDownThread.start();
    }
}

