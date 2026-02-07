/*
 =============================================================================
 FILE: LocksAndAtomicNotes.java

 PHASE 5: LOCKS & ATOMIC CLASSES (ADVANCED)

 WHY INTERVIEWERS CARE:
 ---------------------
 - Concurrency bugs are hard to detect
 - Lock choice affects performance, fairness, deadlocks
 - Atomic classes show deep JVM + CPU understanding

 =============================================================================
*/

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class LocksAndAtomicNotes {

    /*
     ============================================================================
     1️⃣ synchronized (IMPLICIT LOCK)
     ----------------------------------------------------------------------------
     - Built-in Java locking mechanism
     - Lock is acquired implicitly
     - Lock released automatically when block exits
     - NO tryLock
     - NO fairness
     - Simpler but less flexible
     ============================================================================
    */

    private int syncCount = 0;

    // synchronized METHOD
    synchronized void incrementWithSynchronized() {
        syncCount++;
        // Thread automatically acquires object-level monitor lock
        // Lock is released when method exits (even if exception occurs)
    }

    /*
     ============================================================================
     2️⃣ ReentrantLock (EXPLICIT LOCK)
     ----------------------------------------------------------------------------
     - Introduced in Java 5
     - Explicit lock management
     - MUST unlock manually
     - Supports:
         ✔ tryLock()
         ✔ fairness
         ✔ interruptible locking
         ✔ multiple condition variables
     ============================================================================
    */

    private int lockCount = 0;

    // Fair lock (true = fairness enabled)
    private final ReentrantLock lock = new ReentrantLock(true);

    void incrementWithReentrantLock() {

        /*
         ------------------------------------------------------------------------
         tryLock()
         ------------------------------------------------------------------------
         - Attempts to acquire lock without blocking
         - Returns true if lock acquired
         - Avoids deadlocks
         ------------------------------------------------------------------------
        */
        if (lock.tryLock()) {
            try {
                lockCount++;
            } finally {
                // MUST unlock manually
                lock.unlock();
            }
        } else {
            System.out.println("Lock not available, skipping task");
        }
    }

    /*
     ============================================================================
     synchronized vs ReentrantLock (INTERVIEW COMPARISON)
     ============================================================================
     synchronized:
       - Implicit
       - No tryLock
       - No fairness
       - Simple syntax

     ReentrantLock:
       - Explicit
       - tryLock available
       - Fair / Unfair locking
       - More control, more responsibility
     ============================================================================
    */

    /*
     ============================================================================
     3️⃣ ATOMIC CLASSES ⭐⭐⭐
     ----------------------------------------------------------------------------
     - Found in java.util.concurrent.atomic
     - Examples:
         AtomicInteger
         AtomicLong
         AtomicBoolean
         AtomicReference
     - Thread-safe WITHOUT using locks
     - Uses CAS internally
     ============================================================================
    */

    static AtomicInteger atomicCount = new AtomicInteger(0);

    static void atomicIncrement() {

        /*
         ------------------------------------------------------------------------
         incrementAndGet()
         ------------------------------------------------------------------------
         - Atomically increments value
         - No synchronized
         - No explicit lock
         - Lock-free and fast
         ------------------------------------------------------------------------
        */
        atomicCount.incrementAndGet();
    }

    /*
     ============================================================================
     4️⃣ CAS (COMPARE AND SWAP) ⭐⭐⭐ VERY IMPORTANT
     ----------------------------------------------------------------------------
     CAS is a low-level CPU instruction used for optimistic locking

     STEPS:
       1. Read current value (expected value)
       2. Compare with memory value
       3. If same → update
       4. If not same → retry
     ============================================================================
    */

    static void casExplanation() {

        /*
         PSEUDO CODE OF CAS:

         while (true) {
             int expected = currentValue;
             int newValue = expected + 1;

             if (compareAndSwap(expected, newValue)) {
                 break; // success
             }
             // else retry
         }
        */

        // AtomicInteger internally does this CAS loop
        atomicCount.incrementAndGet();
    }

    /*
     ============================================================================
     CAS vs LOCKING (INTERVIEW GOLD)
     ============================================================================
     CAS:
       ✔ Lock-free
       ✔ Non-blocking
       ✔ Faster under low contention
       ❌ Can spin (retry loop)

     Locks:
       ✔ Simple mental model
       ✔ Good under high contention
       ❌ Thread blocking + context switching
     ============================================================================
    */

    /*
     ============================================================================
     MAIN METHOD (DEMO ONLY)
     ============================================================================
    */

    public static void main(String[] args) throws InterruptedException {

        LocksAndAtomicNotes demo = new LocksAndAtomicNotes();

        // synchronized example
        demo.incrementWithSynchronized();

        // ReentrantLock example
        demo.incrementWithReentrantLock();

        // Atomic example
        atomicIncrement();

        System.out.println("Atomic Count: " + atomicCount.get());

        /*
         ------------------------------------------------------------------------
         KEY INTERVIEW TAKEAWAY
         ------------------------------------------------------------------------
         synchronized → simplicity
         ReentrantLock → flexibility
         Atomic → performance with CAS
         ------------------------------------------------------------------------
        */
    }
}

/*
 =============================================================================
 INTERVIEW ONE-LINERS (MUST MEMORIZE)
 ----------------------------------

 ✔ synchronized:
   "Implicit locking with automatic unlock."

 ✔ ReentrantLock:
   "Explicit lock with tryLock and fairness support."

 ✔ Atomic classes:
   "Provide thread safety using CAS without locks."

 ✔ CAS:
   "Optimistic locking using CPU compare-and-swap instructions."

 ✔ Why Atomic is faster?
   "Avoids context switching and blocking."

 =============================================================================
*/