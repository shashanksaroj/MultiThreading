/*
 =============================================================================
 FILE: DeadlockAndLivenessNotes.java

 PHASE 8: DEADLOCK, LIVENESS & PITFALLS 🔥

 WHY INTERVIEWERS CARE:
 ---------------------
 - Deadlocks can bring production systems to a halt
 - Liveness issues are subtle and hard to debug
 - Senior engineers must prevent, not just detect

 =============================================================================
*/

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class DeadlockAndLivenessNotes {

    /*
     ============================================================================
     2️⃣1️⃣ DEADLOCK ⭐⭐⭐
     ----------------------------------------------------------------------------
     WHAT IS DEADLOCK?
     ----------------------------------------------------------------------------
     - Two or more threads waiting forever for each other’s locks
     - No thread can make progress
     - System appears "frozen"
     ============================================================================
    */

    /*
     ============================================================================
     CAUSE: CIRCULAR LOCK DEPENDENCY
     ----------------------------------------------------------------------------
     Thread-1 holds Lock-A → waits for Lock-B
     Thread-2 holds Lock-B → waits for Lock-A
     ============================================================================
    */

    private final Object lockA = new Object();
    private final Object lockB = new Object();

    void deadlockExample() {

        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("Thread-1 acquired Lock-A");
                sleep();

                synchronized (lockB) {
                    System.out.println("Thread-1 acquired Lock-B");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lockB) {
                System.out.println("Thread-2 acquired Lock-B");
                sleep();

                synchronized (lockA) {
                    System.out.println("Thread-2 acquired Lock-A");
                }
            }
        });

        t1.start();
        t2.start();
    }

    /*
     ============================================================================
     DEADLOCK PREVENTION TECHNIQUES ⭐⭐⭐
     ============================================================================
    */

    /*
     ============================================================================
     1️⃣ LOCK ORDERING (BEST PRACTICE)
     ----------------------------------------------------------------------------
     - Always acquire locks in the same order
     - Eliminates circular dependency
     ============================================================================
    */

    void lockOrderingPrevention() {

        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                synchronized (lockB) {
                    System.out.println("Thread-1 safe execution");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lockA) {   // SAME ORDER
                synchronized (lockB) {
                    System.out.println("Thread-2 safe execution");
                }
            }
        });

        t1.start();
        t2.start();
    }

    /*
     ============================================================================
     2️⃣ TIMEOUT (tryLock)
     ----------------------------------------------------------------------------
     - Avoids infinite waiting
     - Allows fallback logic
     ============================================================================
    */

    private final ReentrantLock lock1 = new ReentrantLock();
    private final ReentrantLock lock2 = new ReentrantLock();

    void timeoutPrevention() {

        Thread t1 = new Thread(() -> {
            try {
                if (lock1.tryLock(1, TimeUnit.SECONDS)) {
                    try {
                        if (lock2.tryLock(1, TimeUnit.SECONDS)) {
                            try {
                                System.out.println("Thread-1 acquired both locks");
                            } finally {
                                lock2.unlock();
                            }
                        }
                    } finally {
                        lock1.unlock();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        t1.start();
    }

    /*
     ============================================================================
     3️⃣ AVOID NESTED LOCKS
     ----------------------------------------------------------------------------
     - Acquire one lock at a time
     - Reduce lock scope
     ============================================================================
    */

    /*
     ============================================================================
     2️⃣2️⃣ STARVATION vs DEADLOCK ⭐⭐⭐
     ============================================================================
    */

    /*
     ============================================================================
     DEADLOCK
     ----------------------------------------------------------------------------
     - Threads are permanently blocked
     - No thread progresses
     - Needs external intervention
     ============================================================================
    */

    /*
     ============================================================================
     STARVATION
     ----------------------------------------------------------------------------
     - Thread is alive but never gets CPU or lock
     - Caused by:
         ❌ Unfair locks
         ❌ Thread priority misuse
         ❌ Long-running synchronized blocks
     ============================================================================
    */

    static class StarvationExample {

        private final ReentrantLock unfairLock = new ReentrantLock(false); // unfair

        void demonstrateStarvation() {

            Runnable task = () -> {
                while (true) {
                    unfairLock.lock();
                    try {
                        System.out.println(Thread.currentThread().getName()
                                + " acquired lock");
                    } finally {
                        unfairLock.unlock();
                    }
                }
            };

            new Thread(task, "Low-Priority-Thread").start();
            new Thread(task, "High-Priority-Thread").start();
        }
    }

    /*
     ============================================================================
     DEADLOCK vs STARVATION (INTERVIEW COMPARISON)
     ============================================================================
     Deadlock:
       - Threads stuck forever
       - Circular dependency
       - No progress

     Starvation:
       - Thread waits indefinitely
       - Other threads keep progressing
       - System still running
     ============================================================================
    */

    /*
     ============================================================================
     MAIN METHOD (DEMO)
     ============================================================================
    */

    public static void main(String[] args) {

        DeadlockAndLivenessNotes demo = new DeadlockAndLivenessNotes();

        // ⚠ Uncomment one at a time for demonstration
        // demo.deadlockExample();
        // demo.lockOrderingPrevention();
        // demo.timeoutPrevention();

        StarvationExample starvation = new StarvationExample();
        // starvation.demonstrateStarvation();
    }

    private static void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

/*
 =============================================================================
 INTERVIEW ONE-LINERS (MUST MEMORIZE)
 ----------------------------------

 ✔ Deadlock:
   "Circular lock dependency where threads wait forever."

 ✔ Deadlock prevention:
   "Lock ordering, timeouts, and avoiding nested locks."

 ✔ Starvation:
   "Thread never gets CPU or lock but system keeps running."

 ✔ Deadlock vs Starvation ⭐⭐⭐:
   "Deadlock stops the system; starvation degrades fairness."

 =============================================================================
*/