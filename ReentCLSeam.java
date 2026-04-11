// File: ConcurrencyNotes.java

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/*
===========================================
🔥 CONCURRENCY NOTES (INTERVIEW READY)
===========================================

1. ReentrantLock (ROOM KEY ANALOGY 🗝️)
---------------------------------------
- One room, one key
- Same person can take key multiple times (reentrant)
- Prevents deadlock in nested calls

RULE:
lock() → lock() → unlock() → unlock()

Extra:
- tryLock()
- fairness
- interruptible lock

---------------------------------------

2. CountDownLatch (DOOR ANALOGY 🚪)
---------------------------------------
- Door opens when N people arrive

count = 3 → 2 → 1 → 0 → GO 🚀

- await() → wait
- countDown() → reduce

NOTE:
- One-time use only

---------------------------------------

3. Semaphore (PARKING LOT ANALOGY 🅿️)
---------------------------------------
- Limited parking slots
- Only N cars allowed at same time

permits = 2
→ Only 2 threads allowed
→ Others wait

METHODS:
- acquire() → take slot
- release() → free slot

===========================================
*/

public class ConcurrencyNotes {

    /*
    ===========================================
    🔥 REENTRANT LOCK EXAMPLE
    ===========================================
    */
    private final ReentrantLock lock = new ReentrantLock();

    public void outerMethod() {
        lock.lock();
        try {
            System.out.println("Outer method - lock acquired");
            innerMethod();
        } finally {
            lock.unlock();
        }
    }

    public void innerMethod() {
        lock.lock();
        try {
            System.out.println("Inner method - reentered lock");
        } finally {
            lock.unlock();
        }
    }

    /*
    ===========================================
    🔥 COUNTDOWN LATCH EXAMPLE
    ===========================================
    */
    public void runLatchExample() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(3);

        Runnable service = () -> {
            System.out.println(Thread.currentThread().getName() + " starting...");
            try { Thread.sleep(1000); } catch (Exception e) {}
            System.out.println(Thread.currentThread().getName() + " finished");
            latch.countDown();
        };

        new Thread(service).start();
        new Thread(service).start();
        new Thread(service).start();

        System.out.println("Main thread waiting...");
        latch.await();

        System.out.println("All services completed 🚀");
    }

    /*
    ===========================================
    🔥 SEMAPHORE EXAMPLE
    ===========================================
    */
    public void runSemaphoreExample() {

        Semaphore semaphore = new Semaphore(2); // only 2 permits

        Runnable task = () -> {
            try {
                System.out.println(Thread.currentThread().getName() + " waiting for permit...");

                semaphore.acquire(); // take permit

                System.out.println(Thread.currentThread().getName() + " got permit ✅");

                Thread.sleep(2000); // simulate work

                System.out.println(Thread.currentThread().getName() + " releasing permit ❌");

                semaphore.release(); // release permit

            } catch (Exception e) {}
        };

        // 5 threads competing for 2 permits
        for (int i = 0; i < 5; i++) {
            new Thread(task).start();
        }
    }

    /*
    ===========================================
    🔥 MAIN METHOD
    ===========================================
    */
    public static void main(String[] args) throws InterruptedException {

        ConcurrencyNotes obj = new ConcurrencyNotes();

        System.out.println("\n===== ReentrantLock Demo =====");
        obj.outerMethod();

        System.out.println("\n===== CountDownLatch Demo =====");
        obj.runLatchExample();

        System.out.println("\n===== Semaphore Demo =====");
        obj.runSemaphoreExample();
    }
}
